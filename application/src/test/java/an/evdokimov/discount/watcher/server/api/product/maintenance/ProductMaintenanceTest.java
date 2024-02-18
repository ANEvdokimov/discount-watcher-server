package an.evdokimov.discount.watcher.server.api.product.maintenance;

import an.evdokimov.discount.watcher.server.amqp.dto.ParsedProductInformation;
import an.evdokimov.discount.watcher.server.amqp.dto.ParsedProductPrice;
import an.evdokimov.discount.watcher.server.amqp.dto.ProductForParsing;
import an.evdokimov.discount.watcher.server.amqp.service.ParserService;
import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductWithCookiesRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.LentaProductPriceResponse;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.LentaProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.ParsingStatus;
import an.evdokimov.discount.watcher.server.database.product.model.PriceChange;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.mapper.product.ParsedProductPriceMapper;
import an.evdokimov.discount.watcher.server.mapper.product.ProductMapper;
import an.evdokimov.discount.watcher.server.mapper.product.ProductPriceMapper;
import an.evdokimov.discount.watcher.server.mapper.product.UserProductMapper;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import an.evdokimov.discount.watcher.server.service.product.ProductInformationService;
import an.evdokimov.discount.watcher.server.service.product.ProductPriceService;
import an.evdokimov.discount.watcher.server.service.product.ProductService;
import an.evdokimov.discount.watcher.server.service.product.UserProductService;
import an.evdokimov.discount.watcher.server.service.shop.ShopService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ProductMaintenanceImpl.class)
class ProductMaintenanceTest {
    @MockBean
    private ProductService productService;
    @MockBean
    private ProductInformationService informationService;
    @MockBean
    private ProductPriceService priceService;
    @MockBean
    private UserProductService userProductService;
    @MockBean
    private ParserService parserService;
    @MockBean
    private ShopService shopService;
    @MockBean
    private ProductMapper productMapper;
    @MockBean
    private ProductPriceMapper priceMapper;
    @MockBean
    private ParsedProductPriceMapper parsedProductPriceMapper;
    @MockBean
    private UserProductMapper userProductMapper;
    @MockBean
    private Clock clock;

    @Autowired
    private ProductMaintenance testedProductMaintenance;


    @BeforeEach
    public void initClock() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2023, 5, 10).atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
    }

    @Test
    @DisplayName("Getting product by id")
    void getProduct_Product_validLentaProduct() throws ServerException {
        LentaProductPrice price2 = LentaProductPrice.builder().id(2L).price(BigDecimal.valueOf(5)).build();

        LentaProductPriceResponse priceResponse2 = LentaProductPriceResponse.builder()
                .id(price2.getId())
                .price(price2.getPrice())
                .build();

        Product testProduct = Product.builder()
                .id(666L)
                .prices(List.of(price2))
                .build();

        ProductResponse response = ProductResponse.builder()
                .id(testProduct.getId())
                .lastPrice(priceResponse2)
                .build();

        when(productService.getProduct(666L)).thenReturn(testProduct);
        when(productMapper.map(refEq(testProduct))).thenReturn(response);

        ProductResponse returnedProduct = testedProductMaintenance.getProduct(666L);

        assertEquals(response, returnedProduct);
    }

    @SneakyThrows
    @Test
    @DisplayName("Getting nonexistent product by id")
    void getProduct_nonexistentProduct_ServerException() {
        when(productService.getProduct(666L)).thenThrow(ServerErrorCode.PRODUCT_NOT_FOUND.getException());

        assertThrows(ServerException.class, () -> testedProductMaintenance.getProduct(666L));
        verify(productService).getProduct(666L);
    }

    @Test
    @DisplayName("Adding product")
    void addProduct_LentaProduct_LentaProductResponse() throws MalformedURLException, ServerException {
        NewProductWithCookiesRequest request = new NewProductWithCookiesRequest(
                new URL("https://lenta.com"),
                "coooookie",
                true,
                false,
                false
        );

        User mockedUser = User.builder().login("login").build();
        Shop mockedShop = Shop.builder().id(666L).cookie("coooookie").build();
        ProductInformation mockedInformation = ProductInformation.builder()
                .id(11L)
                .url(request.getUrl())
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();
        Product mockedProduct = Product.builder()
                .id(11L)
                .productInformation(mockedInformation)
                .shop(mockedShop)
                .build();
        LentaProductPrice mockedPrice = LentaProductPrice.builder()
                .id(11L)
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();
        UserProduct mockedUserProduct = UserProduct.builder()
                .id(11L)
                .product(mockedProduct)
                .user(mockedUser)
                .monitorDiscount(request.getMonitorDiscount())
                .monitorAvailability(request.getMonitorAvailability())
                .monitorPriceChanges(request.getMonitorPriceChanges())
                .build();

        when(shopService.getShopByCookie(mockedShop.getCookie())).thenReturn(mockedShop);
        when(informationService.getOrCreateByUrl(request.getUrl())).thenReturn(mockedInformation);
        when(productService.getOrCreateByProductInformationAndShop(mockedInformation, mockedShop))
                .thenReturn(mockedProduct);
        when(priceMapper.mapNewPrice(eq(mockedProduct), any())).thenReturn(mockedPrice);
        when(userProductMapper.map(request, mockedUser, mockedProduct)).thenReturn(mockedUserProduct);


        ProductForParsing expectedResult = ProductForParsing.builder()
                .productInformationId(mockedInformation.getId())
                .productPriceId(mockedPrice.getId())
                .url(mockedInformation.getUrl())
                .cookie(mockedShop.getCookie())
                .build();

        testedProductMaintenance.addProduct(mockedUser, request);

        verify(priceService).addPrice(mockedPrice);
        verify(userProductService).addOrUpdate(mockedUserProduct);
        verify(parserService).parseProduct(expectedResult);
    }

    @SneakyThrows
    @Test
    @DisplayName("Saving parsed product - 1st price")
    void saveParsedProduct_noPrevious_firstPrice() {
        ParsedProductPrice testedParsedPrice = new ParsedProductPrice(
                666L,
                BigDecimal.TEN,
                1.0,
                BigDecimal.TWO,
                false,
                "yes",
                LocalDateTime.now()
        );
        ParsedProductInformation testedParsedInformation = new ParsedProductInformation(
                1L,
                "product_name",
                testedParsedPrice
        );

        Product productFromDb = Product.builder()
                .id(111L)
                .build();
        ProductPrice mockedPriceFromDb = ProductPrice.builder()
                .id(testedParsedPrice.getId())
                .parsingStatus(ParsingStatus.PROCESSING)
                .product(productFromDb)
                .build();
        ProductInformation mockedInfo = ProductInformation.builder()
                .id(1L)
                .name("wrong_name")
                .url(new URL("http://shop.com"))
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();

        when(priceService.getById(testedParsedPrice.getId())).thenReturn(mockedPriceFromDb);
        when(priceService.findLastCompletedPriceByProduct(productFromDb)).thenReturn(Optional.empty());
        when(informationService.getById(mockedInfo.getId())).thenReturn(mockedInfo);
        doAnswer(invocation -> {
            ParsedProductPrice parsedProductPrice = invocation.getArgument(0, ParsedProductPrice.class);
            ProductPrice productPrice = invocation.getArgument(1, ProductPrice.class);

            productPrice.setPrice(parsedProductPrice.getPrice());
            productPrice.setDiscount(parsedProductPrice.getDiscount());
            productPrice.setPriceWithDiscount(parsedProductPrice.getPriceWithDiscount());
            productPrice.setIsInStock(parsedProductPrice.getIsInStock());
            productPrice.setAvailabilityInformation(parsedProductPrice.getAvailabilityInformation());
            productPrice.setParsingDate(parsedProductPrice.getParsingDate());

            return null;
        }).when(parsedProductPriceMapper).updateNotNullFields(testedParsedPrice, mockedPriceFromDb);

        testedProductMaintenance.saveParsedProduct(testedParsedInformation);

        assertEquals(PriceChange.FIRST_PRICE, mockedPriceFromDb.getPriceChange());
//        verify(priceService).addPrice(mockedPriceFromDb); fixme (after #30)
        assertEquals(testedParsedInformation.getName(), mockedInfo.getName());
        assertEquals(ParsingStatus.COMPLETE, mockedInfo.getParsingStatus());
    }

    @SneakyThrows
    @Test
    @DisplayName("Saving parsed product - 1st undefined")
    void saveParsedProduct_noPrevious_undefined() {
        ParsedProductPrice testedParsedPrice = new ParsedProductPrice(
                666L,
                null,
                null,
                null,
                false,
                "yes",
                LocalDateTime.now()
        );
        ParsedProductInformation testedParsedInformation = new ParsedProductInformation(
                1L,
                "product_name",
                testedParsedPrice
        );

        Product productFromDb = Product.builder()
                .id(111L)
                .build();
        ProductPrice mockedPriceFromDb = ProductPrice.builder()
                .id(testedParsedPrice.getId())
                .parsingStatus(ParsingStatus.PROCESSING)
                .product(productFromDb)
                .build();
        ProductInformation mockedInfo = ProductInformation.builder()
                .id(1L)
                .name("wrong_name")
                .url(new URL("http://shop.com"))
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();

        when(priceService.getById(testedParsedPrice.getId())).thenReturn(mockedPriceFromDb);
        when(priceService.findLastCompletedPriceByProduct(productFromDb)).thenReturn(Optional.empty());
        when(informationService.getById(mockedInfo.getId())).thenReturn(mockedInfo);

        testedProductMaintenance.saveParsedProduct(testedParsedInformation);

        assertEquals(PriceChange.UNDEFINED, mockedPriceFromDb.getPriceChange());
//        verify(priceService).addPrice(mockedPriceFromDb); fixme (after #30)
        assertEquals(testedParsedInformation.getName(), mockedInfo.getName());
        assertEquals(ParsingStatus.COMPLETE, mockedInfo.getParsingStatus());
    }

    @SneakyThrows
    @Test
    @DisplayName("Saving parsed product - undefined")
    void saveParsedProduct_previous_undefined() {
        ParsedProductPrice testedParsedPrice = new ParsedProductPrice(
                666L,
                null,
                null,
                null,
                false,
                "yes",
                LocalDateTime.of(2023, 9, 24, 0, 0)
        );
        ParsedProductInformation testedParsedInformation = new ParsedProductInformation(
                1L,
                "product_name",
                testedParsedPrice
        );

        Product productFromDb = Product.builder()
                .id(111L)
                .build();
        ProductPrice mockedPriceFromDb = ProductPrice.builder()
                .id(testedParsedPrice.getId())
                .parsingStatus(ParsingStatus.PROCESSING)
                .product(productFromDb)
                .build();

        ProductPrice previousPriceFromDb = ProductPrice.builder()
                .id(665L)
                .price(BigDecimal.TWO)
                .isInStock(true)
                .availabilityInformation("yes")
                .parsingDate(testedParsedPrice.getParsingDate().minusDays(1))
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.FIRST_PRICE)
                .build();
        ProductInformation mockedInfo = ProductInformation.builder()
                .id(1L)
                .name("wrong_name")
                .url(new URL("http://shop.com"))
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();

        when(priceService.getById(testedParsedPrice.getId())).thenReturn(mockedPriceFromDb);
        when(priceService.findLastCompletedPriceByProduct(productFromDb)).thenReturn(Optional.of(previousPriceFromDb));
        when(informationService.getById(mockedInfo.getId())).thenReturn(mockedInfo);

        testedProductMaintenance.saveParsedProduct(testedParsedInformation);

        assertEquals(PriceChange.UNDEFINED, mockedPriceFromDb.getPriceChange());
//        verify(priceService).addPrice(mockedPriceFromDb); fixme (after #30)
        assertEquals(testedParsedInformation.getName(), mockedInfo.getName());
        assertEquals(ParsingStatus.COMPLETE, mockedInfo.getParsingStatus());
    }

    @SneakyThrows
    @Test
    @DisplayName("Saving parsed product - equal")
    void saveParsedProduct_previousEqual_equal() {
        ParsedProductPrice testedParsedPrice = new ParsedProductPrice(
                666L,
                BigDecimal.TEN,
                1.0,
                BigDecimal.TWO,
                false,
                "yes",
                LocalDateTime.now()
        );
        ParsedProductInformation testedParsedInformation = new ParsedProductInformation(
                1L,
                "product_name",
                testedParsedPrice
        );

        Product productFromDb = Product.builder()
                .id(111L)
                .build();
        ProductPrice mockedPriceFromDb = ProductPrice.builder()
                .id(testedParsedPrice.getId())
                .parsingStatus(ParsingStatus.PROCESSING)
                .product(productFromDb)
                .build();
        ProductInformation mockedInfo = ProductInformation.builder()
                .id(1L)
                .name("wrong_name")
                .url(new URL("http://shop.com"))
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();

        ProductPrice previousPriceFromDb = ProductPrice.builder()
                .id(665L)
                .price(BigDecimal.TWO)
                .isInStock(false)
                .availabilityInformation("yes")
                .parsingDate(testedParsedPrice.getParsingDate().minusDays(1))
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.UNDEFINED)
                .build();

        when(priceService.getById(testedParsedPrice.getId())).thenReturn(mockedPriceFromDb);
        when(priceService.findLastCompletedPriceByProduct(productFromDb)).thenReturn(Optional.of(previousPriceFromDb));
        when(informationService.getById(mockedInfo.getId())).thenReturn(mockedInfo);
        doAnswer(invocation -> {
            ParsedProductPrice parsedProductPrice = invocation.getArgument(0, ParsedProductPrice.class);
            ProductPrice productPrice = invocation.getArgument(1, ProductPrice.class);

            productPrice.setPrice(parsedProductPrice.getPrice());
            productPrice.setDiscount(parsedProductPrice.getDiscount());
            productPrice.setPriceWithDiscount(parsedProductPrice.getPriceWithDiscount());
            productPrice.setIsInStock(parsedProductPrice.getIsInStock());
            productPrice.setAvailabilityInformation(parsedProductPrice.getAvailabilityInformation());
            productPrice.setParsingDate(parsedProductPrice.getParsingDate());

            return null;
        }).when(parsedProductPriceMapper).updateNotNullFields(testedParsedPrice, mockedPriceFromDb);

        testedProductMaintenance.saveParsedProduct(testedParsedInformation);

        assertEquals(PriceChange.EQUAL, mockedPriceFromDb.getPriceChange());
//        verify(priceService).addPrice(mockedPriceFromDb); fixme (after #30)
        assertEquals(testedParsedInformation.getName(), mockedInfo.getName());
        assertEquals(ParsingStatus.COMPLETE, mockedInfo.getParsingStatus());
    }

    @SneakyThrows
    @Test
    @DisplayName("Saving parsed product - up")
    void saveParsedProduct_previousLower_up() {
        ParsedProductPrice testedParsedPrice = new ParsedProductPrice(
                666L,
                BigDecimal.TEN,
                1.0,
                BigDecimal.TWO,
                false,
                "yes",
                LocalDateTime.now()
        );
        ParsedProductInformation testedParsedInformation = new ParsedProductInformation(
                1L,
                "product_name",
                testedParsedPrice
        );

        Product productFromDb = Product.builder()
                .id(111L)
                .build();
        ProductPrice mockedPriceFromDb = ProductPrice.builder()
                .id(testedParsedPrice.getId())
                .parsingStatus(ParsingStatus.PROCESSING)
                .product(productFromDb)
                .build();
        ProductInformation mockedInfo = ProductInformation.builder()
                .id(1L)
                .name("wrong_name")
                .url(new URL("http://shop.com"))
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();

        ProductPrice previousPriceFromDb = ProductPrice.builder()
                .id(665L)
                .price(BigDecimal.ONE)
                .isInStock(false)
                .availabilityInformation("yes")
                .parsingDate(testedParsedPrice.getParsingDate().minusDays(1))
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.UNDEFINED)
                .build();

        when(priceService.getById(testedParsedPrice.getId())).thenReturn(mockedPriceFromDb);
        when(priceService.findLastCompletedPriceByProduct(productFromDb)).thenReturn(Optional.of(previousPriceFromDb));
        when(informationService.getById(mockedInfo.getId())).thenReturn(mockedInfo);
        doAnswer(invocation -> {
            ParsedProductPrice parsedProductPrice = invocation.getArgument(0, ParsedProductPrice.class);
            ProductPrice productPrice = invocation.getArgument(1, ProductPrice.class);

            productPrice.setPrice(parsedProductPrice.getPrice());
            productPrice.setDiscount(parsedProductPrice.getDiscount());
            productPrice.setPriceWithDiscount(parsedProductPrice.getPriceWithDiscount());
            productPrice.setIsInStock(parsedProductPrice.getIsInStock());
            productPrice.setAvailabilityInformation(parsedProductPrice.getAvailabilityInformation());
            productPrice.setParsingDate(parsedProductPrice.getParsingDate());

            return null;
        }).when(parsedProductPriceMapper).updateNotNullFields(testedParsedPrice, mockedPriceFromDb);

        testedProductMaintenance.saveParsedProduct(testedParsedInformation);

        assertEquals(PriceChange.UP, mockedPriceFromDb.getPriceChange());
//        verify(priceService).addPrice(mockedPriceFromDb); fixme (after #30)
        assertEquals(testedParsedInformation.getName(), mockedInfo.getName());
        assertEquals(ParsingStatus.COMPLETE, mockedInfo.getParsingStatus());
    }

    @SneakyThrows
    @Test
    @DisplayName("Saving parsed product - down")
    void saveParsedProduct_previousHigher_down() {
        ParsedProductPrice testedParsedPrice = new ParsedProductPrice(
                666L,
                BigDecimal.TEN,
                1.0,
                BigDecimal.TWO,
                false,
                "yes",
                LocalDateTime.now()
        );
        ParsedProductInformation testedParsedInformation = new ParsedProductInformation(
                1L,
                "product_name",
                testedParsedPrice
        );

        ProductInformation mockedInfo = ProductInformation.builder()
                .id(1L)
                .name("wrong_name")
                .url(new URL("http://shop.com"))
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();
        Product productFromDb = Product.builder()
                .id(111L)
                .productInformation(mockedInfo)
                .build();
        ProductPrice mockedPriceFromDb = ProductPrice.builder()
                .id(testedParsedPrice.getId())
                .parsingStatus(ParsingStatus.PROCESSING)
                .product(productFromDb)
                .build();

        ProductPrice previousPriceFromDb = ProductPrice.builder()
                .id(665L)
                .price(BigDecimal.TEN)
                .isInStock(false)
                .availabilityInformation("yes")
                .parsingDate(testedParsedPrice.getParsingDate().minusDays(1))
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.UNDEFINED)
                .build();

        when(priceService.getById(testedParsedPrice.getId())).thenReturn(mockedPriceFromDb);
        when(priceService.findLastCompletedPriceByProduct(productFromDb)).thenReturn(Optional.of(previousPriceFromDb));
        when(informationService.getById(mockedInfo.getId())).thenReturn(mockedInfo);
        doAnswer(invocation -> {
            ParsedProductPrice parsedProductPrice = invocation.getArgument(0, ParsedProductPrice.class);
            ProductPrice productPrice = invocation.getArgument(1, ProductPrice.class);

            productPrice.setPrice(parsedProductPrice.getPrice());
            productPrice.setDiscount(parsedProductPrice.getDiscount());
            productPrice.setPriceWithDiscount(parsedProductPrice.getPriceWithDiscount());
            productPrice.setIsInStock(parsedProductPrice.getIsInStock());
            productPrice.setAvailabilityInformation(parsedProductPrice.getAvailabilityInformation());
            productPrice.setParsingDate(parsedProductPrice.getParsingDate());

            return null;
        }).when(parsedProductPriceMapper).updateNotNullFields(testedParsedPrice, mockedPriceFromDb);

        testedProductMaintenance.saveParsedProduct(testedParsedInformation);

        assertEquals(PriceChange.DOWN, mockedPriceFromDb.getPriceChange());
//        verify(priceService).addPrice(mockedPriceFromDb); fixme (after #30)
        assertEquals(testedParsedInformation.getName(), mockedInfo.getName());
        assertEquals(ParsingStatus.COMPLETE, mockedInfo.getParsingStatus());
    }

    @SneakyThrows
    @Test
    @DisplayName("Saving parsed product - nonexistent price")
    void saveParsedProduct_nonexistentPrice_serverException() {
        ParsedProductPrice testedParsedPrice = new ParsedProductPrice(
                666L,
                BigDecimal.TEN,
                1.0,
                BigDecimal.TWO,
                false,
                "yes",
                LocalDateTime.now()
        );
        ParsedProductInformation testedParsedInformation = new ParsedProductInformation(
                1L,
                "product_name",
                testedParsedPrice
        );

        when(priceService.getById(testedParsedPrice.getId()))
                .thenThrow(ServerErrorCode.PRODUCT_PRICE_NOT_FOUND.getException());

        assertThrows(
                ServerException.class,
                () -> testedProductMaintenance.saveParsedProduct(testedParsedInformation)
        );
    }

    @SneakyThrows
    @Test
    @DisplayName("Saving parsed product - nonexistent product information")
    void saveParsedProduct_nonexistentInformation_serverException() {
        ParsedProductPrice testedParsedPrice = new ParsedProductPrice(
                666L,
                BigDecimal.TEN,
                1.0,
                BigDecimal.TWO,
                false,
                "yes",
                LocalDateTime.now()
        );
        ParsedProductInformation testedParsedInformation = new ParsedProductInformation(
                1L,
                "product_name",
                testedParsedPrice
        );

        Product productFromDb = Product.builder()
                .id(111L)
                .build();
        ProductPrice mockedPriceFromDb = ProductPrice.builder()
                .id(testedParsedPrice.getId())
                .parsingStatus(ParsingStatus.PROCESSING)
                .product(productFromDb)
                .build();

        when(priceService.getById(testedParsedPrice.getId())).thenReturn(mockedPriceFromDb);
        when(priceService.findLastCompletedPriceByProduct(productFromDb)).thenReturn(Optional.empty());
        when(informationService.getById(any())).thenThrow(ServerErrorCode.PRODUCT_INFORMATION_NOT_FOUND.getException());

        assertThrows(
                ServerException.class,
                () -> testedProductMaintenance.saveParsedProduct(testedParsedInformation)
        );
    }

    @SneakyThrows
    @Test
    @DisplayName("Updating product")
    void updateProduct_validProduct_updatedProducts() throws MalformedURLException {
        ProductPrice oldPrice = ProductPrice.builder().id(1L).price(BigDecimal.valueOf(100)).build();
        Product mockedProduct = Product.builder()
                .id(666L)
                .shop(Shop.builder().id(1L).build())
                .productInformation(ProductInformation.builder().id(1L).name("product")
                        .url(new URL("https://lenta.com")).build())
                .prices(new ArrayList<>(List.of(oldPrice)))
                .build();
        ProductPrice mockedPrice = ProductPrice.builder()
                .product(mockedProduct)
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();

        when(priceMapper.mapNewPrice(eq(mockedProduct), any())).thenReturn(mockedPrice);
        when(productService.getProduct(mockedProduct.getId())).thenReturn(mockedProduct);

        ProductForParsing expectedResult = new ProductForParsing(
                mockedProduct.getProductInformation().getId(),
                mockedPrice.getId(),
                mockedProduct.getProductInformation().getUrl(),
                mockedProduct.getShop().getCookie()
        );

        testedProductMaintenance.updateProduct(mockedProduct.getId());

        verify(priceService).addPrice(mockedPrice);
        verify(parserService).parseProduct(expectedResult);
    }

    @SneakyThrows
    @Test
    @DisplayName("Updating tracked products")
    void updateTrackedProducts() {
        ProductPrice oldPrice = ProductPrice.builder().id(1L).price(BigDecimal.valueOf(100)).build();
        Product mockedProduct = Product.builder()
                .id(666L)
                .shop(Shop.builder().id(1L).build())
                .productInformation(ProductInformation.builder().id(1L).name("product")
                        .url(new URL("https://lenta.com")).build())
                .prices(new ArrayList<>(List.of(oldPrice)))
                .build();
        ProductPrice mockedPrice = ProductPrice.builder()
                .product(mockedProduct)
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();

        when(priceMapper.mapNewPrice(eq(mockedProduct), any())).thenReturn(mockedPrice);
        when(productService.getAllTrackedProducts()).thenReturn(List.of(mockedProduct));

        ProductForParsing expectedResult = new ProductForParsing(
                mockedProduct.getProductInformation().getId(),
                mockedPrice.getId(),
                mockedProduct.getProductInformation().getUrl(),
                mockedProduct.getShop().getCookie()
        );

        testedProductMaintenance.updateTrackedProducts();

        verify(priceService).addPrice(mockedPrice);
        verify(parserService).parseProduct(expectedResult);
    }
}