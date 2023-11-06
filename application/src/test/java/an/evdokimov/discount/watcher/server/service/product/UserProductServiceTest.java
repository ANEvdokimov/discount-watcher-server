package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.UserProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.LentaProductPriceResponse;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.api.product.dto.response.UserProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.LentaProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.product.repository.UserProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.mapper.product.UserProductMapper;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = UserProductServiceImpl.class)
public class UserProductServiceTest {
    @MockBean
    private UserProductRepository userProductRepository;
    @MockBean
    private ShopRepository shopRepository;
    @MockBean
    private UserProductMapper userProductMapper;

    @Autowired
    private UserProductServiceImpl testedService;

    @Test
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
        when(userProductRepository.findAllUserProducts(
                eq(userWithProducts), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(List.of(
                userProduct1,
                userProduct2
        ));

        Collection<UserProductResponse> returnedProducts =
                testedService.getUserProducts(userWithProducts, false, true, true, true);
        assertThat(
                returnedProducts,
                containsInAnyOrder(response1, response2)
        );
    }

    @Test
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
        when(userProductRepository.findActiveUserProducts(userWithProducts, true, true, true))
                .thenReturn(List.of(
                        userProduct1,
                        userProduct2
                ));

        Collection<UserProductResponse> returnedProducts =
                testedService.getUserProducts(
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
        when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));

        when(userProductRepository.findAllUserProductsInShop(
                any(), any(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(List.of(userProduct1));
        when(userProductRepository.findActiveUserProductsInShop(any(), any(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of(userProduct3));

        Collection<UserProductResponse> returnedProducts = testedService.getUserProductsInShop(
                new User(),
                shop.getId(),
                false,
                false,
                false,
                false
        );

        assertThat(returnedProducts, contains(response1));
    }

    @Test
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
        when(shopRepository.findById(shop.getId())).thenReturn(Optional.of(shop));

        when(userProductRepository.findActiveUserProductsInShop(
                any(), any(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(List.of(userProduct1));
        when(userProductRepository.findActiveUserProductsInShop(any(), any(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of(userProduct3));

        Collection<UserProductResponse> returnedProducts =
                testedService.getUserProductsInShop(
                        new User(),
                        shop.getId(),
                        true,
                        true,
                        true,
                        true
                );

        assertThat(returnedProducts, contains(response3));
    }

    @Test
    void getUserProductsInShop_wrongShopId_ServerException() {
        when(shopRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                ServerException.class,
                () -> testedService.getUserProductsInShop(
                        new User(),
                        666L,
                        true,
                        true,
                        true,
                        true
                )
        );
    }

    @SneakyThrows
    @Test
    void update_validUserProduct_updatedProduct() {
        User user = User.builder().login("login").build();
        Product product = Product.builder().id(1L).build();

        UserProduct userProductFromDb = UserProduct.builder()
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

        when(userProductRepository.findByIdAndUser(updatedUserProduct.getId(), user))
                .thenReturn(Optional.of(userProductFromDb));

        testedService.update(user, updatedUserProduct);

        assertEquals(updatedUserProduct.isMonitorDiscount(), userProductFromDb.isMonitorDiscount());
        assertEquals(updatedUserProduct.isMonitorAvailability(), userProductFromDb.isMonitorAvailability());
        assertEquals(updatedUserProduct.isMonitorPriceChanges(), userProductFromDb.isMonitorPriceChanges());
    }

    @SneakyThrows
    @Test
    void update_nonexistentUserProduct_ServerException() {
        User user = User.builder().login("login").build();
        Product product = Product.builder().id(1L).build();

        UserProductRequest updatedUserProduct = UserProductRequest.builder()
                .id(666L)
                .productId(product.getId())
                .monitorDiscount(false)
                .monitorAvailability(false)
                .monitorPriceChanges(false)
                .build();

        when(userProductRepository.findByIdAndUser(updatedUserProduct.getId(), user))
                .thenReturn(Optional.empty());

        assertThrows(ServerException.class,
                () -> testedService.update(user, updatedUserProduct));
    }

    @Test
    void delete_userProduct_deletedProduct() throws ServerException {
        User user = User.builder().login("login").build();
        Product product = Product.builder().id(1L).build();

        UserProduct userProductFromDb = UserProduct.builder()
                .id(666L)
                .user(user)
                .product(product)
                .monitorDiscount(true)
                .monitorAvailability(true)
                .monitorPriceChanges(true)
                .build();

        when(userProductRepository.findByIdAndUser(666L, user))
                .thenReturn(Optional.of(userProductFromDb));

        testedService.delete(user, 666L);

        verify(userProductRepository).delete(userProductFromDb);
    }

    @Test
    void delete_nonexistentUserProduct_ServerException() {
        User user = User.builder().login("login").build();

        when(userProductRepository.findByIdAndUser(666L, user))
                .thenReturn(Optional.empty());

        assertThrows(ServerException.class,
                () -> testedService.delete(user, 666L));
        verify(userProductRepository, times(0)).delete(any());
    }
}
