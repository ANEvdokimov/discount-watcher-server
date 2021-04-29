package an.evdokimov.discount.watcher.server.api.error;

public enum ServerErrorCode {
    USER_ALREADY_EXISTS("A user with this login already exists."),
    WRONG_LOGIN_OR_PASSWORD("Wrong login or password."),
    SHOP_NOT_FOUND("A shop with this id is not exist."),
    UNSUPPORTED_SHOP("The url is incorrect or unsupported shop"),
    PAGE_DOWNLOAD_ERROR("Unable to download page."),
    PARSE_PAGE_ERROR("Error in parsing page");

    private final String message;

    ServerErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
