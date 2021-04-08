package an.evdokimov.discount.watcher.server.parser;

import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ParserFactory {
    private final Map<String, Parser> parsers;

    public ParserFactory(Set<Parser> parsers) {
        this.parsers = parsers.stream().collect(Collectors.toMap(Parser::getSupportedUrl, Function.identity()));
    }

    public Parser getParser(URL url) throws ParserFactoryException {
        Parser parser = parsers.get(url.getHost().toLowerCase(Locale.ENGLISH));
        if (parser == null) {
            throw new ParserFactoryException("Unsupported shop '" + url.getHost() + "'");
        }
        return parser;
    }
}
