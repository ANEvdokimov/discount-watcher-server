package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ProductServiceImpl.class)
class ProductServiceTest {
    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ProductService testedProductService;

    @SneakyThrows
    @Test
    @DisplayName("getting product by id")
    void getProduct_Product_validLentaProduct() {
        Product testProduct = Product.builder()
                .id(666L)
                .build();

        when(productRepository.findById(666L)).thenReturn(Optional.of(testProduct));

        Product actual = testedProductService.getProduct(testProduct.getId());

        assertEquals(testProduct, actual);
    }

    @Test
    @DisplayName("try getting nonexistent product by id")
    void getProduct_nonexistentProduct_ServerException() {
        when(productRepository.findById(666L)).thenReturn(Optional.empty());

        assertThrows(ServerException.class, () -> testedProductService.getProduct(666L));
        verify(productRepository).findById(666L);
    }

    @Test
    @DisplayName("getting products by info and shop")
    void getOrCreateByProductInformationAndShop_existentProduct_product() {
        ProductInformation information = ProductInformation.builder()
                .id(111L)
                .build();
        Shop shop = Shop.builder()
                .id(222L)
                .build();

        Product testProduct = Product.builder()
                .id(666L)
                .productInformation(information)
                .shop(shop)
                .build();

        when(productRepository.findByProductInformationAndShop(information, shop)).thenReturn(Optional.of(testProduct));

        Product actual = testedProductService.getOrCreateByProductInformationAndShop(information, shop);

        assertEquals(testProduct, actual);
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("crating products by info and shop")
    void getOrCreateByProductInformationAndShop_nonexistentProduct_product() {
        ProductInformation information = ProductInformation.builder()
                .id(111L)
                .build();
        Shop shop = Shop.builder()
                .id(222L)
                .build();

        Product testProduct = Product.builder()
                .id(666L)
                .productInformation(information)
                .shop(shop)
                .build();

        when(productRepository.findByProductInformationAndShop(information, shop)).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenReturn(testProduct);

        Product actual = testedProductService.getOrCreateByProductInformationAndShop(information, shop);

        assertEquals(testProduct, actual);
        verify(productRepository).save(any());
    }

    @Test
    @DisplayName("getting tracked products")
    void getAllTrackedProducts_twoProducts_listOfProducts() {
        Product testProduct1 = Product.builder()
                .id(1L)
                .build();
        Product testProduct2 = Product.builder()
                .id(2L)
                .build();

        when(productRepository.findAllTrackedProducts()).thenReturn(List.of(testProduct1, testProduct2));

        Collection<Product> actual = testedProductService.getAllTrackedProducts();

        assertThat(actual, contains(testProduct1, testProduct2));
    }

    @Test
    @DisplayName("getting nonexistent tracked products")
    void getAllTrackedProducts_zeroProducts_listOfProducts() {
        when(productRepository.findAllTrackedProducts()).thenReturn(List.of());

        Collection<Product> actual = testedProductService.getAllTrackedProducts();

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("adding product")
    void addProduct_LentaProduct_LentaProductResponse() {
        Product testProduct = Product.builder()
                .id(666L)
                .build();

        testedProductService.addProduct(testProduct);

        verify(productRepository).save(testProduct);
    }
}