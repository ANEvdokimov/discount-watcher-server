package an.evdokimov.discount.watcher.server.parser;

import lombok.Getter;

public class ParserException extends Exception {
    @Getter
    private final ParserErrorCode errorCode;

    public ParserException(ParserErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ParserException(ParserErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public ParserException(ParserErrorCode errorCode, String message) {
        super(errorCode.getMessage() + " - " + message);
        this.errorCode = errorCode;
    }

    public ParserException(ParserErrorCode errorCode, String message, Throwable cause) {
        super(errorCode.getMessage() + " - " + message, cause);
        this.errorCode = errorCode;
    }
}
