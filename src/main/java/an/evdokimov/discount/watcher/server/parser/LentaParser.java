package an.evdokimov.discount.watcher.server.parser;

import an.evdokimov.discount.watcher.server.database.product.model.LentaProduct;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.parser.downloader.PageDownloader;
import an.evdokimov.discount.watcher.server.parser.downloader.PageDownloaderException;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;

@Component
public class LentaParser implements Parser {
    private static final String HOST_NAME = "lenta.com";
    private final PageDownloader pageDownloader;

    private static final String SELECTOR_PRODUCT_NAME = "body > div.scroll-lock > div.scroll-lock__inner.page-wrap >" +
            " div > main > article > div > div > div.sku-page.js-validation-popup.js-ecommerce-sku-card >" +
            " div.sku-page__head-info > div.sku-page__header > h1";
    private static final String SELECTOR_PRICE = "body > div.scroll-lock > div.scroll-lock__inner.page-wrap > div > " +
            "main > article > div > div > div.sku-page.js-validation-popup.js-ecommerce-sku-card > " +
            "div.sku-page__head-info > div.sku-page__info > div.sku-page__main-info > " +
            "div.sku-page-control-container.sku-page__control > div > div > div.sku-page-control__main > " +
            "div.sku-prices-block.sku-page-control__prices > " +
            "div.sku-prices-block__item.sku-prices-block__item--regular > " +
            "div.sku-price.sku-price--regular.sku-prices-block__price";
    private static final String SELECTOR_PRICE_RUBLES = "span.sku-price__integer";
    private static final String SELECTOR_PRICE_KOPECKS = "small.sku-price__fraction";
    private static final String SELECTOR_PRICE_WITH_DISCOUNT = "body > div.scroll-lock > " +
            "div.scroll-lock__inner.page-wrap > div > main > article > div > div > " +
            "div.sku-page.js-validation-popup.js-ecommerce-sku-card > div.sku-page__head-info > " +
            "div.sku-page__info > div.sku-page__main-info > div.sku-page-control-container.sku-page__control > div > " +
            "div > div.sku-page-control__main > div.sku-prices-block.sku-page-control__prices > " +
            "div.sku-prices-block__item.sku-prices-block__item--primary > " +
            "div.sku-price.sku-price--primary.sku-prices-block__price";
    private static final String SELECTOR_DISCOUNT = "body > div.scroll-lock > div.scroll-lock__inner.page-wrap > div " +
            "> main > article > div > div > div.sku-page.js-validation-popup.js-ecommerce-sku-card > " +
            "div.sku-page__head-info > div.sku-page__slider > " +
            "div.discount-label-small.discount-label-small--sku-page.sku-page__discount-label";
    private static final String SELECTOR_AVAILABILITY_INFORMATION = "body > div.scroll-lock > " +
            "div.scroll-lock__inner.page-wrap > div > main > article > div > div > " +
            "div.sku-page.js-validation-popup.js-ecommerce-sku-card > div.sku-page__head-info > div.sku-page__info > " +
            "div.sku-page__main-info > div.sku-page-control-container.sku-page__control > div > div > " +
            "div.sku-page-control__store-container.sku-store-container > div.sku-store-container__store-search > div";
    private static final String NOT_IN_STOCK_MESSAGE = "Товар закончился";

    public LentaParser(PageDownloader pageDownloader) {
        this.pageDownloader = pageDownloader;
    }

    @Override
    public Product parse(@NotNull URL url, @NotNull Shop shop) throws PageDownloaderException, ParserException {
        validateUrl(url);

        Document page = pageDownloader.downloadPage(url, shop.getCookie());

        ProductInformation productInformation = ProductInformation.builder()
                .urlLenta(url)
                .name(selectProductName(page))
                .build();

        BigDecimal price = selectPrice(page, SELECTOR_PRICE);
        BigDecimal priceWithCard = selectPrice(page, SELECTOR_PRICE_WITH_DISCOUNT);
        Double discount = selectDiscount(page);
        String availabilityInformation = selectAvailabilityInformation(page);

        return LentaProduct.builder()
                .productInformation(productInformation)
                .shop(shop)
                .price(price)
                .priceWithCard(priceWithCard)
                .discount(discount)
                .priceWithDiscount(priceWithCard)
                .isInStock(isProductInStock(availabilityInformation))
                .availabilityInformation(availabilityInformation)
                .date(LocalDateTime.now())
                .build();
    }

    @Override
    public Product parse(@NotNull ProductInformation productInformation, @NotNull Shop shop)
            throws PageDownloaderException, ParserException {
        validateUrl(productInformation.getUrlLenta());

        Document page = pageDownloader.downloadPage(productInformation.getUrlLenta(), shop.getCookie());

        BigDecimal price = selectPrice(page, SELECTOR_PRICE);
        BigDecimal priceWithCard = selectPrice(page, SELECTOR_PRICE_WITH_DISCOUNT);
        Double discount = selectDiscount(page);
        String availabilityInformation = selectElement(page, SELECTOR_AVAILABILITY_INFORMATION).text();

        return LentaProduct.builder()
                .productInformation(productInformation)
                .shop(shop)
                .price(price)
                .priceWithCard(priceWithCard)
                .discount(discount)
                .priceWithDiscount(priceWithCard)
                .isInStock(isProductInStock(availabilityInformation))
                .availabilityInformation(availabilityInformation)
                .date(LocalDateTime.now())
                .build();
    }

    private void validateUrl(@NotNull URL url) throws ParserException {
        if (!url.getHost().equals(HOST_NAME)) {
            throw new ParserException(ParserErrorCode.WRONG_SHOP_URL);
        }
    }

    private Element selectElement(@NotNull Document page, @NotNull String cssSelector) throws ParserException {
        Elements elements = page.select(cssSelector);
        if (elements.size() == 0) {
            throw new ParserException(ParserErrorCode.ELEMENT_NOT_FOUND, "selector=" + cssSelector);
        }
        if (elements.size() != 1) {
            throw new ParserException(ParserErrorCode.AMBIGUOUS_SEARCH_RESULT, "selector=" + cssSelector);
        }
        return elements.get(0);
    }

    private Element selectElement(@NotNull Element element, @NotNull String cssSelector) throws ParserException {
        Elements elements = element.select(cssSelector);
        if (elements.size() == 0) {
            throw new ParserException(ParserErrorCode.ELEMENT_NOT_FOUND, "selector=" + cssSelector);
        }
        if (elements.size() != 1) {
            throw new ParserException(ParserErrorCode.AMBIGUOUS_SEARCH_RESULT, "selector=" + cssSelector);
        }
        return elements.get(0);
    }

    private String selectProductName(@NotNull Document page) throws ParserException {
        return selectElement(page, SELECTOR_PRODUCT_NAME).text();
    }

    private BigDecimal selectPrice(@NotNull Document page, @NotNull String cssSelector) throws ParserException {
        Element priceBlock = selectElement(page, cssSelector);
        int rubles;
        int kopecks;
        try {
            rubles = Integer.parseInt(selectElement(priceBlock, SELECTOR_PRICE_RUBLES).text());
            kopecks = Integer.parseInt(selectElement(priceBlock, SELECTOR_PRICE_KOPECKS).text());
        } catch (NumberFormatException e) {
            throw new ParserException(ParserErrorCode.WRONG_NUMBER_FORMAT, e);
        }
        return BigDecimal.valueOf(rubles).add(BigDecimal.valueOf(kopecks, 2));
    }

    private Double selectDiscount(@NotNull Document page) throws ParserException {
        Elements elements = page.select(SELECTOR_DISCOUNT);
        if (elements.size() == 0) {
            return null;
        }
        if (elements.size() > 1) {
            throw new ParserException(ParserErrorCode.AMBIGUOUS_SEARCH_RESULT, "selector=" + SELECTOR_DISCOUNT);
        }
        double discount;
        try {
            discount = Double.parseDouble(elements.get(0).text().replace("%", ""));
        } catch (NumberFormatException e) {
            throw new ParserException(ParserErrorCode.WRONG_NUMBER_FORMAT, e);
        }
        return discount;
    }

    private String selectAvailabilityInformation(@NotNull Document page) throws ParserException {
        return selectElement(page, SELECTOR_AVAILABILITY_INFORMATION).text();
    }

    private boolean isProductInStock(@NotNull String availabilityInformation) {
        return !availabilityInformation.equalsIgnoreCase(NOT_IN_STOCK_MESSAGE);
    }
}
