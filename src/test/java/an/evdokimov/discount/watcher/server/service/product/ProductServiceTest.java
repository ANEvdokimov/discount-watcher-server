package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.LentaProductPriceResponse;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductPriceResponse;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.LentaProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductInformationRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductPriceRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.parser.ParserException;
import an.evdokimov.discount.watcher.server.parser.ParserFactory;
import an.evdokimov.discount.watcher.server.parser.ParserFactoryException;
import an.evdokimov.discount.watcher.server.parser.downloader.PageDownloaderException;
import an.evdokimov.discount.watcher.server.parser.lenta.LentaParser;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductServiceTest {
    @MockBean
    private LentaParser lentaParser;

    @MockBean
    private ParserFactory parserFactory;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private ProductPriceRepository productPriceRepository;

    @MockBean
    private ProductInformationRepository productInformationRepository;

    @MockBean
    private ShopRepository shopRepository;

    @Autowired
    private ProductService productService;

    @BeforeEach
    public void mockFactory() throws MalformedURLException, ParserFactoryException {
        URL urlLenta = new URL("https://lenta.com");
        when(parserFactory.getParser(urlLenta)).thenReturn(lentaParser);
        when(parserFactory.getParser(not(eq(urlLenta)))).thenThrow(new ParserFactoryException());
    }

    @Test
    void addProduct_LentaProduct_LentaProductResponse() throws ParserException, PageDownloaderException, MalformedURLException, ServerException {
        LentaProductPrice productPrice = LentaProductPrice.builder()
                .price(BigDecimal.valueOf(100))
                .priceWithDiscount(BigDecimal.valueOf(50))
                .priceWithCard(BigDecimal.valueOf(50))
                .build();
        Product product = Product.builder()
                .prices(List.of(productPrice))
                .build();
        productPrice.setProduct(product);

        LentaProductPriceResponse expectedProductPrice = LentaProductPriceResponse.builder()
                .price(BigDecimal.valueOf(100))
                .priceWithDiscount(BigDecimal.valueOf(50))
                .priceWithCard(BigDecimal.valueOf(50))
                .build();
        ProductResponse expectedProduct = ProductResponse.builder()
                .prices(List.of(expectedProductPrice))
                .build();

        when(shopRepository.findById(666L)).thenReturn(Optional.of(new Shop()));
        when(lentaParser.parse(any(URL.class), any())).thenReturn(product);

        ProductResponse result = productService.addProduct(new NewProductRequest(new URL("https://lenta.com"), 666L));

        assertAll(
                () -> assertEquals(expectedProduct, result),
                () -> verify(productRepository, times(1)).save(product)
        );
    }

    @Test
    void addProduct_LentaProduct_PageDownloaderException() throws ParserException, PageDownloaderException {
        when(shopRepository.findById(666L)).thenReturn(Optional.of(new Shop()));
        when(lentaParser.parse(any(URL.class), any(Shop.class))).thenThrow(PageDownloaderException.class);

        assertAll(
                () -> assertThrows(
                        ServerException.class,
                        () -> productService.addProduct(new NewProductRequest(new URL("https://lenta.com"), 666L))
                ),
                () -> verify(productRepository, times(0)).save(any(Product.class))
        );
    }

    @Test
    void addProduct_LentaProduct_ParserException() throws ParserException, PageDownloaderException {
        when(shopRepository.findById(666L)).thenReturn(Optional.of(new Shop()));
        when(lentaParser.parse(any(URL.class), any(Shop.class))).thenThrow(ParserException.class);

        assertAll(
                () -> assertThrows(
                        ServerException.class,
                        () -> productService.addProduct(new NewProductRequest(new URL("https://lenta.com"), 666L))
                ),
                () -> verify(productRepository, times(0)).save(any(Product.class))
        );
    }

    @Test
    void getProduct_Product_validLentaProductWithPriceHistory() throws ServerException {
        LentaProductPrice price1 = LentaProductPrice.builder().id(1L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price2 = LentaProductPrice.builder().id(2L).price(BigDecimal.valueOf(5)).build();

        Product testProduct = Product.builder()
                .id(666L)
                .prices(List.of(price2))
                .build();
        Product testProductWithPriceHistory = Product.builder()
                .id(666L)
                .prices(List.of(price2, price1))
                .build();
        when(productRepository.findByIdWithLastPrice(666L)).thenReturn(Optional.of(testProduct));
        when(productRepository.findById(666L)).thenReturn(Optional.of(testProductWithPriceHistory));

        ProductResponse returnedProduct = productService.getProduct(666L, true);

        assertAll(
                () -> assertEquals(testProduct.getId(), returnedProduct.getId()),
                () -> assertThat(
                        returnedProduct.getPrices().stream().map(ProductPriceResponse::getId).toList(),
                        Matchers.contains(price2.getId(), price1.getId())
                )
        );
    }

    @Test
    void getProduct_Product_validLentaProductWithoutPriceHistory() throws ServerException {
        LentaProductPrice price1 = LentaProductPrice.builder().id(1L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price2 = LentaProductPrice.builder().id(2L).price(BigDecimal.valueOf(5)).build();

        Product testProduct = Product.builder()
                .id(666L)
                .prices(List.of(price2))
                .build();
        Product testProductWithPriceHistory = Product.builder()
                .id(666L)
                .prices(List.of(price2, price1))
                .build();
        when(productRepository.findByIdWithLastPrice(666L)).thenReturn(Optional.of(testProduct));
        when(productRepository.findById(666L)).thenReturn(Optional.of(testProductWithPriceHistory));

        ProductResponse returnedProduct = productService.getProduct(666L, false);

        assertAll(
                () -> assertEquals(testProduct.getId(), returnedProduct.getId()),
                () -> assertThat(
                        returnedProduct.getPrices().stream().map(ProductPriceResponse::getId).toList(),
                        Matchers.contains(price2.getId())
                )
        );
    }

    @Test
    void getProduct_nonexistentProductWithPriceHistory_ServerException() {
        when(productRepository.findById(666L)).thenReturn(Optional.empty());
        when(productRepository.findByIdWithLastPrice(666L)).thenReturn(Optional.empty());

        assertAll(
                () -> assertThrows(ServerException.class, () -> productService.getProduct(666L, true)),
                () -> verify(productRepository, times(1)).findById(666L),
                () -> verify(productRepository, times(0)).findByIdWithLastPrice(666L)
        );
    }

    @Test
    void getProduct_nonexistentProductWithoutPriceHistory_ServerException() {
        when(productRepository.findById(666L)).thenReturn(Optional.empty());
        when(productRepository.findByIdWithLastPrice(666L)).thenReturn(Optional.empty());

        assertAll(
                () -> assertThrows(ServerException.class, () -> productService.getProduct(666L, false)),
                () -> verify(productRepository, times(0)).findById(666L),
                () -> verify(productRepository, times(1)).findByIdWithLastPrice(666L)
        );
    }

    @Test
    void getUserProducts_ProductsWithPriceHistory_collectionOfProducts() {
        LentaProductPrice price1 = LentaProductPrice.builder().id(1L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price2 = LentaProductPrice.builder().id(2L).price(BigDecimal.valueOf(5)).build();

        Product testProduct1 = Product.builder()
                .id(1L)
                .prices(List.of(price2))
                .build();
        Product testProduct1WithPriceHistory = Product.builder()
                .id(1L)
                .prices(List.of(price2, price1))
                .build();

        LentaProductPrice price3 = LentaProductPrice.builder().id(3L).price(BigDecimal.valueOf(100)).build();
        LentaProductPrice price4 = LentaProductPrice.builder().id(4L).price(BigDecimal.valueOf(200)).build();

        Product testProduct2 = Product.builder()
                .id(2L)
                .prices(List.of(price4))
                .build();
        Product testProduct2WithPriceHistory = Product.builder()
                .id(2L)
                .prices(List.of(price4, price3))
                .build();


        User userWithProducts = User.builder().id(666L).build();
        when(productRepository.findAllUsersProducts(userWithProducts)).thenReturn(List.of(
                testProduct1WithPriceHistory,
                testProduct2WithPriceHistory
        ));
        when(productRepository.findAllUsersProductsWithLastPrice(userWithProducts)).thenReturn(List.of(
                testProduct1,
                testProduct2
        ));

        Collection<ProductResponse> returnedProducts = productService.getUserProducts(userWithProducts, true);
        assertAll(
                () -> assertThat(
                        returnedProducts.stream().map(ProductResponse::getId).toList(),
                        containsInAnyOrder(testProduct1WithPriceHistory.getId(), testProduct2WithPriceHistory.getId())
                ),
                () -> assertThat(
                        returnedProducts.stream()
                                .filter(productResponse -> productResponse.getId().equals(testProduct1.getId()))
                                .toList().get(0).getPrices()
                                .stream().map(ProductPriceResponse::getId).toList(),
                        containsInAnyOrder(price1.getId(), price2.getId())
                ),
                () -> assertThat(
                        returnedProducts.stream()
                                .filter(productResponse -> productResponse.getId().equals(testProduct2.getId()))
                                .toList().get(0).getPrices()
                                .stream().map(ProductPriceResponse::getId).toList(),
                        containsInAnyOrder(price3.getId(), price4.getId())
                )
        );
    }

    @Test
    void getUserProducts_ProductsWithoutPriceHistory_collectionOfProducts() {
        LentaProductPrice price1 = LentaProductPrice.builder().id(1L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price2 = LentaProductPrice.builder().id(2L).price(BigDecimal.valueOf(5)).build();

        Product testProduct1 = Product.builder()
                .id(1L)
                .prices(List.of(price2))
                .build();
        Product testProduct1WithPriceHistory = Product.builder()
                .id(1L)
                .prices(List.of(price2, price1))
                .build();

        LentaProductPrice price3 = LentaProductPrice.builder().id(3L).price(BigDecimal.valueOf(100)).build();
        LentaProductPrice price4 = LentaProductPrice.builder().id(4L).price(BigDecimal.valueOf(200)).build();

        Product testProduct2 = Product.builder()
                .id(2L)
                .prices(List.of(price4))
                .build();
        Product testProduct2WithPriceHistory = Product.builder()
                .id(2L)
                .prices(List.of(price4, price3))
                .build();


        User userWithProducts = User.builder().id(666L).build();
        when(productRepository.findAllUsersProducts(userWithProducts)).thenReturn(List.of(
                testProduct1WithPriceHistory,
                testProduct2WithPriceHistory
        ));
        when(productRepository.findAllUsersProductsWithLastPrice(userWithProducts)).thenReturn(List.of(
                testProduct1,
                testProduct2
        ));

        Collection<ProductResponse> returnedProducts = productService.getUserProducts(userWithProducts, false);
        assertAll(
                () -> assertThat(
                        returnedProducts.stream().map(ProductResponse::getId).toList(),
                        containsInAnyOrder(testProduct1WithPriceHistory.getId(), testProduct2WithPriceHistory.getId())
                ),
                () -> assertThat(
                        returnedProducts.stream()
                                .filter(productResponse -> productResponse.getId().equals(testProduct1.getId()))
                                .toList().get(0).getPrices()
                                .stream().map(ProductPriceResponse::getId).toList(),
                        contains(price2.getId())
                ),
                () -> assertThat(
                        returnedProducts.stream()
                                .filter(productResponse -> productResponse.getId().equals(testProduct2.getId()))
                                .toList().get(0).getPrices()
                                .stream().map(ProductPriceResponse::getId).toList(),
                        contains(price4.getId())
                )
        );
    }

    @Test
    void updateProduct_validProduct_updatedProducts() throws ParserException, PageDownloaderException, ServerException,
            MalformedURLException {
        ProductPrice oldPrice = ProductPrice.builder().id(1L).price(BigDecimal.valueOf(100)).build();
        Product product = Product.builder()
                .id(666L)
                .shop(Shop.builder().id(1L).build())
                .productInformation(ProductInformation.builder().id(1L).name("product")
                        .url(new URL("https://lenta.com")).build())
                .prices(List.of(oldPrice))
                .build();

        LentaProductPrice parsedProductPrice = LentaProductPrice.builder().price(BigDecimal.valueOf(5000)).build();
        Product parsedProduct = Product.builder()
                .id(666L)
                .shop(Shop.builder().id(1L).build())
                .productInformation(ProductInformation.builder().id(1L).name("product")
                        .url(new URL("https://lenta.com")).build())
                .prices(List.of(oldPrice, parsedProductPrice))
                .build();

        when(lentaParser.parse(product)).thenReturn(parsedProductPrice);

        assertEquals(parsedProduct, productService.updateProduct(product));
    }

    @Test
    void updateProduct_wrongUrl_ServerException() throws MalformedURLException {
        Product product = Product.builder()
                .shop(Shop.builder().id(1L).build())
                .productInformation(ProductInformation.builder().id(1L).name("product")
                        .url(new URL("https://not-lenta.com")).build())
                .prices(List.of(ProductPrice.builder().id(1L).price(BigDecimal.valueOf(100)).build()))
                .build();

        assertThrows(ServerException.class, () -> productService.updateProduct(product));
    }
}