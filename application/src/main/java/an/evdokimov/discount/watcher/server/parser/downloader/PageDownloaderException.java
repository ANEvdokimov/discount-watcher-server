package an.evdokimov.discount.watcher.server.parser.downloader;

public class PageDownloaderException extends Exception {
    public PageDownloaderException() {
        super();
    }

    public PageDownloaderException(String message) {
        super(message);
    }

    public PageDownloaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public PageDownloaderException(Throwable cause) {
        super(cause);
    }

    protected PageDownloaderException(String message, Throwable cause, boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
