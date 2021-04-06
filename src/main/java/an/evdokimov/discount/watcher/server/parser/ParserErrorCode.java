package an.evdokimov.discount.watcher.server.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParserErrorCode {
    WRONG_SHOP_URL("Unsupported shop."),
    AMBIGUOUS_SEARCH_RESULT("More than one element found."),
    ELEMENT_NOT_FOUND("The element not found."),
    WRONG_NUMBER_FORMAT("Wrong number format.");

    private final String message;
}
