package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.database.product.model.LentaProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductInformationRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductPriceRepository;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.parser.ParserException;
import an.evdokimov.discount.watcher.server.parser.ParserFactory;
import an.evdokimov.discount.watcher.server.parser.ParserFactoryException;
import an.evdokimov.discount.watcher.server.parser.downloader.PageDownloaderException;
import an.evdokimov.discount.watcher.server.parser.lenta.LentaParser;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ProductServiceIntegrationTest {
    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductInformationRepository productInformationRepository;

    @Autowired
    private ProductPriceRepository productPriceRepository;

    @MockBean
    private LentaParser lentaParser;

    @MockBean
    private ParserFactory parserFactory;


    @BeforeEach
    public void mockFactory() throws MalformedURLException, ParserFactoryException {
        URL urlLenta = new URL("https://lenta.com");
        when(parserFactory.getParser(urlLenta)).thenReturn(lentaParser);
        when(parserFactory.getParser(not(eq(urlLenta)))).thenThrow(new ParserFactoryException());
    }

    @Test
    @Transactional
    void updateProduct_validProduct_updatedProductInDb() throws MalformedURLException, ParserException,
            PageDownloaderException, ServerException {
        ProductInformation productInformation = ProductInformation.builder()
                .name("product")
                .url(new URL("https://lenta.com"))
                .build();
        productInformationRepository.saveAndFlush(productInformation);

        Shop shop = Shop.builder().name("lenta1").build();
        shopRepository.saveAndFlush(shop);

        LentaProductPrice price1 = LentaProductPrice.builder().price(BigDecimal.valueOf(100)).build();
        productPriceRepository.saveAndFlush(price1);

        Product product = Product.builder()
                .shop(shop)
                .productInformation(productInformation)
                .prices(List.of(price1))
                .build();
        price1.setProduct(product);
        product = productRepository.saveAndFlush(product);

        when(lentaParser.parse(product)).thenReturn(LentaProductPrice.builder()
                .product(product).price(BigDecimal.valueOf(5000)).build());

        productService.updateProduct(product);

        assertThat(
                productRepository.findById(product.getId()).get()
                        .getPrices().stream().map(ProductPrice::getPrice).collect(Collectors.toList()),
                contains(BigDecimal.valueOf(100), BigDecimal.valueOf(5000))
        );
    }
}
