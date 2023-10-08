package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.amqp.dto.ParsedProductInformation;
import an.evdokimov.discount.watcher.server.amqp.dto.ParsedProductPrice;
import an.evdokimov.discount.watcher.server.amqp.dto.ProductForParsing;
import an.evdokimov.discount.watcher.server.amqp.service.ParserService;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.LentaProductPriceResponse;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.LentaProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.ParsingStatus;
import an.evdokimov.discount.watcher.server.database.product.model.PriceChange;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductInformationRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductPriceRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.UserProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.mapper.product.ParsedProductPriceMapper;
import an.evdokimov.discount.watcher.server.mapper.product.ProductMapper;
import an.evdokimov.discount.watcher.server.mapper.product.ProductPriceMapper;
import an.evdokimov.discount.watcher.server.mapper.product.UserProductMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ProductServiceImpl.class)
class ProductServiceTest {
    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private UserProductRepository userProductRepository;
    @MockBean
    private ProductPriceRepository productPriceRepository;
    @MockBean
    private ProductInformationRepository productInformationRepository;
    @MockBean
    private ShopRepository shopRepository;
    @MockBean
    private ParserService parserService;
    @MockBean
    private ProductMapper productMapper;
    @MockBean
    private ProductPriceMapper productPriceMapper;
    @MockBean
    private UserProductMapper userProductMapper;
    @MockBean
    private ParsedProductPriceMapper parsedProductPriceMapper;
    @MockBean
    private Clock clock;

    @Autowired
    private ProductServiceImpl testedProductService;

    @BeforeEach
    public void initClock() {
        Clock fixedClock = Clock.fixed(LocalDate.of(2023, 5, 10).atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
    }

    @Test
    void addProduct_LentaProduct_LentaProductResponse() throws MalformedURLException, ServerException {
        NewProductRequest request = new NewProductRequest(
                new URL("https://lenta.com"),
                666L,
                true,
                false,
                false
        );

        User mockedUser = User.builder().id(66L).build();
        Shop mockedShop = Shop.builder().id(666L).build();
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

        when(shopRepository.findById(mockedShop.getId())).thenReturn(Optional.of(mockedShop));
        when(productInformationRepository.findOrCreateByUrl(request.getUrl())).thenReturn(mockedInformation);
        when(productRepository.findOrCreateByProductInformationAndShop(mockedInformation, mockedShop))
                .thenReturn(mockedProduct);
        when(productPriceMapper.mapNewPrice(eq(mockedProduct), any())).thenReturn(mockedPrice);
        when(userProductMapper.map(request, mockedUser, mockedProduct)).thenReturn(mockedUserProduct);


        ProductForParsing expectedResult = ProductForParsing.builder()
                .productInformationId(mockedInformation.getId())
                .productPriceId(mockedPrice.getId())
                .url(mockedInformation.getUrl())
                .cookie(mockedShop.getCookie())
                .build();

        testedProductService.addProduct(mockedUser, request);

        assertAll(
                () -> verify(productPriceRepository).save(mockedPrice),
                () -> verify(userProductRepository).saveOrUpdate(mockedUserProduct),
                () -> verify(parserService).parseProduct(expectedResult)
        );
    }

    @Test
    void getProduct_Product_validLentaProductWithPriceHistory() throws ServerException {
        LentaProductPrice price1 = LentaProductPrice.builder().id(1L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price2 = LentaProductPrice.builder().id(2L).price(BigDecimal.valueOf(5)).build();

        LentaProductPriceResponse priceResponse1 = LentaProductPriceResponse.builder()
                .id(price1.getId())
                .price(price1.getPrice())
                .build();
        LentaProductPriceResponse priceResponse2 = LentaProductPriceResponse.builder()
                .id(price2.getId())
                .price(price2.getPrice())
                .build();

        Product testProduct = Product.builder()
                .id(666L)
                .prices(List.of(price2))
                .build();
        Product testProductWithPriceHistory = Product.builder()
                .id(666L)
                .prices(List.of(price2, price1))
                .build();

        ProductResponse response = ProductResponse.builder()
                .id(testProduct.getId())
                .prices(List.of(priceResponse2))
                .build();
        ProductResponse responseWithPriceHistory = ProductResponse.builder()
                .id(testProductWithPriceHistory.getId())
                .prices(List.of(priceResponse2, priceResponse1))
                .build();

        when(productRepository.findByIdWithLastPrice(666L)).thenReturn(Optional.of(testProduct));
        when(productRepository.findById(666L)).thenReturn(Optional.of(testProductWithPriceHistory));
        when(productMapper.map(refEq(testProduct))).thenReturn(response);
        when(productMapper.map(refEq(testProductWithPriceHistory))).thenReturn(responseWithPriceHistory);

        ProductResponse returnedProduct = testedProductService.getProduct(666L, true);

        assertAll(
                () -> assertEquals(responseWithPriceHistory, returnedProduct),
                () -> assertThat(
                        returnedProduct.getPrices(),
                        Matchers.contains(priceResponse2, priceResponse1)
                )
        );
    }

    @Test
    void getProduct_Product_validLentaProductWithoutPriceHistory() throws ServerException {
        LentaProductPrice price1 = LentaProductPrice.builder().id(1L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price2 = LentaProductPrice.builder().id(2L).price(BigDecimal.valueOf(5)).build();

        LentaProductPriceResponse priceResponse1 = LentaProductPriceResponse.builder()
                .id(price1.getId())
                .price(price1.getPrice())
                .build();
        LentaProductPriceResponse priceResponse2 = LentaProductPriceResponse.builder()
                .id(price2.getId())
                .price(price2.getPrice())
                .build();

        Product testProduct = Product.builder()
                .id(666L)
                .prices(List.of(price2))
                .build();
        Product testProductWithPriceHistory = Product.builder()
                .id(666L)
                .prices(List.of(price2, price1))
                .build();

        ProductResponse response = ProductResponse.builder()
                .id(testProduct.getId())
                .prices(List.of(priceResponse2))
                .build();
        ProductResponse responseWithPriceHistory = ProductResponse.builder()
                .id(testProductWithPriceHistory.getId())
                .prices(List.of(priceResponse2, priceResponse1))
                .build();

        when(productRepository.findByIdWithLastPrice(666L)).thenReturn(Optional.of(testProduct));
        when(productRepository.findById(666L)).thenReturn(Optional.of(testProductWithPriceHistory));
        when(productMapper.map(refEq(testProduct))).thenReturn(response);
        when(productMapper.map(refEq(testProductWithPriceHistory))).thenReturn(responseWithPriceHistory);

        ProductResponse returnedProduct = testedProductService.getProduct(666L, false);

        assertAll(
                () -> assertEquals(response, returnedProduct),
                () -> assertThat(
                        returnedProduct.getPrices(),
                        Matchers.contains(priceResponse2)
                )
        );
    }

    @Test
    void getProduct_nonexistentProductWithPriceHistory_ServerException() {
        when(productRepository.findById(666L)).thenReturn(Optional.empty());
        when(productRepository.findByIdWithLastPrice(666L)).thenReturn(Optional.empty());

        assertAll(
                () -> assertThrows(ServerException.class, () -> testedProductService.getProduct(666L, true)),
                () -> verify(productRepository, times(1)).findById(666L),
                () -> verify(productRepository, times(0)).findByIdWithLastPrice(666L)
        );
    }

    @Test
    void getProduct_nonexistentProductWithoutPriceHistory_ServerException() {
        when(productRepository.findById(666L)).thenReturn(Optional.empty());
        when(productRepository.findByIdWithLastPrice(666L)).thenReturn(Optional.empty());

        assertAll(
                () -> assertThrows(ServerException.class, () -> testedProductService.getProduct(666L, false)),
                () -> verify(productRepository, times(0)).findById(666L),
                () -> verify(productRepository, times(1)).findByIdWithLastPrice(666L)
        );
    }

    @Test
    void getUserProducts_allProductsWithPriceHistory_collectionOfProducts() {
        LentaProductPrice price1 = LentaProductPrice.builder().id(1L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price2 = LentaProductPrice.builder().id(2L).price(BigDecimal.valueOf(5)).build();

        LentaProductPriceResponse priceResponse1 = LentaProductPriceResponse.builder()
                .id(price1.getId())
                .price(price1.getPrice())
                .build();
        LentaProductPriceResponse priceResponse2 = LentaProductPriceResponse.builder()
                .id(price2.getId())
                .price(price2.getPrice())
                .build();

        Product testProduct1 = Product.builder()
                .id(1L)
                .prices(List.of(price2))
                .build();
        Product testProduct1WithPriceHistory = Product.builder()
                .id(1L)
                .prices(List.of(price2, price1))
                .build();

        ProductResponse response1 = ProductResponse.builder()
                .id(testProduct1.getId())
                .prices(List.of(priceResponse2))
                .build();
        ProductResponse response1WithPriceHistory = ProductResponse.builder()
                .id(testProduct1WithPriceHistory.getId())
                .prices(List.of(priceResponse2, priceResponse1))
                .build();

        LentaProductPrice price3 = LentaProductPrice.builder().id(3L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price4 = LentaProductPrice.builder().id(4L).price(BigDecimal.valueOf(5)).build();

        LentaProductPriceResponse priceResponse3 = LentaProductPriceResponse.builder()
                .id(price3.getId())
                .price(price3.getPrice())
                .build();
        LentaProductPriceResponse priceResponse4 = LentaProductPriceResponse.builder()
                .id(price4.getId())
                .price(price4.getPrice())
                .build();

        Product testProduct2 = Product.builder()
                .id(2L)
                .prices(List.of(price4))
                .build();
        Product testProduct2WithPriceHistory = Product.builder()
                .id(2L)
                .prices(List.of(price4, price3))
                .build();

        ProductResponse response2 = ProductResponse.builder()
                .id(testProduct2.getId())
                .prices(List.of(priceResponse4))
                .build();
        ProductResponse response2WithPriceHistory = ProductResponse.builder()
                .id(testProduct2WithPriceHistory.getId())
                .prices(List.of(priceResponse4, priceResponse3))
                .build();

        User userWithProducts = User.builder().id(666L).build();

        when(productMapper.map(refEq(testProduct1))).thenReturn(response1);
        when(productMapper.map(refEq(testProduct2))).thenReturn(response2);
        when(productMapper.map(refEq(testProduct1WithPriceHistory))).thenReturn(response1WithPriceHistory);
        when(productMapper.map(refEq(testProduct2WithPriceHistory))).thenReturn(response2WithPriceHistory);
        when(productRepository.findAllUserProducts(
                eq(userWithProducts), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(List.of(
                testProduct1WithPriceHistory,
                testProduct2WithPriceHistory
        ));
        when(productRepository.findAllUserProductsWithLastPrice(
                eq(userWithProducts), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(List.of(
                testProduct1,
                testProduct2
        ));

        Collection<ProductResponse> returnedProducts =
                testedProductService.getUserProducts(userWithProducts, true, false, true, true, true);
        assertAll(
                () -> assertThat(
                        returnedProducts,
                        containsInAnyOrder(response1WithPriceHistory, response2WithPriceHistory)
                ),
                () -> assertThat(
                        returnedProducts.stream()
                                .filter(productResponse -> productResponse.getId().equals(testProduct1.getId()))
                                .map(ProductResponse::getPrices)
                                .flatMap(List::stream)
                                .toList(),
                        containsInAnyOrder(priceResponse1, priceResponse2)
                ),
                () -> assertThat(
                        returnedProducts.stream()
                                .filter(productResponse -> productResponse.getId().equals(testProduct2.getId()))
                                .map(ProductResponse::getPrices)
                                .flatMap(List::stream)
                                .toList(),
                        containsInAnyOrder(priceResponse3, priceResponse4)
                )
        );
    }

    @Test
    void getUserProducts_activeProductsWithPriceHistory_collectionOfProducts() {
        LentaProductPrice price1 = LentaProductPrice.builder().id(1L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price2 = LentaProductPrice.builder().id(2L).price(BigDecimal.valueOf(5)).build();

        LentaProductPriceResponse priceResponse1 = LentaProductPriceResponse.builder()
                .id(price1.getId())
                .price(price1.getPrice())
                .build();
        LentaProductPriceResponse priceResponse2 = LentaProductPriceResponse.builder()
                .id(price2.getId())
                .price(price2.getPrice())
                .build();

        Product testProduct1 = Product.builder()
                .id(1L)
                .prices(List.of(price2))
                .build();
        Product testProduct1WithPriceHistory = Product.builder()
                .id(1L)
                .prices(List.of(price2, price1))
                .build();

        ProductResponse response1 = ProductResponse.builder()
                .id(testProduct1.getId())
                .prices(List.of(priceResponse2))
                .build();
        ProductResponse response1WithPriceHistory = ProductResponse.builder()
                .id(testProduct1WithPriceHistory.getId())
                .prices(List.of(priceResponse2, priceResponse1))
                .build();

        LentaProductPrice price3 = LentaProductPrice.builder().id(3L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price4 = LentaProductPrice.builder().id(4L).price(BigDecimal.valueOf(5)).build();

        LentaProductPriceResponse priceResponse3 = LentaProductPriceResponse.builder()
                .id(price3.getId())
                .price(price3.getPrice())
                .build();
        LentaProductPriceResponse priceResponse4 = LentaProductPriceResponse.builder()
                .id(price4.getId())
                .price(price4.getPrice())
                .build();

        Product testProduct2 = Product.builder()
                .id(2L)
                .prices(List.of(price4))
                .build();
        Product testProduct2WithPriceHistory = Product.builder()
                .id(2L)
                .prices(List.of(price4, price3))
                .build();

        ProductResponse response2 = ProductResponse.builder()
                .id(testProduct2.getId())
                .prices(List.of(priceResponse4))
                .build();
        ProductResponse response2WithPriceHistory = ProductResponse.builder()
                .id(testProduct2WithPriceHistory.getId())
                .prices(List.of(priceResponse4, priceResponse3))
                .build();

        User userWithProducts = User.builder().id(666L).build();

        when(productMapper.map(refEq(testProduct1))).thenReturn(response1);
        when(productMapper.map(refEq(testProduct2))).thenReturn(response2);
        when(productMapper.map(refEq(testProduct1WithPriceHistory))).thenReturn(response1WithPriceHistory);
        when(productMapper.map(refEq(testProduct2WithPriceHistory))).thenReturn(response2WithPriceHistory);
        when(productRepository.findActiveUserProducts(userWithProducts, true, true, true))
                .thenReturn(List.of(
                        testProduct1WithPriceHistory,
                        testProduct2WithPriceHistory
                ));
        when(productRepository.findActiveUserProductsWithLastPrice(
                userWithProducts,
                true,
                true,
                true))
                .thenReturn(List.of(
                        testProduct1,
                        testProduct2
                ));

        Collection<ProductResponse> returnedProducts =
                testedProductService.getUserProducts(
                        userWithProducts,
                        true,
                        true,
                        true,
                        true,
                        true
                );
        assertAll(
                () -> assertThat(
                        returnedProducts,
                        containsInAnyOrder(response1WithPriceHistory, response2WithPriceHistory)
                ),
                () -> assertThat(
                        returnedProducts.stream()
                                .filter(productResponse -> productResponse.getId().equals(testProduct1.getId()))
                                .map(ProductResponse::getPrices)
                                .flatMap(List::stream)
                                .toList(),
                        containsInAnyOrder(priceResponse1, priceResponse2)
                ),
                () -> assertThat(
                        returnedProducts.stream()
                                .filter(productResponse -> productResponse.getId().equals(testProduct2.getId()))
                                .map(ProductResponse::getPrices)
                                .flatMap(List::stream)
                                .toList(),
                        containsInAnyOrder(priceResponse3, priceResponse4)
                )
        );
    }

    @Test
    void getUserProducts_allProductsWithoutPriceHistory_collectionOfProducts() {
        LentaProductPrice price1 = LentaProductPrice.builder().id(1L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price2 = LentaProductPrice.builder().id(2L).price(BigDecimal.valueOf(5)).build();

        LentaProductPriceResponse priceResponse1 = LentaProductPriceResponse.builder()
                .id(price1.getId())
                .price(price1.getPrice())
                .build();
        LentaProductPriceResponse priceResponse2 = LentaProductPriceResponse.builder()
                .id(price2.getId())
                .price(price2.getPrice())
                .build();

        Product testProduct1 = Product.builder()
                .id(1L)
                .prices(List.of(price2))
                .build();
        Product testProduct1WithPriceHistory = Product.builder()
                .id(1L)
                .prices(List.of(price2, price1))
                .build();

        ProductResponse response1 = ProductResponse.builder()
                .id(testProduct1.getId())
                .prices(List.of(priceResponse2))
                .build();
        ProductResponse response1WithPriceHistory = ProductResponse.builder()
                .id(testProduct1WithPriceHistory.getId())
                .prices(List.of(priceResponse2, priceResponse1))
                .build();

        LentaProductPrice price3 = LentaProductPrice.builder().id(3L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price4 = LentaProductPrice.builder().id(4L).price(BigDecimal.valueOf(5)).build();

        LentaProductPriceResponse priceResponse3 = LentaProductPriceResponse.builder()
                .id(price3.getId())
                .price(price3.getPrice())
                .build();
        LentaProductPriceResponse priceResponse4 = LentaProductPriceResponse.builder()
                .id(price4.getId())
                .price(price4.getPrice())
                .build();

        Product testProduct2 = Product.builder()
                .id(2L)
                .prices(List.of(price4))
                .build();
        Product testProduct2WithPriceHistory = Product.builder()
                .id(2L)
                .prices(List.of(price4, price3))
                .build();

        ProductResponse response2 = ProductResponse.builder()
                .id(testProduct2.getId())
                .prices(List.of(priceResponse4))
                .build();
        ProductResponse response2WithPriceHistory = ProductResponse.builder()
                .id(testProduct2WithPriceHistory.getId())
                .prices(List.of(priceResponse4, priceResponse3))
                .build();

        User userWithProducts = User.builder().id(666L).build();

        when(productMapper.map(refEq(testProduct1))).thenReturn(response1);
        when(productMapper.map(refEq(testProduct2))).thenReturn(response2);
        when(productMapper.map(refEq(testProduct1WithPriceHistory))).thenReturn(response1WithPriceHistory);
        when(productMapper.map(refEq(testProduct2WithPriceHistory))).thenReturn(response2WithPriceHistory);
        when(productRepository.findAllUserProducts(
                eq(userWithProducts), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(List.of(
                testProduct1WithPriceHistory,
                testProduct2WithPriceHistory
        ));
        when(productRepository.findAllUserProductsWithLastPrice(
                eq(userWithProducts), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(List.of(
                testProduct1,
                testProduct2
        ));

        Collection<ProductResponse> returnedProducts =
                testedProductService.getUserProducts(userWithProducts, false, false, true, true, true);
        assertAll(
                () -> assertThat(
                        returnedProducts,
                        containsInAnyOrder(response1, response2)
                ),
                () -> assertThat(
                        returnedProducts.stream()
                                .filter(productResponse -> productResponse.getId().equals(testProduct1.getId()))
                                .map(ProductResponse::getPrices)
                                .flatMap(List::stream)
                                .toList(),
                        contains(priceResponse2)
                ),
                () -> assertThat(
                        returnedProducts.stream()
                                .filter(productResponse -> productResponse.getId().equals(testProduct2.getId()))
                                .map(ProductResponse::getPrices)
                                .flatMap(List::stream)
                                .toList(),
                        contains(priceResponse4)
                )
        );
    }

    @Test
    void getUserProducts_activeProductsWithoutPriceHistory_collectionOfProducts() {
        LentaProductPrice price1 = LentaProductPrice.builder().id(1L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price2 = LentaProductPrice.builder().id(2L).price(BigDecimal.valueOf(5)).build();

        LentaProductPriceResponse priceResponse1 = LentaProductPriceResponse.builder()
                .id(price1.getId())
                .price(price1.getPrice())
                .build();
        LentaProductPriceResponse priceResponse2 = LentaProductPriceResponse.builder()
                .id(price2.getId())
                .price(price2.getPrice())
                .build();

        Product testProduct1 = Product.builder()
                .id(1L)
                .prices(List.of(price2))
                .build();
        Product testProduct1WithPriceHistory = Product.builder()
                .id(1L)
                .prices(List.of(price2, price1))
                .build();

        ProductResponse response1 = ProductResponse.builder()
                .id(testProduct1.getId())
                .prices(List.of(priceResponse2))
                .build();
        ProductResponse response1WithPriceHistory = ProductResponse.builder()
                .id(testProduct1WithPriceHistory.getId())
                .prices(List.of(priceResponse2, priceResponse1))
                .build();

        LentaProductPrice price3 = LentaProductPrice.builder().id(3L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price4 = LentaProductPrice.builder().id(4L).price(BigDecimal.valueOf(5)).build();

        LentaProductPriceResponse priceResponse3 = LentaProductPriceResponse.builder()
                .id(price3.getId())
                .price(price3.getPrice())
                .build();
        LentaProductPriceResponse priceResponse4 = LentaProductPriceResponse.builder()
                .id(price4.getId())
                .price(price4.getPrice())
                .build();

        Product testProduct2 = Product.builder()
                .id(2L)
                .prices(List.of(price4))
                .build();
        Product testProduct2WithPriceHistory = Product.builder()
                .id(2L)
                .prices(List.of(price4, price3))
                .build();

        ProductResponse response2 = ProductResponse.builder()
                .id(testProduct2.getId())
                .prices(List.of(priceResponse4))
                .build();
        ProductResponse response2WithPriceHistory = ProductResponse.builder()
                .id(testProduct2WithPriceHistory.getId())
                .prices(List.of(priceResponse4, priceResponse3))
                .build();

        User userWithProducts = User.builder().id(666L).build();

        when(productMapper.map(refEq(testProduct1))).thenReturn(response1);
        when(productMapper.map(refEq(testProduct2))).thenReturn(response2);
        when(productMapper.map(refEq(testProduct1WithPriceHistory))).thenReturn(response1WithPriceHistory);
        when(productMapper.map(refEq(testProduct2WithPriceHistory))).thenReturn(response2WithPriceHistory);
        when(productRepository.findActiveUserProducts(
                userWithProducts,
                true,
                true,
                true
        )).thenReturn(List.of(
                testProduct1WithPriceHistory,
                testProduct2WithPriceHistory
        ));
        when(productRepository.findActiveUserProductsWithLastPrice(
                userWithProducts,
                true,
                true,
                true
        )).thenReturn(List.of(
                testProduct1,
                testProduct2
        ));

        Collection<ProductResponse> returnedProducts =
                testedProductService.getUserProducts(
                        userWithProducts,
                        false,
                        true,
                        true,
                        true,
                        true
                );
        assertAll(
                () -> assertThat(
                        returnedProducts,
                        containsInAnyOrder(response1, response2)
                ),
                () -> assertThat(
                        returnedProducts.stream()
                                .filter(productResponse -> productResponse.getId().equals(testProduct1.getId()))
                                .map(ProductResponse::getPrices)
                                .flatMap(List::stream)
                                .toList(),
                        contains(priceResponse2)
                ),
                () -> assertThat(
                        returnedProducts.stream()
                                .filter(productResponse -> productResponse.getId().equals(testProduct2.getId()))
                                .map(ProductResponse::getPrices)
                                .flatMap(List::stream)
                                .toList(),
                        contains(priceResponse4)
                )
        );
    }

    @Test
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

        when(productPriceMapper.mapNewPrice(eq(mockedProduct), any())).thenReturn(mockedPrice);

        ProductForParsing expectedResult = new ProductForParsing(
                mockedProduct.getProductInformation().getId(),
                mockedPrice.getId(),
                mockedProduct.getProductInformation().getUrl(),
                mockedProduct.getShop().getCookie()
        );

        testedProductService.updateProduct(mockedProduct);

        assertAll(
                () -> verify(productPriceRepository).save(mockedPrice),
                () -> verify(parserService).parseProduct(expectedResult)
        );
    }

    @Test
    void getUserProductsInShop_allProducts_lotsOfProducts() throws ServerException {
        Product product1 = Product.builder().id(1L).build();
        Product product2 = Product.builder().id(2L).build();
        Product product3 = Product.builder().id(3L).build();
        Product product4 = Product.builder().id(4L).build();

        ProductResponse response1 = ProductResponse.builder().id(product1.getId()).build();
        ProductResponse response2 = ProductResponse.builder().id(product2.getId()).build();
        ProductResponse response3 = ProductResponse.builder().id(product3.getId()).build();
        ProductResponse response4 = ProductResponse.builder().id(product4.getId()).build();

        when(productMapper.map(refEq(product1))).thenReturn(response1);
        when(productMapper.map(refEq(product2))).thenReturn(response2);
        when(productMapper.map(refEq(product3))).thenReturn(response3);
        when(productMapper.map(refEq(product4))).thenReturn(response4);

        Shop shop = Shop.builder().id(1L).name("shop").build();
        when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));

        when(productRepository.findAllUserProductsInShop(
                any(), any(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(List.of(product1));
        when(productRepository.findAllUserProductsWithLastPriceInShop(
                any(), any(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(List.of(product2));
        when(productRepository.findActiveUserProductsInShop(any(), any(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of(product3));
        when(productRepository.findActiveUserProductsWithLastPriceInShop(any(), any(), anyBoolean(), anyBoolean(),
                anyBoolean())).thenReturn(List.of(product4));

        Collection<ProductResponse> returnedProducts = testedProductService.getUserProductsInShop(
                new User(),
                shop.getId(),
                true,
                false,
                false,
                false,
                false
        );

        assertThat(returnedProducts, contains(response1));
    }

    @Test
    void getUserProductsInShop_activeProducts_lotsOfProducts() throws ServerException {
        Product product1 = Product.builder().id(1L).build();
        Product product2 = Product.builder().id(2L).build();
        Product product3 = Product.builder().id(3L).build();
        Product product4 = Product.builder().id(4L).build();

        ProductResponse response1 = ProductResponse.builder().id(product1.getId()).build();
        ProductResponse response2 = ProductResponse.builder().id(product2.getId()).build();
        ProductResponse response3 = ProductResponse.builder().id(product3.getId()).build();
        ProductResponse response4 = ProductResponse.builder().id(product4.getId()).build();

        when(productMapper.map(refEq(product1))).thenReturn(response1);
        when(productMapper.map(refEq(product2))).thenReturn(response2);
        when(productMapper.map(refEq(product3))).thenReturn(response3);
        when(productMapper.map(refEq(product4))).thenReturn(response4);

        Shop shop = Shop.builder().id(1L).name("shop").build();
        when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));

        when(productRepository.findActiveUserProductsInShop(
                any(), any(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(List.of(product1));
        when(productRepository.findActiveUserProductsWithLastPriceInShop(
                any(), any(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(List.of(product2));
        when(productRepository.findActiveUserProductsInShop(any(), any(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of(product3));
        when(productRepository.findActiveUserProductsWithLastPriceInShop(any(), any(), anyBoolean(), anyBoolean(),
                anyBoolean())).thenReturn(List.of(product4));

        Collection<ProductResponse> returnedProducts =
                testedProductService.getUserProductsInShop(
                        new User(),
                        shop.getId(),
                        true,
                        true,
                        true,
                        true,
                        true
                );

        assertThat(returnedProducts, contains(response3));
    }

    @Test
    void getUserProductsInShop_allProductsWithLastPrice_lotsOfProducts() throws ServerException {
        Product product1 = Product.builder().id(1L).build();
        Product product2 = Product.builder().id(2L).build();
        Product product3 = Product.builder().id(3L).build();
        Product product4 = Product.builder().id(4L).build();

        ProductResponse response1 = ProductResponse.builder().id(product1.getId()).build();
        ProductResponse response2 = ProductResponse.builder().id(product2.getId()).build();
        ProductResponse response3 = ProductResponse.builder().id(product3.getId()).build();
        ProductResponse response4 = ProductResponse.builder().id(product4.getId()).build();

        when(productMapper.map(refEq(product1))).thenReturn(response1);
        when(productMapper.map(refEq(product2))).thenReturn(response2);
        when(productMapper.map(refEq(product3))).thenReturn(response3);
        when(productMapper.map(refEq(product4))).thenReturn(response4);

        Shop shop = Shop.builder().id(1L).name("shop").build();
        when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));

        when(productRepository.findAllUserProductsInShop(
                any(), any(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(List.of(product1));
        when(productRepository.findAllUserProductsWithLastPriceInShop(
                any(), any(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(List.of(product2));
        when(productRepository.findActiveUserProductsInShop(any(), any(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of(product3));
        when(productRepository.findActiveUserProductsWithLastPriceInShop(any(), any(), anyBoolean(), anyBoolean(),
                anyBoolean())).thenReturn(List.of(product4));

        Collection<ProductResponse> returnedProducts = testedProductService.getUserProductsInShop(
                new User(),
                shop.getId(),
                false,
                false,
                false,
                false,
                false
        );

        assertThat(returnedProducts, contains(response2));
    }

    @Test
    void getUserProductsInShop_activeProductsWithLastPrice_lotsOfProducts() throws ServerException {
        Product product1 = Product.builder().id(1L).build();
        Product product2 = Product.builder().id(2L).build();
        Product product3 = Product.builder().id(3L).build();
        Product product4 = Product.builder().id(4L).build();

        ProductResponse response1 = ProductResponse.builder().id(product1.getId()).build();
        ProductResponse response2 = ProductResponse.builder().id(product2.getId()).build();
        ProductResponse response3 = ProductResponse.builder().id(product3.getId()).build();
        ProductResponse response4 = ProductResponse.builder().id(product4.getId()).build();

        when(productMapper.map(refEq(product1))).thenReturn(response1);
        when(productMapper.map(refEq(product2))).thenReturn(response2);
        when(productMapper.map(refEq(product3))).thenReturn(response3);
        when(productMapper.map(refEq(product4))).thenReturn(response4);

        Shop shop = Shop.builder().id(1L).name("shop").build();
        when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));

        when(productRepository.findActiveUserProductsInShop(
                any(), any(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(List.of(product1));
        when(productRepository.findActiveUserProductsWithLastPriceInShop(
                any(), any(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(List.of(product2));
        when(productRepository.findActiveUserProductsInShop(any(), any(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of(product3));
        when(productRepository.findActiveUserProductsWithLastPriceInShop(any(), any(), anyBoolean(), anyBoolean(),
                anyBoolean())).thenReturn(List.of(product4));

        Collection<ProductResponse> returnedProducts =
                testedProductService.getUserProductsInShop(
                        new User(),
                        shop.getId(),
                        false,
                        true,
                        true,
                        true,
                        true
                );

        assertThat(returnedProducts, contains(response4));
    }

    @Test
    void getUserProductsInShop_wrongShopId_ServerException() {
        when(shopRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                ServerException.class,
                () -> testedProductService.getUserProductsInShop(
                        new User(),
                        666L,
                        false,
                        true,
                        true,
                        true,
                        true
                )
        );
    }

    @SneakyThrows
    @Test
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

        when(productPriceRepository.findById(testedParsedPrice.getId())).thenReturn(Optional.of(mockedPriceFromDb));
        when(productPriceRepository.findLastPriceByProduct(productFromDb)).thenReturn(Optional.empty());
        when(productInformationRepository.updateNameById(
                testedParsedInformation.getId(),
                testedParsedInformation.getName())
        ).thenReturn(1);
        when(productInformationRepository.updateNameById(
                testedParsedInformation.getId(),
                testedParsedInformation.getName())
        ).thenReturn(1);
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

        testedProductService.saveParsedProduct(testedParsedInformation);

        assertEquals(PriceChange.FIRST_PRICE, mockedPriceFromDb.getPriceChange());
        verify(productPriceRepository).save(mockedPriceFromDb);
        verify(productInformationRepository)
                .updateNameById(testedParsedInformation.getId(), testedParsedInformation.getName());
    }

    @SneakyThrows
    @Test
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

        when(productPriceRepository.findById(testedParsedPrice.getId())).thenReturn(Optional.of(mockedPriceFromDb));
        when(productPriceRepository.findLastPriceByProduct(productFromDb)).thenReturn(Optional.empty());
        when(productInformationRepository.updateNameById(
                testedParsedInformation.getId(),
                testedParsedInformation.getName())
        ).thenReturn(1);

        testedProductService.saveParsedProduct(testedParsedInformation);

        assertEquals(PriceChange.UNDEFINED, mockedPriceFromDb.getPriceChange());
        verify(productPriceRepository).save(mockedPriceFromDb);
        verify(productInformationRepository)
                .updateNameById(testedParsedInformation.getId(), testedParsedInformation.getName());
    }

    @SneakyThrows
    @Test
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

        when(productPriceRepository.findById(testedParsedPrice.getId())).thenReturn(Optional.of(mockedPriceFromDb));
        when(productPriceRepository.findLastPriceByProduct(productFromDb)).thenReturn(Optional.of(previousPriceFromDb));
        when(productInformationRepository.updateNameById(
                testedParsedInformation.getId(),
                testedParsedInformation.getName())
        ).thenReturn(1);

        testedProductService.saveParsedProduct(testedParsedInformation);

        assertEquals(PriceChange.UNDEFINED, mockedPriceFromDb.getPriceChange());
        verify(productPriceRepository).save(mockedPriceFromDb);
        verify(productInformationRepository)
                .updateNameById(testedParsedInformation.getId(), testedParsedInformation.getName());
    }

    @SneakyThrows
    @Test
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

        ProductPrice previousPriceFromDb = ProductPrice.builder()
                .id(665L)
                .price(BigDecimal.TWO)
                .isInStock(false)
                .availabilityInformation("yes")
                .parsingDate(testedParsedPrice.getParsingDate().minusDays(1))
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.UNDEFINED)
                .build();

        when(productPriceRepository.findById(testedParsedPrice.getId())).thenReturn(Optional.of(mockedPriceFromDb));
        when(productPriceRepository.findLastPriceByProduct(productFromDb)).thenReturn(Optional.of(previousPriceFromDb));
        when(productInformationRepository.updateNameById(
                testedParsedInformation.getId(),
                testedParsedInformation.getName())
        ).thenReturn(1);
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

        testedProductService.saveParsedProduct(testedParsedInformation);

        assertEquals(PriceChange.EQUAL, mockedPriceFromDb.getPriceChange());
        verify(productPriceRepository).save(mockedPriceFromDb);
        verify(productInformationRepository)
                .updateNameById(testedParsedInformation.getId(), testedParsedInformation.getName());
    }

    @SneakyThrows
    @Test
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

        ProductPrice previousPriceFromDb = ProductPrice.builder()
                .id(665L)
                .price(BigDecimal.ONE)
                .isInStock(false)
                .availabilityInformation("yes")
                .parsingDate(testedParsedPrice.getParsingDate().minusDays(1))
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.UNDEFINED)
                .build();

        when(productPriceRepository.findById(testedParsedPrice.getId())).thenReturn(Optional.of(mockedPriceFromDb));
        when(productPriceRepository.findLastPriceByProduct(productFromDb)).thenReturn(Optional.of(previousPriceFromDb));
        when(productInformationRepository.updateNameById(
                testedParsedInformation.getId(),
                testedParsedInformation.getName())
        ).thenReturn(1);
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

        testedProductService.saveParsedProduct(testedParsedInformation);

        assertEquals(PriceChange.UP, mockedPriceFromDb.getPriceChange());
        verify(productPriceRepository).save(mockedPriceFromDb);
        verify(productInformationRepository)
                .updateNameById(testedParsedInformation.getId(), testedParsedInformation.getName());
    }

    @SneakyThrows
    @Test
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
                .price(BigDecimal.TEN)
                .isInStock(false)
                .availabilityInformation("yes")
                .parsingDate(testedParsedPrice.getParsingDate().minusDays(1))
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.UNDEFINED)
                .build();

        when(productPriceRepository.findById(testedParsedPrice.getId())).thenReturn(Optional.of(mockedPriceFromDb));
        when(productPriceRepository.findLastPriceByProduct(productFromDb)).thenReturn(Optional.of(previousPriceFromDb));
        when(productInformationRepository.updateNameById(
                testedParsedInformation.getId(),
                testedParsedInformation.getName())
        ).thenReturn(1);
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

        testedProductService.saveParsedProduct(testedParsedInformation);

        assertEquals(PriceChange.DOWN, mockedPriceFromDb.getPriceChange());
        verify(productPriceRepository).save(mockedPriceFromDb);
        verify(productInformationRepository)
                .updateNameById(testedParsedInformation.getId(), testedParsedInformation.getName());
    }

    @SneakyThrows
    @Test
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

        when(productPriceRepository.findById(testedParsedPrice.getId())).thenReturn(Optional.empty());

        assertThrows(
                ServerException.class,
                () -> testedProductService.saveParsedProduct(testedParsedInformation)
        );
    }

    @SneakyThrows
    @Test
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

        when(productPriceRepository.findById(testedParsedPrice.getId())).thenReturn(Optional.of(mockedPriceFromDb));
        when(productPriceRepository.findLastPriceByProduct(productFromDb)).thenReturn(Optional.empty());

        assertThrows(
                ServerException.class,
                () -> testedProductService.saveParsedProduct(testedParsedInformation)
        );
    }
}