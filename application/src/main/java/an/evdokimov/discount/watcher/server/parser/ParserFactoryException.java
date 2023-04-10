package an.evdokimov.discount.watcher.server.parser;

public class ParserFactoryException extends Exception {

    public ParserFactoryException() {
        super();
    }

    public ParserFactoryException(String message) {
        super(message);
    }

    public ParserFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParserFactoryException(Throwable cause) {
        super(cause);
    }

    protected ParserFactoryException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
