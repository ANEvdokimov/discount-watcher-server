package an.evdokimov.discount.watcher.server.mapper.product;

import an.evdokimov.discount.watcher.server.database.product.model.ParsingStatus;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ProductPriceMapperImpl.class)
class ProductPriceMapperTest {
    @Autowired
    private ProductPriceMapper testedMapper;

    @Test
    void mapNewPrice() throws MalformedURLException {
        ProductInformation information = ProductInformation.builder()
                .url(new URL("https://example.com"))
                .build();
        Product product = Product.builder()
                .id(666L)
                .productInformation(information)
                .build();

        LocalDateTime creationDate = LocalDateTime.of(2023, 10, 8, 0, 0);
        ProductPrice expected = ProductPrice.builder()
                .product(product)
                .creationDate(creationDate)
                .parsingStatus(ParsingStatus.PROCESSING)
                .build();

        ProductPrice result = testedMapper.mapNewPrice(product, creationDate);

        assertEquals(expected, result);
    }
}