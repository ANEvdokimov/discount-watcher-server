package an.evdokimov.discount.watcher.server.api.error;

public enum ServerErrorCode {
    USER_ALREADY_EXISTS("A user with this login already exists."),
    WRONG_LOGIN_OR_PASSWORD("Wrong login or password.");

    private final String message;

    ServerErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
