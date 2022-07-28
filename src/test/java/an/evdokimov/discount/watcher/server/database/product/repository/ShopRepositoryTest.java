package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.database.user.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShopRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProductRepository userProductRepository;

    @Autowired
    private ProductInformationRepository productInformationRepository;

    @Autowired
    private ProductPriceRepository productPriceRepository;

    @Autowired
    private ShopRepository shopRepository;

    @BeforeAll
    public void fillDb() {
        user1 = userRepository.save(User.builder().name("user1").build());
        user2 = userRepository.save(User.builder().name("user2").build());
        user3 = userRepository.save(User.builder().name("user3").build());
        user4 = userRepository.save(User.builder().name("user4").build());
        userRepository.flush();

        ProductInformation productInformation1 =
                productInformationRepository.save(ProductInformation.builder().name("product1").build());
        ProductInformation productInformation2 =
                productInformationRepository.save(ProductInformation.builder().name("product2").build());
        ProductInformation productInformation3 =
                productInformationRepository.save(ProductInformation.builder().name("product3").build());
        ProductInformation productInformation4 =
                productInformationRepository.save(ProductInformation.builder().name("product4").build());
        ProductInformation productInformation5 =
                productInformationRepository.save(ProductInformation.builder().name("product5").build());
        productInformationRepository.flush();

        price1 = ProductPrice.builder()
                .price(new BigDecimal("100.00"))
                .date(LocalDateTime.of(2022, 4, 1, 0, 0))
                .build();
        price2 = ProductPrice.builder()
                .price(new BigDecimal("200.00"))
                .date(LocalDateTime.of(2022, 4, 2, 0, 0))
                .build();
        price3 = ProductPrice.builder()
                .price(new BigDecimal("300.00"))
                .date(LocalDateTime.of(2022, 4, 2, 12, 0))
                .build();
        productPriceRepository.saveAllAndFlush(List.of(price1, price2, price3));

        price4 = ProductPrice.builder()
                .price(new BigDecimal("222.00"))
                .date(LocalDateTime.of(2022, 4, 6, 0, 0))
                .build();
        productPriceRepository.saveAndFlush(price4);

        price3_1 = productPriceRepository.save(ProductPrice.builder()
                .price(new BigDecimal("10.00"))
                .date(LocalDateTime.of(2022, 4, 12, 0, 0))
                .build());
        price3_2 = productPriceRepository.save(ProductPrice.builder()
                .price(new BigDecimal("15.00"))
                .date(LocalDateTime.of(2022, 4, 13, 0, 0))
                .build());

        price5_1 = productPriceRepository.save(ProductPrice.builder()
                .price(new BigDecimal("100.00"))
                .date(LocalDateTime.of(2022, 4, 13, 0, 0))
                .build());
        productPriceRepository.flush();

        shop1 = shopRepository.save(Shop.builder().name("shop1").build());
        shop2 = shopRepository.save(Shop.builder().name("shop2").build());
        shopRepository.flush();

        product1 = productRepository.save(Product.builder().productInformation(productInformation1).shop(shop1)
                .prices(List.of(price1, price2, price3)).build());
        product2 = productRepository.save(Product.builder().productInformation(productInformation2).shop(shop2)
                .prices(List.of(price4)).build());
        product3 = productRepository.save(Product.builder().productInformation(productInformation3).shop(shop1)
                .prices(List.of(price3_1, price3_2)).build());
        product4 = productRepository.save(Product.builder().productInformation(productInformation4).shop(shop2)
                .prices(new ArrayList<>()).build());
        product5 = productRepository.save(Product.builder().productInformation(productInformation5).shop(shop1)
                .prices(List.of(price5_1)).build());
        productRepository.flush();

        price1.setProduct(product1);
        price2.setProduct(product1);
        price3.setProduct(product1);
        price4.setProduct(product2);
        price3_1.setProduct(product3);
        price3_2.setProduct(product3);
        price5_1.setProduct(product5);
        productPriceRepository.save(price1);
        productPriceRepository.save(price2);
        productPriceRepository.save(price3);
        productPriceRepository.save(price4);
        productPriceRepository.save(price3_1);
        productPriceRepository.save(price3_2);
        productPriceRepository.save(price5_1);
        productPriceRepository.flush();

        userProductRepository.save(UserProduct.builder().user(user1).monitorAvailability(true).monitorDiscount(false)
                .monitorPriceChanges(false).product(product1).build());
        userProductRepository.save(UserProduct.builder().user(user1).monitorAvailability(false).monitorDiscount(false)
                .monitorPriceChanges(false).product(product3).build());

        userProductRepository.save(UserProduct.builder().user(user2).monitorAvailability(true).monitorDiscount(true)
                .monitorPriceChanges(true).product(product2).build());
        userProductRepository.save(UserProduct.builder().user(user2).monitorAvailability(false).monitorDiscount(true)
                .monitorPriceChanges(false).product(product3).build());
        userProductRepository.save(UserProduct.builder().user(user2).monitorAvailability(false).monitorDiscount(false)
                .monitorPriceChanges(false).product(product4).build());
        userProductRepository.save(UserProduct.builder().user(user2).monitorAvailability(false).monitorDiscount(false)
                .monitorPriceChanges(true).product(product5).build());

        userProductRepository.save(UserProduct.builder().user(user4).monitorAvailability(false).monitorDiscount(false)
                .monitorPriceChanges(false).product(product1).build());
        userProductRepository.save(UserProduct.builder().user(user4).monitorAvailability(false).monitorDiscount(false)
                .monitorPriceChanges(false).product(product2).build());
        userProductRepository.flush();
    }

    @AfterAll
    public void afterAll() {
        userProductRepository.deleteAll();
        productPriceRepository.deleteAll();
        productRepository.deleteAll();
        productInformationRepository.deleteAll();
        shopRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private ProductPrice price1;
    private ProductPrice price2;
    private ProductPrice price3;
    private ProductPrice price4;
    private ProductPrice price3_1;
    private ProductPrice price3_2;
    private ProductPrice price5_1;
    private Shop shop1;
    private Shop shop2;
    private Product product1;
    private Product product2;
    private Product product3;
    private Product product4;
    private Product product5;

    @Test
    void findAllUserShops_user1_shop1() {
        assertThat(
                shopRepository.findAllUserShops(user1),
                contains(shop1)
        );
    }

    @Test
    void findAllUserShops_user2_listOfShops() {
        assertThat(
                shopRepository.findAllUserShops(user2),
                containsInAnyOrder(shop1, shop2)
        );
    }

    @Test
    void findAllUserShops_user3_emptyList() {
        assertTrue(shopRepository.findAllUserShops(user3).isEmpty());
    }

    @Test
    void findAllUserShops_user4_emptyList() {
        assertTrue(shopRepository.findAllUserShops(user4).isEmpty());
    }
}
