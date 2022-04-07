package an.evdokimov.discount.watcher.server.parser;

import an.evdokimov.discount.watcher.server.database.city.model.City;
import an.evdokimov.discount.watcher.server.database.product.model.LentaProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.shop.model.CommercialNetwork;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

        LentaProductPrice expectedProductPrice = LentaProductPrice.builder()
                .price(BigDecimal.valueOf(10050, 2))
                .discount(10.0)
                .priceWithDiscount(BigDecimal.valueOf(9050, 2))
                .priceWithCard(BigDecimal.valueOf(9050, 2))
                .isInStock(true)
                .availabilityInformation("Товар заканчивается")
                .date(LocalDateTime.now(clock))
                .build();

        Product expectedProduct = Product.builder()
                .prices(List.of(expectedProductPrice))
                .shop(shop)
                .productInformation(productInformation)
                .build();

        expectedProductPrice.setProduct(expectedProduct);

        Product result = parser.parse(productInformation.getUrl(), shop);

        assertTrue(compareProducts(expectedProduct, result));
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

        LentaProductPrice expectedProductPrice = LentaProductPrice.builder()
                .price(BigDecimal.valueOf(10050, 2))
                .discount(10.0)
                .priceWithDiscount(BigDecimal.valueOf(9050, 2))
                .priceWithCard(BigDecimal.valueOf(9050, 2))
                .isInStock(true)
                .availabilityInformation("Товар заканчивается")
                .date(LocalDateTime.now(clock))
                .build();

        Product expectedProduct = Product.builder()
                .prices(List.of(expectedProductPrice))
                .shop(shop)
                .productInformation(productInformation)
                .build();

        expectedProductPrice.setProduct(expectedProduct);

        Product result = parser.parse(productInformation, shop);

        assertTrue(compareProducts(expectedProduct, result));
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

        LentaProductPrice expectedProductPrice = LentaProductPrice.builder()
                .price(BigDecimal.valueOf(10050, 2))
                .discount(10.0)
                .priceWithDiscount(BigDecimal.valueOf(9050, 2))
                .priceWithCard(BigDecimal.valueOf(9050, 2))
                .isInStock(true)
                .availabilityInformation("Товара много")
                .date(LocalDateTime.now(clock))
                .build();

        Product expectedProduct = Product.builder()
                .prices(List.of(expectedProductPrice))
                .shop(shop)
                .productInformation(productInformation)
                .build();

        expectedProductPrice.setProduct(expectedProduct);

        Product result = parser.parse(productInformation.getUrl(), shop);

        assertTrue(compareProducts(expectedProduct, result));
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

        LentaProductPrice expectedProductPrice = LentaProductPrice.builder()
                .price(BigDecimal.valueOf(10050, 2))
                .discount(10.0)
                .priceWithDiscount(BigDecimal.valueOf(9050, 2))
                .priceWithCard(BigDecimal.valueOf(9050, 2))
                .isInStock(false)
                .availabilityInformation("Товар закончился")
                .date(LocalDateTime.now(clock))
                .build();

        Product expectedProduct = Product.builder()
                .prices(List.of(expectedProductPrice))
                .shop(shop)
                .productInformation(productInformation)
                .build();

        expectedProductPrice.setProduct(expectedProduct);

        Product result = parser.parse(productInformation.getUrl(), shop);

        assertTrue(compareProducts(expectedProduct, result));
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

        LentaProductPrice expectedProductPrice = LentaProductPrice.builder()
                .price(BigDecimal.valueOf(10050, 2))
                .discount(10.0)
                .priceWithDiscount(BigDecimal.valueOf(9050, 2))
                .priceWithCard(BigDecimal.valueOf(9050, 2))
                .isInStock(true)
                .availabilityInformation("Товара достаточно")
                .date(LocalDateTime.now(clock))
                .build();

        Product expectedProduct = Product.builder()
                .prices(List.of(expectedProductPrice))
                .shop(shop)
                .productInformation(productInformation)
                .build();

        expectedProductPrice.setProduct(expectedProduct);

        Product result = parser.parse(productInformation.getUrl(), shop);

        assertTrue(compareProducts(expectedProduct, result));
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

        LentaProductPrice expectedProductPrice = LentaProductPrice.builder()
                .price(BigDecimal.valueOf(10050, 2))
                .discount(null)
                .priceWithDiscount(null)
                .priceWithCard(BigDecimal.valueOf(9050, 2))
                .isInStock(true)
                .availabilityInformation("Товара много")
                .date(LocalDateTime.now(clock))
                .build();

        Product expectedProduct = Product.builder()
                .prices(List.of(expectedProductPrice))
                .shop(shop)
                .productInformation(productInformation)
                .build();

        expectedProductPrice.setProduct(expectedProduct);

        Product result = parser.parse(productInformation.getUrl(), shop);

        assertTrue(compareProducts(expectedProduct, result));
    }

    private boolean compareProducts(Product product1, Product product2) {
        ArrayList<Boolean> comparisonResults = new ArrayList<>();

        comparisonResults.add(Objects.equals(product1.getId(), product2.getId()));
        comparisonResults.add(compareShops(product1.getShop(), product2.getShop()));
        comparisonResults.add(compareProductInformation(
                product1.getProductInformation(),
                product2.getProductInformation())
        );
        if (product1.getPrices().size() == product2.getPrices().size()) {
            for (int i = 0; i < product1.getPrices().size(); i++) {
                comparisonResults.add(compareProductPrices(
                        product1.getPrices().get(i),
                        product2.getPrices().get(i)
                ));
            }
        } else {
            return false;
        }

        for (Boolean bool : comparisonResults) {
            if (bool.equals(false)) {
                return false;
            }
        }
        return true;
    }

    private boolean compareShops(Shop shop1, Shop shop2) {
        if (shop1 == null && shop2 == null) {
            return true;
        }

        if (shop1 != null && shop2 != null) {
            ArrayList<Boolean> comparisonResults = new ArrayList<>();

            comparisonResults.add(Objects.equals(shop1.getId(), shop2.getId()));
            comparisonResults.add(Objects.equals(shop1.getName(), shop2.getName()));
            comparisonResults.add(Objects.equals(shop1.getAddress(), shop2.getAddress()));
            comparisonResults.add(Objects.equals(shop1.getCookie(), shop2.getCookie()));
            comparisonResults.add(compareCites(shop1.getCity(), shop2.getCity()));
            comparisonResults.add(compareCommercialNetwork(shop1.getCommercialNetwork(), shop2.getCommercialNetwork()));

            for (Boolean bool : comparisonResults) {
                if (bool.equals(false)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private boolean compareCites(City city1, City city2) {
        if (city1 == null && city2 == null) {
            return true;
        }

        if (city1 != null && city2 != null) {
            ArrayList<Boolean> comparisonResults = new ArrayList<>();

            comparisonResults.add(Objects.equals(city1.getId(), city2.getId()));
            comparisonResults.add(Objects.equals(city1.getName(), city2.getName()));
            comparisonResults.add(Objects.equals(city1.getCyrillicName(), city2.getCyrillicName()));

            for (Boolean bool : comparisonResults) {
                if (bool.equals(false)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private boolean compareCommercialNetwork(CommercialNetwork commercialNetwork1,
                                             CommercialNetwork commercialNetwork2) {
        if (commercialNetwork1 == null && commercialNetwork2 == null) {
            return true;
        }

        if (commercialNetwork1 != null && commercialNetwork2 != null) {
            ArrayList<Boolean> comparisonResults = new ArrayList<>();

            comparisonResults.add(Objects.equals(commercialNetwork1.getId(), commercialNetwork2.getId()));
            comparisonResults.add(Objects.equals(commercialNetwork1.getName(), commercialNetwork2.getName()));
            comparisonResults.add(
                    Objects.equals(commercialNetwork1.getCyrillicName(), commercialNetwork2.getCyrillicName())
            );
            for (Boolean bool : comparisonResults) {
                if (bool.equals(false)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private boolean compareProductInformation(ProductInformation productInformation1,
                                              ProductInformation productInformation2) {
        if (productInformation1 == null && productInformation2 == null) {
            return true;
        }

        if (productInformation1 != null && productInformation2 != null) {
            ArrayList<Boolean> comparisonResults = new ArrayList<>();

            comparisonResults.add(Objects.equals(productInformation1.getId(), productInformation2.getId()));
            comparisonResults.add(Objects.equals(productInformation1.getName(), productInformation2.getName()));
            comparisonResults.add(Objects.equals(productInformation1.getUrl(), productInformation2.getUrl()));

            for (Boolean bool : comparisonResults) {
                if (bool.equals(false)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private boolean compareProductPrices(ProductPrice productPrice1, ProductPrice productPrice2) {
        if (productPrice1 == null && productPrice2 == null) {
            return true;
        }

        if (productPrice1 != null && productPrice2 != null) {
            if (!productPrice1.getClass().equals(productPrice2.getClass())) {
                return false;
            }

            ArrayList<Boolean> comparisonResults = new ArrayList<>();

            comparisonResults.add(Objects.equals(productPrice1.getId(), productPrice2.getId()));
            comparisonResults.add(Objects.equals(productPrice1.getDate(), productPrice2.getDate()));
            comparisonResults.add(Objects.equals(productPrice1.getPrice(), productPrice2.getPrice()));
            comparisonResults.add(Objects.equals(productPrice1.getDiscount(), productPrice2.getDiscount()));
            comparisonResults.add(Objects.equals(
                    productPrice1.getAvailabilityInformation(),
                    productPrice2.getAvailabilityInformation())
            );
            comparisonResults.add(Objects.equals(
                    productPrice1.getPriceWithDiscount(),
                    productPrice2.getPriceWithDiscount())
            );
            if (productPrice1 instanceof LentaProductPrice) {
                comparisonResults.add(Objects.equals(
                        ((LentaProductPrice) productPrice1).getPriceWithCard(),
                        ((LentaProductPrice) productPrice2).getPriceWithCard()
                ));
            }

            for (Boolean bool : comparisonResults) {
                if (bool.equals(false)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }
}