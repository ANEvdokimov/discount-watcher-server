package an.evdokimov.discount.watcher.server.api.error;

import lombok.Getter;

public enum ServerErrorCode {
    USER_ALREADY_EXISTS("A user with this login already exists."),
    WRONG_LOGIN_OR_PASSWORD("Wrong login or password."),
    SHOP_NOT_FOUND("A shop does not exist."),
    UNSUPPORTED_SHOP("The url is incorrect or unsupported shop"),
    PAGE_DOWNLOAD_ERROR("Unable to download page."),
    PARSE_PAGE_ERROR("Error in parsing page"),
    PRODUCT_NOT_FOUND("A product with this id not found."),
    USER_PRODUCT_NOT_FOUND("A user's product by these parameters not found."),
    PRODUCT_PRICE_NOT_FOUND("Product price with this id not found"),
    PRODUCT_INFORMATION_NOT_FOUND("Product information with this id not found"),
    PARSE_RESPONSE_ID_ERROR("ProductInformationId and ProductPriceId can not be null.");

    @Getter
    private final String message;

    ServerErrorCode(String message) {
        this.message = message;
    }

    public void throwException() throws ServerException {
        throw new ServerException(this);
    }

    public void throwException(String details) throws ServerException {
        throw new ServerException(this, details);
    }

    public ServerException getException() {
        return new ServerException(this);
    }

    public ServerException getException(String details) {
        return new ServerException(this, details);
    }
}
