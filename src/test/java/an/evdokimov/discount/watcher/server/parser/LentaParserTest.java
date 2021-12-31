package an.evdokimov.discount.watcher.server.parser;

import an.evdokimov.discount.watcher.server.database.product.model.LentaProduct;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.parser.downloader.PageDownloader;
import an.evdokimov.discount.watcher.server.parser.downloader.PageDownloaderException;
import an.evdokimov.discount.watcher.server.parser.lenta.LentaParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class LentaParserTest {
    private final static LocalDate LOCAL_DATE = LocalDate.of(2021, 1, 1);

    @MockBean
    private PageDownloader pageDownloader;

    @MockBean
    private Clock clock;

    @Autowired
    private LentaParser parser;

    @BeforeEach
    public void initClock() {
        Clock fixedClock = Clock.fixed(LOCAL_DATE.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
    }

    @Test
    void parse_byUrlUnsupportedShop_exception() throws MalformedURLException {
        URL url = new URL("https://unsupported_shop");
        Shop shop = Shop.builder()
                .id(1L)
                .name("name")
                .build();

        assertThrows(
                ParserException.class,
                () -> parser.parse(url, shop)
        );
    }

    @Test
    void parse_byProductUnsupportedShop_exception() throws MalformedURLException {
        ProductInformation productInformation = ProductInformation.builder()
                .url(new URL("https://unsupported_shop"))
                .build();

        Shop shop = Shop.builder()
                .id(1L)
                .name("name")
                .build();

        assertThrows(
                ParserException.class,
                () -> parser.parse(productInformation, shop)
        );
    }

    @Test
    void parse_byUrlParseError_exception() throws IOException, PageDownloaderException {
        Document page = Jsoup.parse(
                new File("src/test/resources/page_examples/lenta/EmptyProductPage.html"), "utf8");
        when(pageDownloader.downloadPage(any(), any())).thenReturn(page);

        assertThrows(
                ParserException.class,
                () -> parser.parse(new URL("https://test_shop.com"), new Shop())
        );
    }

    @Test
    void parse_byProductParseError_exception() throws IOException, PageDownloaderException {
        Document page = Jsoup.parse(
                new File("src/test/resources/page_examples/lenta/EmptyProductPage.html"), "utf8");
        when(pageDownloader.downloadPage(any(), any())).thenReturn(page);

        ProductInformation productInformation = ProductInformation.builder()
                .url(new URL("https://test_shop.com"))
                .build();

        assertThrows(
                ParserException.class,
                () -> parser.parse(productInformation, new Shop())
        );
    }

    @Test
    void parse_byUrlProductRunOut_product() throws IOException, PageDownloaderException, ParserException {
        Document page = Jsoup.parse(
                new File("src/test/resources/page_examples/lenta/ProductRunOut.html"), "utf8");
        when(pageDownloader.downloadPage(any(), any())).thenReturn(page);

        ProductInformation productInformation = ProductInformation.builder()
                .name("product_name, subtitle")
                .url(new URL("https://lenta.com/product"))
                .build();

        Shop shop = new Shop();

        LentaProduct expected_product = LentaProduct.builder()
                .productInformation(productInformation)
                .shop(shop)
                .price(BigDecimal.valueOf(10050, 2))
                .discount(10.0)
                .priceWithDiscount(BigDecimal.valueOf(9050, 2))
                .priceWithCard(BigDecimal.valueOf(9050, 2))
                .isInStock(true)
                .availabilityInformation("Товар заканчивается")
                .date(LocalDateTime.now(clock))
                .build();

        Product result = parser.parse(productInformation.getUrl(), shop);

        assertEquals(expected_product, result);
    }

    @Test
    void parse_byProductProductRunOut_product() throws IOException, PageDownloaderException, ParserException {
        Document page = Jsoup.parse(
                new File("src/test/resources/page_examples/lenta/ProductRunOut.html"), "utf8");
        when(pageDownloader.downloadPage(any(), any())).thenReturn(page);

        ProductInformation productInformation = ProductInformation.builder()
                .name("product_name, subtitle")
                .url(new URL("https://lenta.com/product"))
                .build();

        Shop shop = new Shop();

        LentaProduct expected_product = LentaProduct.builder()
                .productInformation(productInformation)
                .shop(shop)
                .price(BigDecimal.valueOf(10050, 2))
                .discount(10.0)
                .priceWithDiscount(BigDecimal.valueOf(9050, 2))
                .priceWithCard(BigDecimal.valueOf(9050, 2))
                .isInStock(true)
                .availabilityInformation("Товар заканчивается")
                .date(LocalDateTime.now(clock))
                .build();

        Product result = parser.parse(productInformation, shop);

        assertEquals(expected_product, result);
    }

    @Test
    void parse_byUrlLotsOfProducts_product() throws IOException, PageDownloaderException, ParserException {
        Document page = Jsoup.parse(
                new File("src/test/resources/page_examples/lenta/LotsOfProducts.html"), "utf8");
        when(pageDownloader.downloadPage(any(), any())).thenReturn(page);

        ProductInformation productInformation = ProductInformation.builder()
                .name("product_name, subtitle")
                .url(new URL("https://lenta.com/product"))
                .build();

        Shop shop = new Shop();

        LentaProduct expected_product = LentaProduct.builder()
                .productInformation(productInformation)
                .shop(shop)
                .price(BigDecimal.valueOf(10050, 2))
                .discount(10.0)
                .priceWithDiscount(BigDecimal.valueOf(9050, 2))
                .priceWithCard(BigDecimal.valueOf(9050, 2))
                .isInStock(true)
                .availabilityInformation("Товара много")
                .date(LocalDateTime.now(clock))
                .build();

        Product result = parser.parse(productInformation.getUrl(), shop);

        assertEquals(expected_product, result);
    }

    @Test
    void parse_byUrlProductOutOfStock_product() throws IOException, PageDownloaderException, ParserException {
        Document page = Jsoup.parse(
                new File("src/test/resources/page_examples/lenta/ProductOutOfStock.html"), "utf8");
        when(pageDownloader.downloadPage(any(), any())).thenReturn(page);

        ProductInformation productInformation = ProductInformation.builder()
                .name("product_name, subtitle")
                .url(new URL("https://lenta.com/product"))
                .build();

        Shop shop = new Shop();

        LentaProduct expected_product = LentaProduct.builder()
                .productInformation(productInformation)
                .shop(shop)
                .price(BigDecimal.valueOf(10050, 2))
                .discount(10.0)
                .priceWithDiscount(BigDecimal.valueOf(9050, 2))
                .priceWithCard(BigDecimal.valueOf(9050, 2))
                .isInStock(false)
                .availabilityInformation("Товар закончился")
                .date(LocalDateTime.now(clock))
                .build();

        Product result = parser.parse(productInformation.getUrl(), shop);

        assertEquals(expected_product, result);
    }

    @Test
    void parse_byUrlProductsAreEnough_product() throws IOException, PageDownloaderException, ParserException {
        Document page = Jsoup.parse(
                new File("src/test/resources/page_examples/lenta/ProductsAreEnough.html"), "utf8");
        when(pageDownloader.downloadPage(any(), any())).thenReturn(page);

        ProductInformation productInformation = ProductInformation.builder()
                .name("product_name, subtitle")
                .url(new URL("https://lenta.com/product"))
                .build();

        Shop shop = new Shop();

        LentaProduct expected_product = LentaProduct.builder()
                .productInformation(productInformation)
                .shop(shop)
                .price(BigDecimal.valueOf(10050, 2))
                .discount(10.0)
                .priceWithDiscount(BigDecimal.valueOf(9050, 2))
                .priceWithCard(BigDecimal.valueOf(9050, 2))
                .isInStock(true)
                .availabilityInformation("Товара достаточно")
                .date(LocalDateTime.now(clock))
                .build();

        Product result = parser.parse(productInformation.getUrl(), shop);

        assertEquals(expected_product, result);
    }

    @Test
    void parse_byUrlProductWithoutDiscount_product() throws IOException, PageDownloaderException, ParserException {
        Document page = Jsoup.parse(
                new File("src/test/resources/page_examples/lenta/ProductWithoutDiscount.html"), "utf8");
        when(pageDownloader.downloadPage(any(), any())).thenReturn(page);

        ProductInformation productInformation = ProductInformation.builder()
                .name("product_name, subtitle")
                .url(new URL("https://lenta.com/product"))
                .build();

        Shop shop = new Shop();

        LentaProduct expected_product = LentaProduct.builder()
                .productInformation(productInformation)
                .shop(shop)
                .price(BigDecimal.valueOf(10050, 2))
                .discount(null)
                .priceWithDiscount(null)
                .priceWithCard(BigDecimal.valueOf(9050, 2))
                .isInStock(true)
                .availabilityInformation("Товара много")
                .date(LocalDateTime.now(clock))
                .build();

        Product result = parser.parse(productInformation.getUrl(), shop);

        assertEquals(expected_product, result);
    }
}