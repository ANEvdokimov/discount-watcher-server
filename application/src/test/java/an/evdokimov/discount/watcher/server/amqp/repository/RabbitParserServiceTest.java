package an.evdokimov.discount.watcher.server.amqp.repository;

import an.evdokimov.discount.watcher.server.amqp.dto.ParsedProductInformation;
import an.evdokimov.discount.watcher.server.amqp.dto.ParsedProductPrice;
import an.evdokimov.discount.watcher.server.amqp.dto.ParsingErrorMessage;
import an.evdokimov.discount.watcher.server.amqp.dto.ProductForParsing;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.configuration.property.RabbitProperties;
import an.evdokimov.discount.watcher.server.database.product.model.ParsingError;
import an.evdokimov.discount.watcher.server.database.product.model.ParsingStatus;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.repository.ParsingErrorRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductInformationRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductPriceRepository;
import an.evdokimov.discount.watcher.server.mapper.product.ParsedProductPriceMapper;
import an.evdokimov.discount.watcher.server.mapper.product.ParsingErrorMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {RabbitParserService.class, RabbitProperties.class})
class RabbitParserServiceTest {
    @MockBean
    private RabbitTemplate rabbitTemplate;
    @MockBean
    private ProductPriceRepository productPriceRepository;
    @MockBean
    private ProductInformationRepository productInformationRepository;
    @MockBean
    private ParsingErrorRepository parsingErrorRepository;
    @MockBean
    private ParsedProductPriceMapper parsedProductPriceMapper;
    @MockBean
    private ParsingErrorMapper parsingErrorMapper;

    @Autowired
    private RabbitParserService testedRabbitParserService;
    @Autowired
    private RabbitProperties rabbitProperties;

    @Test
    void parseProduct_product_sentToQueue() {
        ProductForParsing mockedProduct = new ProductForParsing();

        testedRabbitParserService.parseProduct(mockedProduct);

        verify(rabbitTemplate).convertAndSend(rabbitProperties.getInputQueueName(), mockedProduct);
    }

    @Test
    void handleParsedProductPrice_validResponse_saveProduct() throws ServerException {
        ParsedProductPrice mockedParsedPrice = new ParsedProductPrice(
                666L,
                BigDecimal.TEN,
                1.0,
                BigDecimal.TWO,
                false,
                "yes",
                LocalDateTime.now()
        );
        ParsedProductInformation mockedParsedInformation = new ParsedProductInformation(
                1L,
                "product_name",
                mockedParsedPrice
        );

        ProductPrice mockedPrice = ProductPrice.builder()
                .id(mockedParsedPrice.getId())
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();

        when(productPriceRepository.findById(mockedParsedPrice.getId())).thenReturn(Optional.of(mockedPrice));
        when(productInformationRepository.updateNameById(
                mockedParsedInformation.getId(),
                mockedParsedInformation.getName())
        ).thenReturn(1);

        testedRabbitParserService.handleParsedProductPrice(mockedParsedInformation);

        verify(productPriceRepository).save(mockedPrice);
        verify(productInformationRepository)
                .updateNameById(mockedParsedInformation.getId(), mockedParsedInformation.getName());
    }

    @Test
    void handleParsedProductPrice_nullPriceId_ServerException() {
        ParsedProductPrice mockedParsedPrice = new ParsedProductPrice(
                null,
                BigDecimal.TEN,
                1.0,
                BigDecimal.TWO,
                false,
                "yes",
                LocalDateTime.now()
        );
        ParsedProductInformation mockedParsedInformation = new ParsedProductInformation(
                1L,
                "product_name",
                mockedParsedPrice
        );

        ProductPrice mockedPrice = ProductPrice.builder()
                .id(mockedParsedPrice.getId())
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();


        assertThrows(
                ServerException.class,
                () -> testedRabbitParserService.handleParsedProductPrice(mockedParsedInformation)
        );
        verify(productPriceRepository, never()).save(mockedPrice);
        verify(productInformationRepository, never())
                .updateNameById(mockedParsedInformation.getId(), mockedParsedInformation.getName());
    }

    @Test
    void handleParsedProductPrice_nullInformationId_ServerException() {
        ParsedProductPrice mockedParsedPrice = new ParsedProductPrice(
                666L,
                BigDecimal.TEN,
                1.0,
                BigDecimal.TWO,
                false,
                "yes",
                LocalDateTime.now()
        );
        ParsedProductInformation mockedParsedInformation = new ParsedProductInformation(
                null,
                "product_name",
                mockedParsedPrice
        );

        ProductPrice mockedPrice = ProductPrice.builder()
                .id(mockedParsedPrice.getId())
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();


        assertThrows(
                ServerException.class,
                () -> testedRabbitParserService.handleParsedProductPrice(mockedParsedInformation)
        );
        verify(productPriceRepository, never()).save(mockedPrice);
        verify(productInformationRepository, never())
                .updateNameById(mockedParsedInformation.getId(), mockedParsedInformation.getName());
    }

    @Test
    void handleParsedProductPrice_nullIds_ServerException() {
        ParsedProductPrice mockedParsedPrice = new ParsedProductPrice(
                null,
                BigDecimal.TEN,
                1.0,
                BigDecimal.TWO,
                false,
                "yes",
                LocalDateTime.now()
        );
        ParsedProductInformation mockedParsedInformation = new ParsedProductInformation(
                null,
                "product_name",
                mockedParsedPrice
        );

        ProductPrice mockedPrice = ProductPrice.builder()
                .id(mockedParsedPrice.getId())
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();


        assertThrows(
                ServerException.class,
                () -> testedRabbitParserService.handleParsedProductPrice(mockedParsedInformation)
        );
        verify(productPriceRepository, never()).save(mockedPrice);
        verify(productInformationRepository, never())
                .updateNameById(mockedParsedInformation.getId(), mockedParsedInformation.getName());
    }

    @Test
    void handleParsedProductPrice_nonexistentPrice_ServerException() {
        ParsedProductPrice mockedParsedPrice = new ParsedProductPrice(
                666L,
                BigDecimal.TEN,
                1.0,
                BigDecimal.TWO,
                false,
                "yes",
                LocalDateTime.now()
        );
        ParsedProductInformation mockedParsedInformation = new ParsedProductInformation(
                1L,
                "product_name",
                mockedParsedPrice
        );

        ProductPrice mockedPrice = ProductPrice.builder()
                .id(mockedParsedPrice.getId())
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();

        when(productPriceRepository.findById(mockedParsedPrice.getId())).thenReturn(Optional.ofNullable(null));
        when(productInformationRepository.updateNameById(
                mockedParsedInformation.getId(),
                mockedParsedInformation.getName())
        ).thenReturn(1);

        assertThrows(
                ServerException.class,
                () -> testedRabbitParserService.handleParsedProductPrice(mockedParsedInformation)
        );
        verify(productPriceRepository, never()).save(mockedPrice);
        verify(productInformationRepository, never())
                .updateNameById(mockedParsedInformation.getId(), mockedParsedInformation.getName());
    }

    @Test
    void handleParsedProductPrice_nonexistentInformation_ServerException() {
        ParsedProductPrice mockedParsedPrice = new ParsedProductPrice(
                666L,
                BigDecimal.TEN,
                1.0,
                BigDecimal.TWO,
                false,
                "yes",
                LocalDateTime.now()
        );
        ParsedProductInformation mockedParsedInformation = new ParsedProductInformation(
                1L,
                "product_name",
                mockedParsedPrice
        );

        ProductPrice mockedPrice = ProductPrice.builder()
                .id(mockedParsedPrice.getId())
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();

        when(productPriceRepository.findById(mockedParsedPrice.getId())).thenReturn(Optional.of(mockedPrice));
        when(productInformationRepository.updateNameById(
                mockedParsedInformation.getId(),
                mockedParsedInformation.getName())
        ).thenReturn(0);

        assertThrows(
                ServerException.class,
                () -> testedRabbitParserService.handleParsedProductPrice(mockedParsedInformation)
        );
        verify(productPriceRepository).save(mockedPrice);
        verify(productInformationRepository)
                .updateNameById(mockedParsedInformation.getId(), mockedParsedInformation.getName());
    }

    @Test
    void handleParsingError_errorMessage_saveErrorMessage() {
        ProductPrice mockedPrice = ProductPrice.builder().id(666L).build();
        ProductInformation mockedInformation = ProductInformation.builder().id(6L).build();
        ParsingErrorMessage mockedParsedError = new ParsingErrorMessage(
                mockedPrice.getId(), mockedInformation.getId(), "error"
        );
        ParsingError mockedError = new ParsingError(
                null, mockedParsedError.getMessage(), mockedPrice, mockedInformation
        );

        when(productPriceRepository.findById(mockedPrice.getId())).thenReturn(Optional.of(mockedPrice));
        when(productInformationRepository.findById(mockedInformation.getId()))
                .thenReturn(Optional.of(mockedInformation));
        when(parsingErrorMapper.map(mockedParsedError.getMessage(), mockedInformation, mockedPrice))
                .thenReturn(mockedError);

        testedRabbitParserService.handleParsingError(mockedParsedError);

        verify(parsingErrorRepository).save(mockedError);
    }

    @Test
    void handleParsingError_nonexistentProduct_saveErrorMessage() {
        ParsingErrorMessage mockedParsedError = new ParsingErrorMessage(
                1L, 2L, "error"
        );
        ParsingError mockedError = new ParsingError(
                null, mockedParsedError.getMessage(), null, null
        );

        when(productPriceRepository.findById(mockedParsedError.getProductPriceId()))
                .thenReturn(Optional.ofNullable(null));
        when(productInformationRepository.findById(mockedParsedError.getProductInformationId()))
                .thenReturn(Optional.ofNullable(null));
        when(parsingErrorMapper.map(mockedParsedError.getMessage(), null, null))
                .thenReturn(mockedError);

        testedRabbitParserService.handleParsingError(mockedParsedError);

        verify(parsingErrorRepository).save(mockedError);
    }

    @Test
    void handleParsingError_errorMessageWithoutIds_saveErrorMessage() {
        ParsingErrorMessage mockedParsedError = new ParsingErrorMessage(null, null, "error");
        ParsingError mockedError = new ParsingError(null, mockedParsedError.getMessage(), null, null);

        when(parsingErrorMapper.map(mockedParsedError.getMessage(), null, null))
                .thenReturn(mockedError);

        testedRabbitParserService.handleParsingError(mockedParsedError);

        verify(parsingErrorRepository).save(mockedError);
    }
}