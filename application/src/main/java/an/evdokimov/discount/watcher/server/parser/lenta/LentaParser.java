package an.evdokimov.discount.watcher.server.parser.lenta;

import an.evdokimov.discount.watcher.server.database.product.model.LentaProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.parser.Parser;
import an.evdokimov.discount.watcher.server.parser.ParserErrorCode;
import an.evdokimov.discount.watcher.server.parser.ParserException;
import an.evdokimov.discount.watcher.server.parser.downloader.PageDownloader;
import an.evdokimov.discount.watcher.server.parser.downloader.PageDownloaderException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class LentaParser implements Parser {
    private static final String HOST_NAME = "lenta.com";
    private final PageDownloader pageDownloader;
    private final Clock clock;
    private final ObjectMapper mapper;

    private static final String SELECTOR_PRODUCT_DATA = "body > div.scroll-lock > div.scroll-lock__inner.page-wrap > " +
            "div > main > article > div > div > div.sku-page.js-validation-popup.js-ecommerce-sku-card > " +
            "div.sku-page__head-info > div.sku-page__info > div.sku-page__main-info > " +
            "div.sku-page-control-container.sku-page__control";
    private static final String HTML_DATA_ATTRIBUTE = "data-model";

    public LentaParser(PageDownloader pageDownloader, Clock clock, ObjectMapper mapper) {
        this.pageDownloader = pageDownloader;
        this.clock = clock;
        this.mapper = mapper;
    }

    @Override
    public String getSupportedUrl() {
        return HOST_NAME;
    }

    @Override
    public Product parse(@NotNull URL url, @NotNull Shop shop) throws PageDownloaderException, ParserException {
        LentaProductFromPage productFromPage = downloadProduct(url, shop);

        ProductInformation productInformation = ProductInformation.builder()
                .url(url)
                .name(productFromPage.getTitle() + ", " + productFromPage.getSubTitle())
                .build();

        return createProduct(productFromPage, productInformation, shop);
    }

    @Override
    public Product parse(@NotNull ProductInformation productInformation, @NotNull Shop shop)
            throws PageDownloaderException, ParserException {
        LentaProductFromPage productFromPage = downloadProduct(productInformation.getUrl(), shop);

        return createProduct(productFromPage, productInformation, shop);
    }

    @Override
    public LentaProductPrice parse(@NotNull Product product) throws ParserException, PageDownloaderException {
        LentaProductFromPage productFromPage =
                downloadProduct(product.getProductInformation().getUrl(), product.getShop());

        LentaProductPrice lentaProductPrice = createProductPrice(productFromPage);
        lentaProductPrice.setProduct(product);

        return lentaProductPrice;
    }

    private void validateUrl(@NotNull URL url) throws ParserException {
        if (!url.getHost().equals(HOST_NAME)) {
            log.warn("wrong host. url={}", url);
            throw new ParserException(ParserErrorCode.WRONG_SHOP_URL);
        }
    }

    private LentaProductFromPage downloadProduct(URL url, Shop shop) throws ParserException, PageDownloaderException {
        validateUrl(url);

        Document page = pageDownloader.downloadPage(url, shop.getCookie());

        try {
            return mapper.readValue(
                    selectElement(page, SELECTOR_PRODUCT_DATA).attr(HTML_DATA_ATTRIBUTE),
                    LentaProductFromPage.class
            );
        } catch (JsonProcessingException e) {
            log.warn("wrong json format. url={}", url);
            throw new ParserException(ParserErrorCode.UNKNOWN_DATA_FORMAT, e);
        }
    }

    private Element selectElement(@NotNull Document page, @NotNull String cssSelector) throws ParserException {
        Elements elements = page.select(cssSelector);
        if (elements.size() == 0) {
            log.warn("wrong ccs-selector (element not found)");
            throw new ParserException(ParserErrorCode.ELEMENT_NOT_FOUND, "selector=" + cssSelector);
        }
        if (elements.size() != 1) {
            log.warn("wrong ccs-selector (more then one element)");
            throw new ParserException(ParserErrorCode.AMBIGUOUS_SEARCH_RESULT, "selector=" + cssSelector);
        }
        return elements.get(0);
    }

    private String getAvailabilityInformation(int stock) {
        return switch (stock) {
            case 0 -> "Товар закончился";
            case 1 -> "Товар заканчивается";
            case 2 -> "Товара достаточно";
            case 3 -> "Товара много";
            default -> "unknown stock code: " + stock;
        };
    }

    private Product createProduct(LentaProductFromPage productFromPage, ProductInformation productInformation,
                                  Shop shop)
            throws ParserException {
        LentaProductPrice lentaProductPrice = createProductPrice(productFromPage);

        Product product = Product.builder()
                .shop(shop)
                .productInformation(productInformation)
                .prices(List.of(lentaProductPrice))
                .build();

        lentaProductPrice.setProduct(product);

        return product;
    }

    private LentaProductPrice createProductPrice(LentaProductFromPage productFromPage) throws ParserException {
        Double discount;
        BigDecimal priceWithDiscount;
        try {
            if (productFromPage.isHasDiscount()) {
                discount = Double.parseDouble(productFromPage.getPromoPercent());
                priceWithDiscount = productFromPage.getCardPrice().getValue();
            } else {
                discount = null;
                priceWithDiscount = null;
            }
        } catch (NumberFormatException e) {
            log.warn("wrong number format. productFromPage.promoPercent={}, productFromPage.cardPrice={}",
                    productFromPage.getPromoPercent(), productFromPage.getCardPrice());
            throw new ParserException(ParserErrorCode.WRONG_NUMBER_FORMAT, e);
        }

        return LentaProductPrice.builder()
                .price(productFromPage.getRegularPrice().getValue())
                .priceWithCard(productFromPage.getCardPrice().getValue())
                .discount(discount)
                .priceWithDiscount(priceWithDiscount)
                .isInStock(productFromPage.getStock() != 0)
                .availabilityInformation(getAvailabilityInformation(productFromPage.getStock()))
                .date(LocalDateTime.now(clock))
                .build();
    }
}
