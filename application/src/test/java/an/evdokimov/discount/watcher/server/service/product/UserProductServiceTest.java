package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.database.product.model.LentaProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.product.repository.UserProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = UserProductServiceImpl.class)
public class UserProductServiceTest {
    @MockBean
    private UserProductRepository userProductRepository;

    @Autowired
    private UserProductServiceImpl testedService;

    @Test
    void getUserProducts_allProducts_collectionOfProducts() {
        LentaProductPrice price1 = LentaProductPrice.builder().id(1L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price2 = LentaProductPrice.builder().id(2L).price(BigDecimal.valueOf(5)).build();

        Product testProduct1 = Product.builder()
                .id(1L)
                .prices(List.of(price2))
                .build();

        LentaProductPrice price3 = LentaProductPrice.builder().id(3L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price4 = LentaProductPrice.builder().id(4L).price(BigDecimal.valueOf(5)).build();

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

        when(userProductRepository.findAllUserProducts(
                eq(userWithProducts), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(List.of(
                userProduct1,
                userProduct2
        ));

        Collection<UserProduct> returnedProducts =
                testedService.getAll(userWithProducts, false, true, true, true);
        assertThat(
                returnedProducts,
                containsInAnyOrder(userProduct1, userProduct2)
        );
    }

    @Test
    void getUserProducts_activeProducts_collectionOfProducts() {
        LentaProductPrice price1 = LentaProductPrice.builder().id(1L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price2 = LentaProductPrice.builder().id(2L).price(BigDecimal.valueOf(5)).build();

        Product testProduct1 = Product.builder()
                .id(1L)
                .prices(List.of(price2))
                .build();

        LentaProductPrice price3 = LentaProductPrice.builder().id(3L).price(BigDecimal.valueOf(10)).build();
        LentaProductPrice price4 = LentaProductPrice.builder().id(4L).price(BigDecimal.valueOf(5)).build();

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

        when(userProductRepository.findActiveUserProducts(userWithProducts, true, true, true))
                .thenReturn(List.of(
                        userProduct1,
                        userProduct2
                ));

        Collection<UserProduct> returnedProducts =
                testedService.getAll(
                        userWithProducts,
                        true,
                        true,
                        true,
                        true
                );

        assertThat(
                returnedProducts,
                containsInAnyOrder(userProduct1, userProduct2)
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

        Shop shop = Shop.builder().id(1L).name("shop").build();

        when(userProductRepository.findAllUserProductsInShop(
                any(), any(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(List.of(userProduct1));
        when(userProductRepository.findActiveUserProductsInShop(any(), any(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of(userProduct3));

        Collection<UserProduct> returnedProducts = testedService.getAll(
                new User(),
                false,
                false,
                false,
                false,
                shop
        );

        assertThat(returnedProducts, contains(userProduct1));
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

        Shop shop = Shop.builder().id(1L).name("shop").build();

        when(userProductRepository.findActiveUserProductsInShop(
                any(), any(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(List.of(userProduct1));
        when(userProductRepository.findActiveUserProductsInShop(any(), any(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of(userProduct3));

        Collection<UserProduct> returnedProducts =
                testedService.getAll(
                        new User(),
                        true,
                        true,
                        true,
                        true,
                        shop
                );

        assertThat(returnedProducts, contains(userProduct3));
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

        UserProduct updatedUserProduct = UserProduct.builder()
                .id(666L)
                .product(product)
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

        UserProduct updatedUserProduct = UserProduct.builder()
                .id(666L)
                .product(product)
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

    @Test
    @DisplayName("update user products by user and products")
    void addOrUpdate_existentUserProducts_success() {
        User user = User.builder().login("login").build();
        Product product = Product.builder().id(1L).build();


        UserProduct userProductFromDb = UserProduct.builder()
                .id(666L)
                .user(user)
                .product(product)
                .monitorDiscount(false)
                .monitorAvailability(false)
                .monitorPriceChanges(false)
                .build();
        UserProduct testedProduct = UserProduct.builder()
                .id(666L)
                .user(user)
                .product(product)
                .monitorDiscount(true)
                .monitorAvailability(true)
                .monitorPriceChanges(true)
                .build();

        when(userProductRepository.findByUserAndProduct(user, product)).thenReturn(Optional.ofNullable(testedProduct));

        testedService.saveOrUpdate(testedProduct);

        verify(userProductRepository).save(testedProduct);
        verify(userProductRepository, never()).save(userProductFromDb);
    }

    @Test
    @DisplayName("create user products by user and products")
    void addOrUpdate_nonexistentUserProducts_success() {
        User user = User.builder().login("login").build();
        Product product = Product.builder().id(1L).build();


        UserProduct testedProduct = UserProduct.builder()
                .id(666L)
                .user(user)
                .product(product)
                .monitorDiscount(true)
                .monitorAvailability(true)
                .monitorPriceChanges(true)
                .build();

        when(userProductRepository.findByUserAndProduct(user, product)).thenReturn(Optional.ofNullable(testedProduct));

        testedService.saveOrUpdate(testedProduct);

        verify(userProductRepository).save(testedProduct);
        verify(userProductRepository).save(any());
    }
}
