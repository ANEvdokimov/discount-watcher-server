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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductRepositoryTest {
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
        user5 = userRepository.save(User.builder().name("user5").build());
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

        price1_1 = ProductPrice.builder()
                .price(new BigDecimal("100.00"))
                .date(LocalDateTime.of(2022, 4, 1, 0, 0))
                .build();
        price1_2 = ProductPrice.builder()
                .price(new BigDecimal("200.00"))
                .date(LocalDateTime.of(2022, 4, 2, 0, 0))
                .build();
        price1_3 = ProductPrice.builder()
                .price(new BigDecimal("300.00"))
                .isInStock(true)
                .date(LocalDateTime.of(2022, 4, 2, 12, 0))
                .build();
        productPriceRepository.saveAllAndFlush(List.of(price1_1, price1_2, price1_3));

        price2_1 = ProductPrice.builder()
                .price(new BigDecimal("222.00"))
                .discount(50.0)
                .isInStock(true)
                .date(LocalDateTime.of(2022, 4, 6, 0, 0))
                .build();
        productPriceRepository.saveAndFlush(price2_1);

        price3_1 = productPriceRepository.save(ProductPrice.builder()
                .price(new BigDecimal("10.00"))
                .date(LocalDateTime.of(2022, 4, 12, 0, 0))
                .build());
        price3_2 = productPriceRepository.save(ProductPrice.builder()
                .price(new BigDecimal("15.00"))
                .discount(20.0)
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
                .prices(List.of(price1_1, price1_2, price1_3)).build());
        product2 = productRepository.save(Product.builder().productInformation(productInformation2).shop(shop2)
                .prices(List.of(price2_1)).build());
        product3 = productRepository.save(Product.builder().productInformation(productInformation3).shop(shop1)
                .prices(List.of(price3_1, price3_2)).build());
        product4 = productRepository.save(Product.builder().productInformation(productInformation4).shop(shop2)
                .prices(new ArrayList<>()).build());
        product5 = productRepository.save(Product.builder().productInformation(productInformation5).shop(shop1)
                .prices(List.of(price5_1)).build());
        productRepository.flush();

        price1_1.setProduct(product1);
        price1_2.setProduct(product1);
        price1_3.setProduct(product1);
        price2_1.setProduct(product2);
        price3_1.setProduct(product3);
        price3_2.setProduct(product3);
        price5_1.setProduct(product5);
        productPriceRepository.save(price1_1);
        productPriceRepository.save(price1_2);
        productPriceRepository.save(price1_3);
        productPriceRepository.save(price2_1);
        productPriceRepository.save(price3_1);
        productPriceRepository.save(price3_2);
        productPriceRepository.save(price5_1);
        productPriceRepository.flush();

        userProductRepository.save(UserProduct.builder().user(user1).monitorAvailability(true).monitorDiscount(false)
                .monitorPriceChanges(false).product(product1).build());
        userProductRepository.save(UserProduct.builder().user(user1).monitorAvailability(false).monitorDiscount(false)
                .monitorPriceChanges(false).product(product2).build());

        userProductRepository.save(UserProduct.builder().user(user2).monitorAvailability(true).monitorDiscount(true)
                .monitorPriceChanges(true).product(product2).build());
        userProductRepository.save(UserProduct.builder().user(user2).monitorAvailability(false).monitorDiscount(true)
                .monitorPriceChanges(false).product(product3).build());
        userProductRepository.save(UserProduct.builder().user(user2).monitorAvailability(false).monitorDiscount(false)
                .monitorPriceChanges(false).product(product4).build());
        userProductRepository.save(UserProduct.builder().user(user2).monitorAvailability(false).monitorDiscount(false)
                .monitorPriceChanges(true).product(product5).build());

        userProductRepository.save(UserProduct.builder().user(user4).monitorAvailability(false).monitorDiscount(false)
                .monitorPriceChanges(true).product(product3).build());
        userProductRepository.save(UserProduct.builder().user(user4).monitorAvailability(false).monitorDiscount(false)
                .monitorPriceChanges(true).product(product5).build());

        userProductRepository.save(UserProduct.builder().user(user5).monitorAvailability(true).monitorDiscount(true)
                .monitorPriceChanges(true).product(product1).build());
        userProductRepository.save(UserProduct.builder().user(user5).monitorAvailability(true).monitorDiscount(true)
                .monitorPriceChanges(true).product(product3).build());

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
    private User user5;
    private ProductPrice price1_1;
    private ProductPrice price1_2;
    private ProductPrice price1_3;
    private ProductPrice price2_1;
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
    void findAllByUser_getProductByUser_productList() {
        assertThat(
                productRepository.findAllUsersProducts(user1),
                containsInAnyOrder(product1, product2)
        );
    }

    @Test
    void findAllActiveProducts_Products_listOfActiveProducts() {
        assertThat(
                productRepository.findAllTrackedProducts(),
                containsInAnyOrder(product1, product2, product3, product5)
        );
    }

    @Test
    void findByIdWithLastPrice_productWith3Prices_productWithLastPrice() {
        assertThat(
                productRepository.findByIdWithLastPrice(product1.getId()).get().getPrices(),
                contains(price1_3)
        );
    }

    @Test
    void findAllUsersProductWithLastPrice_products_usersProductsWithLastPrice() {
        Collection<Product> allUsersProducts = productRepository.findAllUsersProductsWithLastPrice(user1);

        assertAll(
                () -> assertThat(
                        allUsersProducts,
                        containsInAnyOrder(product1, product2)
                ),
                () -> assertThat(
                        allUsersProducts.stream().filter(product -> product.equals(product1))
                                .toList().get(0).getPrices(),
                        contains(price1_3)
                ),
                () -> assertThat(
                        allUsersProducts.stream().filter(product -> product.equals(product2))
                                .toList().get(0).getPrices(),
                        contains(price2_1)
                )
        );
    }

    @Test
    void findAllUsersProductsWithLastPrice_monitorAvailability_emptyList() {
        ArrayList<Product> products =
                new ArrayList<>(productRepository.findAllUsersProductsWithLastPrice(user4, true, null, null));

        assertTrue(products.isEmpty());
    }

    @Test
    void findAllUsersProductsWithLastPrice_monitorAvailability_productsWithLastPrice() {
        ArrayList<Product> products =
                new ArrayList<>(productRepository.findAllUsersProductsWithLastPrice(user1, true, null, null));

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price1_3))
        );
    }

    @Test
    void findAllUsersProductsWithLastPrice_monitorDiscount_emptyList() {
        ArrayList<Product> products =
                new ArrayList<>(productRepository.findAllUsersProductsWithLastPrice(user1, null, true, null));

        assertTrue(products.isEmpty());
    }

    @Test
    void findAllUsersProductsWithLastPrice_monitorDiscount_productsWithLastPrice() {
        ArrayList<Product> products =
                new ArrayList<>(productRepository.findAllUsersProductsWithLastPrice(user2, null, true, null));

        assertAll(
                () -> assertThat(products, containsInAnyOrder(product2, product3)),
                () -> assertThat(products.get(0).getPrices(), contains(price3_2))
        );
    }

    @Test
    void findAllUsersProductsWithLastPrice_monitorPriceChanges_emptyList() {
        ArrayList<Product> products =
                new ArrayList<>(productRepository.findAllUsersProductsWithLastPrice(user1, null, null, true));

        assertTrue(products.isEmpty());
    }

    @Test
    void findAllUsersProductsWithLastPrice_monitorPriceChanges_productsWithLastPrice() {
        ArrayList<Product> products =
                new ArrayList<>(productRepository.findAllUsersProductsWithLastPrice(user2, null, null, true));

        assertAll(
                () -> assertThat(products, containsInAnyOrder(product2, product5)),
                () -> assertThat(products.stream()
                                .filter(product -> product.getId().equals(product2.getId()))
                                .findAny()
                                .get().getPrices(),
                        contains(price2_1)),
                () -> assertThat(products.stream()
                                .filter(product -> product.getId().equals(product5.getId()))
                                .findAny()
                                .get().getPrices(),
                        contains(price5_1))
        );
    }

    @Test
    void findAllUsersProductsWithLastPrice_monitorAvailabilityAndDiscountAndPriceChanges_productsWithLastPrice() {
        ArrayList<Product> products =
                new ArrayList<>(productRepository.findAllUsersProductsWithLastPrice(user2, true, true, true));

        assertAll(
                () -> assertThat(products, contains(product2)),
                () -> assertThat(products.get(0).getPrices(), contains(price2_1))
        );
    }

    @Test
    void findAllUsersProductsWithLastPrice_onlyDiscount_productsWithLastPrice() {
        ArrayList<Product> products =
                new ArrayList<>(productRepository.findAllUsersProductsWithLastPrice(user2, false, true, false));

        assertAll(
                () -> assertThat(products, containsInAnyOrder(product3)),
                () -> assertThat(products.get(0).getPrices(), contains(price3_2))
        );
    }

    @Test
    void findAllUsersProducts_monitorAvailability_emptyList() {
        ArrayList<Product> products =
                new ArrayList<>(productRepository.findAllUsersProducts(user4, true, null, null));

        assertTrue(products.isEmpty());
    }

    @Test
    void findAllUsersProducts_monitorAvailability_productsWithPriceHistory() {
        ArrayList<Product> products =
                new ArrayList<>(productRepository.findAllUsersProducts(user1, true, null, null));

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price1_3, price1_2, price1_1))
        );
    }

    @Test
    void findAllUsersProducts_monitorDiscount_emptyList() {
        ArrayList<Product> products =
                new ArrayList<>(productRepository.findAllUsersProducts(user1, null, true, null));

        assertTrue(products.isEmpty());
    }

    @Test
    void findAllUsersProducts_monitorDiscount_productsWithPriceHistory() {
        ArrayList<Product> products =
                new ArrayList<>(productRepository.findAllUsersProducts(user2, null, true, null));

        assertAll(
                () -> assertThat(products, containsInAnyOrder(product2, product3)),
                () -> assertThat(
                        products.stream()
                                .filter(product -> product.getId().equals(product2.getId()))
                                .findAny()
                                .get().getPrices(),
                        contains(price2_1)),
                () -> assertThat(products.stream()
                                .filter(product -> product.getId().equals(product3.getId()))
                                .findAny()
                                .get().getPrices(),
                        contains(price3_2, price3_1))
        );
    }

    @Test
    void findAllUsersProducts_onlyDiscount_productsWithPriceHistory() {
        ArrayList<Product> products =
                new ArrayList<>(productRepository.findAllUsersProducts(user2, false, true, false));

        assertAll(
                () -> assertThat(products, contains(product3)),
                () -> assertThat(products.get(0).getPrices(), contains(price3_2, price3_1))
        );
    }

    @Test
    void findAllUsersProducts_monitorPriceChanges_emptyList() {
        ArrayList<Product> products =
                new ArrayList<>(productRepository.findAllUsersProducts(user1, null, null, true));

        assertTrue(products.isEmpty());
    }

    @Test
    void findAllUsersProducts_monitorPriceChanges_productsWithPriceHistory() {
        ArrayList<Product> products =
                new ArrayList<>(productRepository.findAllUsersProducts(user2, null, true, null));

        Map<Long, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, product -> product));

        assertAll(
                () -> assertThat(products, containsInAnyOrder(product2, product3)),
                () -> assertThat(productMap.get(product2.getId()).getPrices(), contains(price2_1)),
                () -> assertThat(productMap.get(product3.getId()).getPrices(), contains(price3_2, price3_1))
        );
    }

    @Test
    void findAllUsersProducts_monitorAvailabilityAndDiscountAndPriceChanges_productsWithPriceHistory() {
        ArrayList<Product> products =
                new ArrayList<>(productRepository.findAllUsersProducts(user2, true, true, true));

        assertAll(
                () -> assertThat(products, contains(product2)),
                () -> assertThat(products.get(0).getPrices(), contains(price2_1))
        );
    }

    @Test
    void findAllUsersProductsInShop_user1AndShop1_listOfProductsWithPriceHistory() {
        ArrayList<Product> products = new ArrayList<>(productRepository
                .findAllUsersProductsInShop(user1, shop1, true, null, null));

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price1_3, price1_2, price1_1))
        );
    }

    @Test
    void findAllUsersProductsInShop_user1AndShop1_emptyList() {
        ArrayList<Product> products = new ArrayList<>(productRepository
                .findAllUsersProductsInShop(user1, shop1, null, true, null));

        assertTrue(products.isEmpty());
    }

    @Test
    void findAllUsersProductsInShop_user4AndShop1_listOfProductsWithPriceHistory() {
        ArrayList<Product> products = new ArrayList<>(productRepository
                .findAllUsersProductsInShop(user4, shop1, null, null, true));

        assertAll(
                () -> assertThat(products, contains(product3, product5)),
                () -> assertThat(
                        products.stream()
                                .filter(product -> product.getId().equals(product3.getId()))
                                .findAny().get().getPrices(),
                        contains(price3_2, price3_1)
                ),
                () -> assertThat(
                        products.stream()
                                .filter(product -> product.getId().equals(product5.getId()))
                                .findAny().get().getPrices(),
                        contains(price5_1)
                )
        );
    }

    @Test
    void findAllUserProductsWithLastPriceInShop_user1AndShop1_listOfProductsWithLastPrice() {
        ArrayList<Product> products = new ArrayList<>(productRepository
                .findAllUserProductsWithLastPriceInShop(user1, shop1, true, null, null));

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price1_3))
        );
    }

    @Test
    void findAllUsersProductsInShop_user1AndShop1_listOfProducts() {
        Collection<Product> allUsersProductsInShop = productRepository.findAllUsersProductsInShop(user1, shop1);

        assertAll(
                () -> assertThat(allUsersProductsInShop, contains(product1)),
                () -> assertThat(
                        allUsersProductsInShop.stream().findAny().get().getPrices(),
                        contains(price1_3, price1_2, price1_1)
                )
        );
    }

    @Test
    void findAllUsersProductsWithLastPriceInShop_user1AndShop1_listOfProducts() {
        Collection<Product> allUsersProductsInShop =
                productRepository.findAllUsersProductsWithLastPriceInShop(user1, shop1);

        assertAll(
                () -> assertThat(allUsersProductsInShop, contains(product1)),
                () -> assertThat(allUsersProductsInShop.stream().findAny().get().getPrices(), contains(price1_3))
        );
    }

    @Test
    void findActiveUsersProducts_availability_listOfProductsWithPriceHistory() {
        ArrayList<Product> products = new ArrayList<>(
                productRepository.findActiveUserProducts(user1, true, null, null)
        );

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price1_3, price1_2, price1_1))
        );
    }

    @Test
    void findActiveUsersProducts_discount_listOfProductsWithPriceHistory() {
        ArrayList<Product> products = new ArrayList<>(
                productRepository.findActiveUserProducts(user2, null, true, null)
        );

        assertAll(
                () -> assertThat(products, contains(product2, product3)),
                () -> assertThat(products.stream()
                                .filter(product -> product.getId().equals(product2.getId()))
                                .findAny()
                                .get().getPrices(),
                        contains(price2_1)
                ),
                () -> assertThat(products.stream()
                                .filter(product -> product.getId().equals(product3.getId()))
                                .findAny()
                                .get().getPrices(),
                        contains(price3_2, price3_1)
                )
        );
    }

    @Test
    void findActiveUsersProducts_availabilityAndDiscount_listOfProductsWithPriceHistory() {
        ArrayList<Product> products = new ArrayList<>(
                productRepository.findActiveUserProducts(user2, true, true, null)
        );

        assertAll(
                () -> assertThat(products, contains(product2)),
                () -> assertThat(products.get(0).getPrices(), contains(price2_1))
        );
    }

    @Test
    void findActiveUsersProducts_availability_listOfProductsWithOnlyLastPrice() {
        ArrayList<Product> products = new ArrayList<>(
                productRepository.findActiveUserProductsWithLastPrice(user1, true, null, null)
        );

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price1_3))
        );
    }

    @Test
    void findActiveUsersProducts_discount_listOfProductsWithOnlyLastPrice() {
        ArrayList<Product> products = new ArrayList<>(
                productRepository.findActiveUserProductsWithLastPrice(user2, null, true, null)
        );

        assertAll(
                () -> assertThat(products, containsInAnyOrder(product2, product3)),
                () -> assertThat(products.stream()
                                .filter(product -> product.getId().equals(product2.getId()))
                                .findAny()
                                .get().getPrices(),
                        contains(price2_1)
                ),
                () -> assertThat(products.stream()
                                .filter(product -> product.getId().equals(product3.getId()))
                                .findAny()
                                .get().getPrices(),
                        contains(price3_2)
                )
        );
    }

    @Test
    void findActiveUsersProducts_availabilityAndDiscount_listOfProductsWithOnlyLastPrice() {
        ArrayList<Product> products = new ArrayList<>(
                productRepository.findActiveUserProductsWithLastPrice(user2, true, true, null)
        );

        assertAll(
                () -> assertThat(products, contains(product2)),
                () -> assertThat(products.get(0).getPrices(), contains(price2_1))
        );
    }

    ////////////////
    @Test
    void findActiveUsersProductsInShop_availability_listOfProductsWithPriceHistory() {
        ArrayList<Product> products = new ArrayList<>(
                productRepository.findActiveUserProductsInShop(user1, shop1, true, null, null)
        );

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price1_3, price1_2, price1_1))
        );
    }

    @Test
    void findActiveUsersProductsInShop_discount_listOfProductsWithPriceHistory() {
        ArrayList<Product> products = new ArrayList<>(
                productRepository.findActiveUserProductsInShop(user2, shop2, null, true, null)
        );

        assertAll(
                () -> assertThat(products, contains(product2)),
                () -> assertThat(products.get(0).getPrices(), contains(price2_1))
        );
    }

    @Test
    void findActiveUsersProductsInShop_availabilityAndDiscount_listOfProductsWithPriceHistory() {
        ArrayList<Product> products = new ArrayList<>(
                productRepository.findActiveUserProductsInShop(user2, shop2, true, true, null)
        );

        assertAll(
                () -> assertThat(products, contains(product2)),
                () -> assertThat(products.get(0).getPrices(), contains(price2_1))
        );
    }

    @Test
    void findActiveUsersProductsInShop_availability_listOfProductsWithOnlyLastPrice() {
        ArrayList<Product> products = new ArrayList<>(
                productRepository.findActiveUserProductsWithLastPriceInShop(user1, shop1, true, null, null)
        );

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price1_3))
        );
    }

    @Test
    void findActiveUsersProductsInShop_discount_listOfProductsWithOnlyLastPrice() {
        ArrayList<Product> products = new ArrayList<>(
                productRepository.findActiveUserProductsInShop(user2, shop2, null, true, null)
        );

        assertAll(
                () -> assertThat(products, contains(product2)),
                () -> assertThat(products.get(0).getPrices(), contains(price2_1))
        );
    }

    @Test
    void findActiveUsersProductsInShop_availabilityAndDiscount_listOfProductsWithOnlyLastPrice() {
        ArrayList<Product> products = new ArrayList<>(
                productRepository.findActiveUserProductsWithLastPriceInShop(user2, shop2, true, true, null)
        );

        assertAll(
                () -> assertThat(products, contains(product2)),
                () -> assertThat(products.get(0).getPrices(), contains(price2_1))
        );
    }

    @Test
    void findActiveUsersProductsInShop_availabilityAndDiscount_emptyList() {
        ArrayList<Product> products = new ArrayList<>(
                productRepository.findActiveUserProductsWithLastPriceInShop(user5, shop2, true, true, null)
        );

        assertTrue(products.isEmpty());
    }
}