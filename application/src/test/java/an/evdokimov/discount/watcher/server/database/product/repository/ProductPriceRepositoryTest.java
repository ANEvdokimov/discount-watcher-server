package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.LentaProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductPriceRepositoryTest {
    @Autowired
    private ProductPriceRepository repository;

    @AfterEach
    void clearDb() {
        repository.deleteAll();
    }

    @Test
    void findById_LentaProductPrice_instanceOfLentaProductPrice() {
        LentaProductPrice lentaProductPrice = LentaProductPrice.builder()
                .price(BigDecimal.valueOf(1000))
                .priceWithCard(BigDecimal.valueOf(500))
                .priceWithDiscount(BigDecimal.valueOf(500))
                .build();
        LentaProductPrice savedLentaProductPrice = repository.saveAndFlush(lentaProductPrice);

        assertTrue(repository.findById(savedLentaProductPrice.getId()).get() instanceof LentaProductPrice);
    }

    @Test
    void findById_ProductPrice_instanceOfProductPrice() {
        ProductPrice productPrice = ProductPrice.builder()
                .price(BigDecimal.valueOf(1000))
                .priceWithDiscount(BigDecimal.valueOf(500))
                .build();
        ProductPrice savedLentaProductPrice = repository.saveAndFlush(productPrice);

        assertFalse(repository.findById(savedLentaProductPrice.getId()).get() instanceof LentaProductPrice);
    }
}