package an.evdokimov.discount.watcher.server.scheduler;

import an.evdokimov.discount.watcher.server.api.product.maintenance.ProductMaintenance;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ProductUpdateScheduler.class)
class ProductUpdateSchedulerTest {
    @MockBean
    private ProductMaintenance productMaintenance;
    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ProductUpdateScheduler testedProductUpdateScheduler;

    @Test
    void updateProduct_3Products_updatedProducts() {
        Product product1 = Product.builder()
                .shop(Shop.builder().id(1L).build())
                .productInformation(ProductInformation.builder().id(1L).name("product1").build())
                .prices(List.of(ProductPrice.builder().id(1L).price(BigDecimal.valueOf(100)).build()))
                .build();
        Product product2 = Product.builder()
                .shop(Shop.builder().id(2L).build())
                .productInformation(ProductInformation.builder().id(2L).name("product2").build())
                .prices(List.of(
                        ProductPrice.builder().id(2L).price(BigDecimal.valueOf(1000)).build(),
                        ProductPrice.builder().id(3L).price(BigDecimal.valueOf(666)).build()
                ))
                .build();
        Product product3 = Product.builder()
                .shop(Shop.builder().id(2L).build())
                .productInformation(ProductInformation.builder().id(3L).name("product3").build())
                .prices(List.of())
                .build();

        when(productRepository.findAllTrackedProducts()).thenReturn(List.of(product1, product2, product3));

        testedProductUpdateScheduler.updateProducts();

        verify(productMaintenance, times(1)).updateTrackedProducts();
    }
}