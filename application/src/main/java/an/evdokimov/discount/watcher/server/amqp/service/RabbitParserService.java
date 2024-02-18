package an.evdokimov.discount.watcher.server.amqp.service;

import an.evdokimov.discount.watcher.server.amqp.dto.ParsedProductInformation;
import an.evdokimov.discount.watcher.server.amqp.dto.ParsingErrorMessage;
import an.evdokimov.discount.watcher.server.amqp.dto.ProductForParsing;
import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.maintenance.ProductMaintenance;
import an.evdokimov.discount.watcher.server.configuration.property.RabbitProperties;
import an.evdokimov.discount.watcher.server.database.product.model.ParsingStatus;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.repository.ParsingErrorRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductInformationRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductPriceRepository;
import an.evdokimov.discount.watcher.server.mapper.product.ParsingErrorMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class RabbitParserService implements ParserService {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties rabbitProperties;
    private final ProductPriceRepository priceRepository;
    private final ProductInformationRepository informationRepository;
    private final ParsingErrorRepository parsingErrorRepository;
    private final ParsingErrorMapper errorMapper;
    private final ProductMaintenance productMaintenance;

    public RabbitParserService(RabbitTemplate rabbitTemplate,
                               RabbitProperties rabbitProperties,
                               ProductPriceRepository priceRepository,
                               ProductInformationRepository informationRepository,
                               ParsingErrorRepository parsingErrorRepository,
                               ParsingErrorMapper errorMapper,
                               @Lazy ProductMaintenance productService) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitProperties = rabbitProperties;
        this.priceRepository = priceRepository;
        this.informationRepository = informationRepository;
        this.parsingErrorRepository = parsingErrorRepository;
        this.errorMapper = errorMapper;
        this.productMaintenance = productService;
    }

    @Override
    public void parseProduct(ProductForParsing product) {
        log.info("Product {} was sent to Product Passer Service", product);
        rabbitTemplate.convertAndSend(rabbitProperties.getInputQueueName(), product);
    }

    @Override
    @RabbitListener(queues = "#{outputQueue.name}", errorHandler = "#{parsingErrorHandler}")
    @Transactional
    public void handleParsedProductPrice(ParsedProductInformation parsedProduct) throws ServerException {
        log.info("Parsed Product Price {} was received from Product Passer Service", parsedProduct);

        if (parsedProduct.getId() == null || parsedProduct.getProductPrice().getId() == null) {
            ServerErrorCode.PARSE_RESPONSE_ID_ERROR.throwException(parsedProduct.toString());
        } else {
            productMaintenance.saveParsedProduct(parsedProduct);
        }
    }

    @Override
    @RabbitListener(queues = "#{outputErrorQueue.name}", errorHandler = "#{unhandledParsingErrorHandler}")
    @Transactional
    public void handleParsingError(ParsingErrorMessage error) {
        log.error("Parsing error {} was received from Product Parser queue", error);

        ProductInformation information = error.getProductInformationId() != null ?
                informationRepository.findById(error.getProductInformationId()).orElse(null) : null;
        ProductPrice price = error.getProductPriceId() != null ?
                priceRepository.findById(error.getProductPriceId()).orElse(null) : null;

        if (information != null) {
            information.setParsingStatus(ParsingStatus.ERROR);
        }
        if (price != null) {
            price.setParsingStatus(ParsingStatus.ERROR);
        }

        parsingErrorRepository.save(errorMapper.map(error.getMessage(), information, price));
    }


    @Component("parsingErrorHandler")
    @Slf4j
    @RequiredArgsConstructor
    static class ParsingErrorHandler implements RabbitListenerErrorHandler {
        private final RabbitTemplate rabbitTemplate;
        private final RabbitProperties rabbitProperties;

        @Override
        public Object handleError(Message amqpMessage,
                                  org.springframework.messaging.Message<?> message,
                                  ListenerExecutionFailedException exception) throws Exception {
            String errorString = exception.getCause().getMessage();
            ParsingErrorMessage errorMessage;
            if (message != null) {
                ParsedProductInformation payload = (ParsedProductInformation) message.getPayload();
                errorMessage = new ParsingErrorMessage(payload.getProductPrice().getId(), payload.getId(), errorString);
            } else {
                errorMessage = new ParsingErrorMessage(null, null, errorString);
            }

            log.warn("Error message {} was sent to the parsing error queue.", errorMessage);
            rabbitTemplate.convertAndSend(
                    rabbitProperties.getOutputErrorQueueName(),
                    errorMessage
            );

            return null;
        }
    }

    @Component("unhandledParsingErrorHandler")
    @Slf4j
    static class UnhandledParsingErrorHandler implements RabbitListenerErrorHandler {
        @Override
        public Object handleError(Message amqpMessage,
                                  org.springframework.messaging.Message<?> message,
                                  ListenerExecutionFailedException exception) throws Exception {
            log.error("The parsing error message was not handled. Message: {}. Details: {}.",
                    amqpMessage,
                    message != null ? message.getPayload() : "null",
                    exception);
            return null;
        }
    }
}
