package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.LentaProductResponse;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.LentaProduct;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.UserProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.parser.ParserException;
import an.evdokimov.discount.watcher.server.parser.ParserFactory;
import an.evdokimov.discount.watcher.server.parser.ParserFactoryException;
import an.evdokimov.discount.watcher.server.parser.downloader.PageDownloaderException;
import an.evdokimov.discount.watcher.server.parser.lenta.LentaParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest()
class ProductServiceTest {
    @MockBean
    private LentaParser lentaParser;

    @MockBean
    private ParserFactory parserFactory;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private UserProductRepository userProductRepository;

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
        LentaProduct product = LentaProduct.builder()
                .price(BigDecimal.valueOf(100))
                .priceWithDiscount(BigDecimal.valueOf(50))
                .priceWithCard(BigDecimal.valueOf(50))
                .build();

        LentaProductResponse expectedProduct = LentaProductResponse.builder()
                .price(BigDecimal.valueOf(100))
                .priceWithDiscount(BigDecimal.valueOf(50))
                .priceWithCard(BigDecimal.valueOf(50))
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
    void getProduct_LentaProduct_validLentaProduct() throws ServerException {
        LentaProduct testProduct = LentaProduct.builder().id(666L).build();
        when(productRepository.findById(666L)).thenReturn(Optional.of(testProduct));

        ProductResponse returnedProduct = productService.getProduct(666L);

        ProductResponse expectedProduct = LentaProductResponse.builder().id(666L).build();
        assertEquals(expectedProduct, returnedProduct);
    }

    @Test
    void getProduct_nonexistentProduct_ServerException() {
        when(productRepository.findById(666L)).thenReturn(Optional.empty());

        assertAll(
                () -> assertThrows(ServerException.class, () -> productService.getProduct(666L)),
                () -> verify(productRepository, times(1)).findById(666L)
        );
    }

    @Test
    void getProducts_LentaProducts_collectionOfProducts() {
        User userWithProducts = User.builder().id(666L).build();
        when(userProductRepository.findByUser(userWithProducts)).thenReturn(List.of(
                UserProduct.builder().product(LentaProduct.builder().id(1L).build()).build(),
                UserProduct.builder().product(LentaProduct.builder().id(2L).build()).build(),
                UserProduct.builder().product(LentaProduct.builder().id(3L).build()).build()
        ));

        List<LentaProductResponse> expectedProducts = List.of(
                LentaProductResponse.builder().id(1L).build(),
                LentaProductResponse.builder().id(2L).build(),
                LentaProductResponse.builder().id(3L).build()
        );

        assertThat(productService.getUserProducts(userWithProducts), containsInAnyOrder(expectedProducts.toArray()));
    }

    @Test
    void getProducts_nonexistentLentaProducts_emptyCollection() {
        User userWithoutProducts = User.builder().id(666L).build();
        when(userProductRepository.findByUser(userWithoutProducts)).thenReturn(Collections.emptyList());

        assertAll(
                () -> assertEquals(0, productService.getUserProducts(userWithoutProducts).size()),
                () -> verify(userProductRepository, times(1)).findByUser(userWithoutProducts)
        );
    }
}