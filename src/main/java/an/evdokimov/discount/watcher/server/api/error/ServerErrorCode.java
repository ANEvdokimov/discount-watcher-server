package an.evdokimov.discount.watcher.server.api.error;

public enum ServerErrorCode {
    USER_ALREADY_EXISTS("A user with this login already exists.");

    private final String message;

    ServerErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
