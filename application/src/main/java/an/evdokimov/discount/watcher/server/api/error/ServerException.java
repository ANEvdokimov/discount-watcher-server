package an.evdokimov.discount.watcher.server.api.error;

public class ServerException extends Exception {
    private final ServerErrorCode errorCode;

    public ServerException(ServerErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ServerException(ServerErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public ServerException(ServerErrorCode errorCode, String details) {
        super(errorCode.getMessage() + " Details: " + details);
        this.errorCode = errorCode;
    }

    public ServerException(ServerErrorCode errorCode, String details, Throwable cause) {
        super(errorCode.getMessage() + " Details: " + details, cause);
        this.errorCode = errorCode;
    }
}
