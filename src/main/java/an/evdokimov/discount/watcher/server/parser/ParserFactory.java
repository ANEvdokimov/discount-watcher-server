package an.evdokimov.discount.watcher.server.parser;

import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Locale;

@Component
public class ParserFactory {
    private final LentaParser lentaParser;

    public ParserFactory(LentaParser lentaParser) {
        this.lentaParser = lentaParser;
    }

    public Parser createParser(URL url) throws ParserFactoryException {
        switch (url.getHost().toLowerCase(Locale.ROOT)) {
            case "lenta.com":
                return lentaParser;
            default:
                throw new ParserFactoryException("Unsupported shop '" + url.getHost() + "'");
        }
    }
}
