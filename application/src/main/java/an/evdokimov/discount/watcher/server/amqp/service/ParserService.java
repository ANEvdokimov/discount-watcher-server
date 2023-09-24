package an.evdokimov.discount.watcher.server.amqp.service;

import an.evdokimov.discount.watcher.server.amqp.dto.ParsedProductInformation;
import an.evdokimov.discount.watcher.server.amqp.dto.ParsingErrorMessage;
import an.evdokimov.discount.watcher.server.amqp.dto.ProductForParsing;
import an.evdokimov.discount.watcher.server.api.error.ServerException;

public interface ParserService {
    void parseProduct(ProductForParsing product);

    void handleParsedProductPrice(ParsedProductInformation productPrice) throws ServerException;

    void handleParsingError(ParsingErrorMessage errorMessage);
}
