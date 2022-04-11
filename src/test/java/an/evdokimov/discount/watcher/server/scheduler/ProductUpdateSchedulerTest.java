package an.evdokimov.discount.watcher.server.scheduler;

import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.service.product.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductUpdateSchedulerTest {
    @Autowired
    private ProductUpdateScheduler productUpdateScheduler;

    @MockBean
    private ProductServiceImpl productService;

    @MockBean
    private ProductRepository productRepository;

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

        when(productRepository.findAllActiveProducts()).thenReturn(List.of(product1, product2, product3));

        productUpdateScheduler.updateProduct();
        assertAll(
                () -> verify(productService, times(1)).updateProduct(product1),
                () -> verify(productService, times(1)).updateProduct(product2),
                () -> verify(productService, times(1)).updateProduct(product3)
        );
    }
}