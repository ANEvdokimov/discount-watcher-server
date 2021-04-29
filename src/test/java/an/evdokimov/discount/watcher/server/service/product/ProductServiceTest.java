package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.LentaProductResponse;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.LentaProduct;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
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
import java.util.Optional;

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
    void addParser_LentaProduct_PageDownloaderException() throws ParserException, PageDownloaderException {
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
    void addParser_LentaProduct_ParserException() throws ParserException, PageDownloaderException {
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
}