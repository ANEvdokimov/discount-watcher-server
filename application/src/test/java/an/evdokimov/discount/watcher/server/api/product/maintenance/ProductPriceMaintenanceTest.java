package an.evdokimov.discount.watcher.server.api.product.maintenance;

import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductPriceResponse;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.mapper.product.ProductPriceMapper;
import an.evdokimov.discount.watcher.server.service.product.ProductPriceService;
import an.evdokimov.discount.watcher.server.service.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ProductPriceMaintenanceImpl.class)
class ProductPriceMaintenanceTest {
    @MockBean
    private ProductPriceService priceService;
    @MockBean
    private ProductService productService;
    @MockBean
    private ProductPriceMapper priceMapper;


    @Autowired
    private ProductPriceMaintenance testedMaintenance;


    @Test
    @DisplayName("get prices by existent products")
    void getByProducts_existentProducts_listOfPrices() throws ServerException {
        Product product = Product.builder().id(666L).build();
        ProductPrice price = ProductPrice.builder().id(1L).product(product).build();
        List<ProductPrice> prices = List.of(price);
        ProductPriceResponse response = ProductPriceResponse.builder().id(price.getId()).build();
        List<ProductPriceResponse> expected = List.of(response);

        when(productService.getById(product.getId())).thenReturn(product);
        when(priceService.getByProduct(eq(product), anyBoolean(), any())).thenReturn(prices);
        when(priceMapper.map(price)).thenReturn(response);

        List<ProductPriceResponse> actual = testedMaintenance.getByProduct(product.getId(), true, LocalDate.MIN);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("get prices by nonexistent products")
    void getByProducts_nonexistentProducts_exception() throws ServerException {
        when(productService.getById(666L)).thenThrow(ServerErrorCode.PRODUCT_NOT_FOUND.getException());

        assertThrows(ServerException.class, () -> testedMaintenance.getByProduct(666L, true, LocalDate.MIN));
    }
}