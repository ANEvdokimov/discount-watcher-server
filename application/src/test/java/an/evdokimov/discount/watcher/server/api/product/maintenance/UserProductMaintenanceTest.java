package an.evdokimov.discount.watcher.server.api.product.maintenance;

import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.UserProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.LentaProductPriceResponse;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.api.product.dto.response.UserProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.LentaProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.mapper.product.UserProductMapper;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import an.evdokimov.discount.watcher.server.service.product.UserProductService;
import an.evdokimov.discount.watcher.server.service.shop.ShopService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = UserProductMaintenanceImpl.class)
class UserProductMaintenanceTest {
    @MockBean
    private UserProductService userProductService;
    @MockBean
    private ShopService shopService;
    @MockBean
    private UserProductMapper userProductMapper;


    @Autowired
    private UserProductMaintenance testedMaintenance;

    @Test
    @DisplayName("get all UserProducts")
    void getUserProducts_allProducts_collectionOfProducts() {
        LentaProductPrice price1 = LentaProductPrice.builder().id(1L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price2 = LentaProductPrice.builder().id(2L).price(BigDecimal.valueOf(5)).build();

        LentaProductPriceResponse priceResponse1 = LentaProductPriceResponse.builder()
                .id(price1.getId())
                .price(price1.getPrice())
                .build();
        LentaProductPriceResponse priceResponse2 = LentaProductPriceResponse.builder()
                .id(price2.getId())
                .price(price2.getPrice())
                .build();

        Product testProduct1 = Product.builder()
                .id(1L)
                .prices(List.of(price2))
                .build();

        LentaProductPrice price3 = LentaProductPrice.builder().id(3L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price4 = LentaProductPrice.builder().id(4L).price(BigDecimal.valueOf(5)).build();

        LentaProductPriceResponse priceResponse3 = LentaProductPriceResponse.builder()
                .id(price3.getId())
                .price(price3.getPrice())
                .build();
        LentaProductPriceResponse priceResponse4 = LentaProductPriceResponse.builder()
                .id(price4.getId())
                .price(price4.getPrice())
                .build();

        Product testProduct2 = Product.builder()
                .id(2L)
                .prices(List.of(price4))
                .build();

        User userWithProducts = User.builder().login("login").build();

        UserProduct userProduct1 = UserProduct.builder()
                .user(userWithProducts)
                .product(testProduct1)
                .build();
        UserProduct userProduct2 = UserProduct.builder()
                .user(userWithProducts)
                .product(testProduct2)
                .build();

        UserProductResponse response1 = UserProductResponse.builder()
                .id(userProduct1.getId())
                .product(ProductResponse.builder()
                        .id(testProduct1.getId())
                        .lastPrice(priceResponse2)
                        .build())
                .build();
        UserProductResponse response2 = UserProductResponse.builder()
                .id(userProduct1.getId())
                .product(ProductResponse.builder()
                        .id(testProduct2.getId())
                        .lastPrice(priceResponse4)
                        .build())
                .build();

        when(userProductMapper.map(refEq(userProduct1))).thenReturn(response1);
        when(userProductMapper.map(refEq(userProduct2))).thenReturn(response2);
        when(userProductService.getAll(eq(userWithProducts), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of(
                        userProduct1,
                        userProduct2
                ));

        Collection<UserProductResponse> returnedProducts =
                testedMaintenance.getAll(userWithProducts, false, true, true, true);
        assertThat(
                returnedProducts,
                containsInAnyOrder(response1, response2)
        );
    }

    @Test
    @DisplayName("get active UserProducts")
    void getUserProducts_activeProducts_collectionOfProducts() {
        LentaProductPrice price1 = LentaProductPrice.builder().id(1L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price2 = LentaProductPrice.builder().id(2L).price(BigDecimal.valueOf(5)).build();

        LentaProductPriceResponse priceResponse1 = LentaProductPriceResponse.builder()
                .id(price1.getId())
                .price(price1.getPrice())
                .build();
        LentaProductPriceResponse priceResponse2 = LentaProductPriceResponse.builder()
                .id(price2.getId())
                .price(price2.getPrice())
                .build();

        Product testProduct1 = Product.builder()
                .id(1L)
                .prices(List.of(price2))
                .build();

        LentaProductPrice price3 = LentaProductPrice.builder().id(3L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price4 = LentaProductPrice.builder().id(4L).price(BigDecimal.valueOf(5)).build();

        LentaProductPriceResponse priceResponse3 = LentaProductPriceResponse.builder()
                .id(price3.getId())
                .price(price3.getPrice())
                .build();
        LentaProductPriceResponse priceResponse4 = LentaProductPriceResponse.builder()
                .id(price4.getId())
                .price(price4.getPrice())
                .build();

        Product testProduct2 = Product.builder()
                .id(2L)
                .prices(List.of(price4))
                .build();

        User userWithProducts = User.builder().login("login").build();

        UserProduct userProduct1 = UserProduct.builder()
                .user(userWithProducts)
                .product(testProduct1)
                .build();
        UserProduct userProduct2 = UserProduct.builder()
                .user(userWithProducts)
                .product(testProduct2)
                .build();

        UserProductResponse response1 = UserProductResponse.builder()
                .id(userProduct1.getId())
                .product(ProductResponse.builder()
                        .id(testProduct1.getId())
                        .lastPrice(priceResponse2)
                        .build())
                .build();
        UserProductResponse response2 = UserProductResponse.builder()
                .id(userProduct1.getId())
                .product(ProductResponse.builder()
                        .id(testProduct2.getId())
                        .lastPrice(priceResponse4)
                        .build())
                .build();

        when(userProductMapper.map(refEq(userProduct1))).thenReturn(response1);
        when(userProductMapper.map(refEq(userProduct2))).thenReturn(response2);
        when(userProductService.getAll(userWithProducts, true, true, true, true))
                .thenReturn(List.of(
                        userProduct1,
                        userProduct2
                ));

        Collection<UserProductResponse> returnedProducts =
                testedMaintenance.getAll(
                        userWithProducts,
                        true,
                        true,
                        true,
                        true
                );

        assertThat(
                returnedProducts,
                containsInAnyOrder(response1, response2)
        );
    }

    @Test
    @DisplayName("get all UserProducts in shop")
    void getUserProductsInShop_allProducts_lotsOfProducts() throws ServerException {
        Product product1 = Product.builder().id(1L).build();
        Product product2 = Product.builder().id(2L).build();
        Product product3 = Product.builder().id(3L).build();
        Product product4 = Product.builder().id(4L).build();

        User userWithProducts = User.builder().login("login").build();

        UserProduct userProduct1 = UserProduct.builder()
                .user(userWithProducts)
                .product(product1)
                .build();
        UserProduct userProduct2 = UserProduct.builder()
                .user(userWithProducts)
                .product(product2)
                .build();
        UserProduct userProduct3 = UserProduct.builder()
                .user(userWithProducts)
                .product(product3)
                .build();
        UserProduct userProduct4 = UserProduct.builder()
                .user(userWithProducts)
                .product(product4)
                .build();

        UserProductResponse response1 = UserProductResponse.builder()
                .id(userProduct1.getId())
                .product(ProductResponse.builder().id(product1.getId()).build())
                .build();
        UserProductResponse response2 = UserProductResponse.builder()
                .id(userProduct2.getId())
                .product(ProductResponse.builder().id(product2.getId()).build())
                .build();
        UserProductResponse response3 = UserProductResponse.builder()
                .id(userProduct3.getId())
                .product(ProductResponse.builder().id(product3.getId()).build())
                .build();
        UserProductResponse response4 = UserProductResponse.builder()
                .id(userProduct4.getId())
                .product(ProductResponse.builder().id(product4.getId()).build())
                .build();

        when(userProductMapper.map(refEq(userProduct1))).thenReturn(response1);
        when(userProductMapper.map(refEq(userProduct2))).thenReturn(response2);
        when(userProductMapper.map(refEq(userProduct3))).thenReturn(response3);
        when(userProductMapper.map(refEq(userProduct4))).thenReturn(response4);

        Shop shop = Shop.builder().id(1L).name("shop").build();
        when(shopService.getById(shop.getId())).thenReturn(shop);

        when(userProductService.getAll(any(), eq(false), anyBoolean(), anyBoolean(), anyBoolean(), any()))
                .thenReturn(List.of(userProduct1));
        when(userProductService.getAll(any(), eq(true), anyBoolean(), anyBoolean(), anyBoolean(), any()))
                .thenReturn(List.of(userProduct3));

        Collection<UserProductResponse> returnedProducts = testedMaintenance.getAll(
                new User(),
                false,
                false,
                false,
                false,
                shop.getId()
        );

        assertThat(returnedProducts, contains(response1));
    }

    @Test
    @DisplayName("get active UserProducts in shop")
    void getUserProductsInShop_activeProducts_lotsOfProducts() throws ServerException {
        Product product1 = Product.builder().id(1L).build();
        Product product2 = Product.builder().id(2L).build();
        Product product3 = Product.builder().id(3L).build();
        Product product4 = Product.builder().id(4L).build();

        User userWithProducts = User.builder().login("login").build();

        UserProduct userProduct1 = UserProduct.builder()
                .user(userWithProducts)
                .product(product1)
                .build();
        UserProduct userProduct2 = UserProduct.builder()
                .user(userWithProducts)
                .product(product2)
                .build();
        UserProduct userProduct3 = UserProduct.builder()
                .user(userWithProducts)
                .product(product3)
                .build();
        UserProduct userProduct4 = UserProduct.builder()
                .user(userWithProducts)
                .product(product4)
                .build();

        UserProductResponse response1 = UserProductResponse.builder()
                .id(userProduct1.getId())
                .product(ProductResponse.builder().id(product1.getId()).build())
                .build();
        UserProductResponse response2 = UserProductResponse.builder()
                .id(userProduct2.getId())
                .product(ProductResponse.builder().id(product2.getId()).build())
                .build();
        UserProductResponse response3 = UserProductResponse.builder()
                .id(userProduct3.getId())
                .product(ProductResponse.builder().id(product3.getId()).build())
                .build();
        UserProductResponse response4 = UserProductResponse.builder()
                .id(userProduct4.getId())
                .product(ProductResponse.builder().id(product4.getId()).build())
                .build();

        when(userProductMapper.map(refEq(userProduct1))).thenReturn(response1);
        when(userProductMapper.map(refEq(userProduct2))).thenReturn(response2);
        when(userProductMapper.map(refEq(userProduct3))).thenReturn(response3);
        when(userProductMapper.map(refEq(userProduct4))).thenReturn(response4);

        Shop shop = Shop.builder().id(1L).name("shop").build();
        when(shopService.getById(shop.getId())).thenReturn(shop);

        when(userProductService.getAll(any(), eq(false), anyBoolean(), anyBoolean(), anyBoolean(), any()))
                .thenReturn(List.of(userProduct1));
        when(userProductService.getAll(any(), eq(true), anyBoolean(), anyBoolean(), anyBoolean(), any()))
                .thenReturn(List.of(userProduct3));

        Collection<UserProductResponse> returnedProducts =
                testedMaintenance.getAll(
                        new User(),
                        true,
                        true,
                        true,
                        true,
                        shop.getId()
                );

        assertThat(returnedProducts, contains(response3));
    }

    @SneakyThrows
    @Test
    @DisplayName("get all UserProducts in nonexistent shop")
    void getUserProductsInShop_wrongShopId_ServerException() {
        when(shopService.getById(anyLong())).thenThrow(ServerErrorCode.SHOP_NOT_FOUND.getException());

        assertThrows(
                ServerException.class,
                () -> testedMaintenance.getAll(
                        new User(),
                        true,
                        true,
                        true,
                        true,
                        666L
                )
        );
    }

    @SneakyThrows
    @Test
    @DisplayName("update UserProduct")
    void update_validUserProduct_updatedProduct() {
        User user = User.builder().login("login").build();
        Product product = Product.builder().id(1L).build();

        UserProduct userProduct = UserProduct.builder()
                .id(666L)
                .user(user)
                .product(product)
                .monitorDiscount(true)
                .monitorAvailability(true)
                .monitorPriceChanges(true)
                .build();

        UserProductRequest updatedUserProduct = UserProductRequest.builder()
                .id(666L)
                .productId(product.getId())
                .monitorDiscount(false)
                .monitorAvailability(false)
                .monitorPriceChanges(false)
                .build();

        when(userProductMapper.map(updatedUserProduct)).thenReturn(userProduct);

        testedMaintenance.update(user, updatedUserProduct);

        verify(userProductService).update(user, userProduct);
    }

    @Test
    @DisplayName("delete UserProduct")
    void delete_userProduct_deletedProduct() throws ServerException {
        User user = User.builder().login("login").build();

        testedMaintenance.delete(user, 666L);

        verify(userProductService).delete(user, 666L);
    }
}