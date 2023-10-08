package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.city.model.City;
import an.evdokimov.discount.watcher.server.database.city.repository.CityRepository;
import an.evdokimov.discount.watcher.server.database.product.model.ParsingStatus;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.model.ShopChain;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopChainRepository;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.database.user.model.UserRole;
import an.evdokimov.discount.watcher.server.database.user.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
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
    private ProductRepository testedProductRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProductRepository userProductRepository;
    @Autowired
    private ProductInformationRepository informationRepository;
    @Autowired
    private ProductPriceRepository priceRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private ShopChainRepository shopChainRepository;

    @BeforeAll
    public void fillDb() throws MalformedURLException {
        user1 = userRepository.save(User.builder().name("user1").login("login1").password("pass")
                .role(UserRole.ROLE_USER).enabled(true).registerDate(LocalDateTime.now()).build());
        user2 = userRepository.save(User.builder().name("user2").login("login2").password("pass")
                .role(UserRole.ROLE_USER).enabled(true).registerDate(LocalDateTime.now()).build());
        user3 = userRepository.save(User.builder().name("user3").login("login3").password("pass")
                .role(UserRole.ROLE_USER).enabled(true).registerDate(LocalDateTime.now()).build());
        user4 = userRepository.save(User.builder().name("user4").login("login4").password("pass")
                .role(UserRole.ROLE_USER).enabled(true).registerDate(LocalDateTime.now()).build());
        user5 = userRepository.save(User.builder().name("user5").login("login5").password("pass")
                .role(UserRole.ROLE_USER).enabled(true).registerDate(LocalDateTime.now()).build());
        user6 = userRepository.save(User.builder().name("user6").login("login6").password("pass")
                .role(UserRole.ROLE_USER).enabled(true).registerDate(LocalDateTime.now()).build());

        City city17 = cityRepository.save(City.builder().name("city-17").cyrillicName("city-17").build());

        ShopChain shopChain = shopChainRepository.save(ShopChain.builder().name("shop-chain-1").build());

        shop1 = shopRepository.save(Shop.builder().name("shop1").address("adr").shopChain(shopChain)
                .city(city17).build());
        shop2 = shopRepository.save(Shop.builder().name("shop2").address("adr").shopChain(shopChain)
                .city(city17).build());

        ProductInformation productInformation1 = informationRepository.save(ProductInformation.builder()
                .name("product1").parsingStatus(ParsingStatus.COMPLETE).url(new URL("http://url.test")).build());
        ProductInformation productInformation2 = informationRepository.save(ProductInformation.builder()
                .name("product2").parsingStatus(ParsingStatus.COMPLETE).url(new URL("http://url.test")).build());
        ProductInformation productInformation3 = informationRepository.save(ProductInformation.builder()
                .name("product3").parsingStatus(ParsingStatus.COMPLETE).url(new URL("http://url.test")).build());
        ProductInformation productInformation4 = informationRepository.save(ProductInformation.builder()
                .name("product4").parsingStatus(ParsingStatus.COMPLETE).url(new URL("http://url.test")).build());
        ProductInformation productInformation5 = informationRepository.save(ProductInformation.builder()
                .name("product5").parsingStatus(ParsingStatus.COMPLETE).url(new URL("http://url.test")).build());
        ProductInformation productInformation6 = informationRepository.save(ProductInformation.builder()
                .name("product6").parsingStatus(ParsingStatus.COMPLETE).url(new URL("http://url.test")).build());
        ProductInformation productInformation7 = informationRepository.save(ProductInformation.builder()
                .name("product7").parsingStatus(ParsingStatus.COMPLETE).url(new URL("http://url.test")).build());

        product1 = testedProductRepository.save(Product.builder().productInformation(productInformation1).shop(shop1)
                .build());
        product2 = testedProductRepository.save(Product.builder().productInformation(productInformation2).shop(shop2)
                .build());
        product3 = testedProductRepository.save(Product.builder().productInformation(productInformation3).shop(shop1)
                .build());
        product4 = testedProductRepository.save(Product.builder().productInformation(productInformation4).shop(shop2)
                .build());
        product5 = testedProductRepository.save(Product.builder().productInformation(productInformation5).shop(shop1)
                .build());
        product6 = testedProductRepository.save(Product.builder().productInformation(productInformation6).shop(shop2)
                .build());
        product7 = testedProductRepository.save(Product.builder().productInformation(productInformation7).shop(shop1)
                .build());

        price1_1 = priceRepository.save(ProductPrice.builder()
                .product(product1)
                .parsingStatus(ParsingStatus.COMPLETE)
                .price(new BigDecimal("100.00"))
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 1, 0, 0))
                .build());
        price1_2 = priceRepository.save(ProductPrice.builder()
                .product(product1)
                .parsingStatus(ParsingStatus.COMPLETE)
                .price(new BigDecimal("200.00"))
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 2, 0, 0))
                .build());
        price1_3 = priceRepository.save(ProductPrice.builder()
                .product(product1)
                .parsingStatus(ParsingStatus.COMPLETE)
                .price(new BigDecimal("300.00"))
                .isInStock(true)
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 2, 12, 0))
                .build());

        price2_1 = priceRepository.save(ProductPrice.builder()
                .product(product2)
                .parsingStatus(ParsingStatus.COMPLETE)
                .price(new BigDecimal("222.00"))
                .discount(50.0)
                .isInStock(true)
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 6, 0, 0))
                .build());

        price3_1 = priceRepository.save(ProductPrice.builder()
                .product(product3)
                .parsingStatus(ParsingStatus.COMPLETE)
                .price(new BigDecimal("10.00"))
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 12, 0, 0))
                .build());
        price3_2 = priceRepository.save(ProductPrice.builder()
                .product(product3)
                .parsingStatus(ParsingStatus.COMPLETE)
                .price(new BigDecimal("15.00"))
                .discount(20.0)
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 13, 0, 0))
                .build());

        price5_1 = priceRepository.save(ProductPrice.builder()
                .product(product5)
                .parsingStatus(ParsingStatus.COMPLETE)
                .price(new BigDecimal("100.00"))
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 13, 0, 0))
                .build());

        price6_1 = priceRepository.save(ProductPrice.builder()
                .product(product6)
                .parsingStatus(ParsingStatus.COMPLETE)
                .price(new BigDecimal("100.00"))
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 13, 0, 0))
                .isInStock(true)
                .build());

        price7_1 = priceRepository.save(ProductPrice.builder()
                .product(product7)
                .parsingStatus(ParsingStatus.COMPLETE)
                .price(new BigDecimal("100.00"))
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 13, 0, 0))
                .discount(100.0)
                .isInStock(false)
                .build());

        product1.setPrices(List.of(price1_1, price1_2, price1_3));
        product2.setPrices(List.of(price2_1));
        product3.setPrices(List.of(price3_1, price3_2));
        product5.setPrices(List.of(price5_1));
        product6.setPrices(List.of(price6_1));
        product7.setPrices(List.of(price7_1));

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

        userProductRepository.save(UserProduct.builder().user(user6).monitorAvailability(false).monitorDiscount(false)
                .monitorPriceChanges(true).product(product6).build());
        userProductRepository.save(UserProduct.builder().user(user6).monitorAvailability(false).monitorDiscount(false)
                .monitorPriceChanges(false).product(product7).build());
    }

    @AfterAll
    public void afterAll() {
        userProductRepository.deleteAll();
        priceRepository.deleteAll();
        testedProductRepository.deleteAll();
        informationRepository.deleteAll();
        shopRepository.deleteAll();
        shopChainRepository.deleteAll();
        cityRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;
    private User user6;
    private ProductPrice price1_1;
    private ProductPrice price1_2;
    private ProductPrice price1_3;
    private ProductPrice price2_1;
    private ProductPrice price3_1;
    private ProductPrice price3_2;
    private ProductPrice price5_1;
    private ProductPrice price6_1;
    private ProductPrice price7_1;
    private Shop shop1;
    private Shop shop2;
    private Product product1;
    private Product product2;
    private Product product3;
    private Product product4;
    private Product product5;
    private Product product6;
    private Product product7;

    @Test
    void findAllByUser_getProductByUser_productList() {
        assertThat(
                testedProductRepository.findAllUserProducts(user1),
                containsInAnyOrder(product1, product2)
        );
    }

    @Test
    void findAllTrackedProducts_Products_listOfActiveProducts() {
        assertThat(
                testedProductRepository.findAllTrackedProducts(),
                containsInAnyOrder(product1, product2, product3, product5, product6)
        );
    }

    @Test
    void findByIdWithLastPrice_productWith3Prices_productWithLastPrice() {
        assertThat(
                testedProductRepository.findByIdWithLastPrice(product1.getId()).get().getPrices(),
                contains(price1_3)
        );
    }

    @Test
    void findAllUsersProductWithLastPrice_products_usersProductsWithLastPrice() {
        Collection<Product> allUsersProducts = testedProductRepository.findAllUserProductsWithLastPrice(user1);

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
                new ArrayList<>(testedProductRepository.findAllUserProductsWithLastPrice(user4, true, null, null));

        assertTrue(products.isEmpty());
    }

    @Test
    void findAllUsersProductsWithLastPrice_monitorAvailability_productsWithLastPrice() {
        ArrayList<Product> products =
                new ArrayList<>(testedProductRepository.findAllUserProductsWithLastPrice(user1, true, null, null));

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price1_3))
        );
    }

    @Test
    void findAllUsersProductsWithLastPrice_monitorDiscount_emptyList() {
        ArrayList<Product> products =
                new ArrayList<>(testedProductRepository.findAllUserProductsWithLastPrice(user1, null, true, null));

        assertTrue(products.isEmpty());
    }

    @Test
    void findAllUsersProductsWithLastPrice_monitorDiscount_productsWithLastPrice() {
        ArrayList<Product> products =
                new ArrayList<>(testedProductRepository.findAllUserProductsWithLastPrice(user2, null, true, null));

        assertAll(
                () -> assertThat(products, containsInAnyOrder(product2, product3)),
                () -> assertThat(products.get(0).getPrices(), contains(price3_2))
        );
    }

    @Test
    void findAllUsersProductsWithLastPrice_monitorPriceChanges_emptyList() {
        ArrayList<Product> products =
                new ArrayList<>(testedProductRepository.findAllUserProductsWithLastPrice(user1, null, null, true));

        assertTrue(products.isEmpty());
    }

    @Test
    void findAllUsersProductsWithLastPrice_monitorPriceChanges_productsWithLastPrice() {
        ArrayList<Product> products =
                new ArrayList<>(testedProductRepository.findAllUserProductsWithLastPrice(user2, null, null, true));

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
                new ArrayList<>(testedProductRepository.findAllUserProductsWithLastPrice(user2, true, true, true));

        assertAll(
                () -> assertThat(products, contains(product2)),
                () -> assertThat(products.get(0).getPrices(), contains(price2_1))
        );
    }

    @Test
    void findAllUsersProductsWithLastPrice_onlyDiscount_productsWithLastPrice() {
        ArrayList<Product> products =
                new ArrayList<>(testedProductRepository.findAllUserProductsWithLastPrice(user2, false, true, false));

        assertAll(
                () -> assertThat(products, containsInAnyOrder(product3)),
                () -> assertThat(products.get(0).getPrices(), contains(price3_2))
        );
    }

    @Test
    void findAllUsersProducts_monitorAvailability_emptyList() {
        ArrayList<Product> products =
                new ArrayList<>(testedProductRepository.findAllUserProducts(user4, true, null, null));

        assertTrue(products.isEmpty());
    }

    @Test
    void findAllUsersProducts_monitorAvailability_productsWithPriceHistory() {
        ArrayList<Product> products =
                new ArrayList<>(testedProductRepository.findAllUserProducts(user1, true, null, null));

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price1_3, price1_2, price1_1))
        );
    }

    @Test
    void findAllUsersProducts_monitorDiscount_emptyList() {
        ArrayList<Product> products =
                new ArrayList<>(testedProductRepository.findAllUserProducts(user1, null, true, null));

        assertTrue(products.isEmpty());
    }

    @Test
    void findAllUsersProducts_monitorDiscount_productsWithPriceHistory() {
        ArrayList<Product> products =
                new ArrayList<>(testedProductRepository.findAllUserProducts(user2, null, true, null));

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
                new ArrayList<>(testedProductRepository.findAllUserProducts(user2, false, true, false));

        assertAll(
                () -> assertThat(products, contains(product3)),
                () -> assertThat(products.get(0).getPrices(), contains(price3_2, price3_1))
        );
    }

    @Test
    void findAllUsersProducts_monitorPriceChanges_emptyList() {
        ArrayList<Product> products =
                new ArrayList<>(testedProductRepository.findAllUserProducts(user1, null, null, true));

        assertTrue(products.isEmpty());
    }

    @Test
    void findAllUsersProducts_monitorPriceChanges_productsWithPriceHistory() {
        ArrayList<Product> products =
                new ArrayList<>(testedProductRepository.findAllUserProducts(user2, null, true, null));

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
                new ArrayList<>(testedProductRepository.findAllUserProducts(user2, true, true, true));

        assertAll(
                () -> assertThat(products, contains(product2)),
                () -> assertThat(products.get(0).getPrices(), contains(price2_1))
        );
    }

    @Test
    void findAllUsersProductsInShop_user1AndShop1_listOfProductsWithPriceHistory() {
        ArrayList<Product> products = new ArrayList<>(testedProductRepository
                .findAllUserProductsInShop(user1, shop1, true, null, null));

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price1_3, price1_2, price1_1))
        );
    }

    @Test
    void findAllUsersProductsInShop_user1AndShop1_emptyList() {
        ArrayList<Product> products = new ArrayList<>(testedProductRepository
                .findAllUserProductsInShop(user1, shop1, null, true, null));

        assertTrue(products.isEmpty());
    }

    @Test
    void findAllUsersProductsInShop_user4AndShop1_listOfProductsWithPriceHistory() {
        ArrayList<Product> products = new ArrayList<>(testedProductRepository
                .findAllUserProductsInShop(user4, shop1, null, null, true));

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
        ArrayList<Product> products = new ArrayList<>(testedProductRepository
                .findAllUserProductsWithLastPriceInShop(user1, shop1, true, null, null));

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price1_3))
        );
    }

    @Test
    void findAllUsersProductsInShop_user1AndShop1_listOfProducts() {
        Collection<Product> allUsersProductsInShop = testedProductRepository.findAllUserProductsInShop(user1, shop1);

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
                testedProductRepository.findAllUserProductsWithLastPriceInShop(user1, shop1);

        assertAll(
                () -> assertThat(allUsersProductsInShop, contains(product1)),
                () -> assertThat(allUsersProductsInShop.stream().findAny().get().getPrices(), contains(price1_3))
        );
    }

    @Test
    void findActiveUsersProducts_availability_listOfProductsWithPriceHistory() {
        ArrayList<Product> products = new ArrayList<>(
                testedProductRepository.findActiveUserProducts(user1, true, null, null)
        );

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price1_3, price1_2, price1_1))
        );
    }

    @Test
    void findActiveUsersProducts_discount_listOfProductsWithPriceHistory() {
        ArrayList<Product> products = new ArrayList<>(
                testedProductRepository.findActiveUserProducts(user2, null, true, null)
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
                testedProductRepository.findActiveUserProducts(user2, true, true, null)
        );

        assertAll(
                () -> assertThat(products, contains(product2)),
                () -> assertThat(products.get(0).getPrices(), contains(price2_1))
        );
    }

    @Test
    void findActiveUsersProducts_availability_listOfProductsWithOnlyLastPrice() {
        ArrayList<Product> products = new ArrayList<>(
                testedProductRepository.findActiveUserProductsWithLastPrice(user1, true, null, null)
        );

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price1_3))
        );
    }

    @Test
    void findActiveUsersProducts_discount_listOfProductsWithOnlyLastPrice() {
        ArrayList<Product> products = new ArrayList<>(
                testedProductRepository.findActiveUserProductsWithLastPrice(user2, null, true, null)
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
                testedProductRepository.findActiveUserProductsWithLastPrice(user2, true, true, null)
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
                testedProductRepository.findActiveUserProductsInShop(user1, shop1, true, null, null)
        );

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price1_3, price1_2, price1_1))
        );
    }

    @Test
    void findActiveUsersProductsInShop_discount_listOfProductsWithPriceHistory() {
        ArrayList<Product> products = new ArrayList<>(
                testedProductRepository.findActiveUserProductsInShop(user2, shop2, null, true, null)
        );

        assertAll(
                () -> assertThat(products, contains(product2)),
                () -> assertThat(products.get(0).getPrices(), contains(price2_1))
        );
    }

    @Test
    void findActiveUsersProductsInShop_availabilityAndDiscount_listOfProductsWithPriceHistory() {
        ArrayList<Product> products = new ArrayList<>(
                testedProductRepository.findActiveUserProductsInShop(user2, shop2, true, true, null)
        );

        assertAll(
                () -> assertThat(products, contains(product2)),
                () -> assertThat(products.get(0).getPrices(), contains(price2_1))
        );
    }

    @Test
    void findActiveUsersProductsInShop_availability_listOfProductsWithOnlyLastPrice() {
        ArrayList<Product> products = new ArrayList<>(
                testedProductRepository.findActiveUserProductsWithLastPriceInShop(user1, shop1, true, null, null)
        );

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price1_3))
        );
    }

    @Test
    void findActiveUsersProductsInShop_discount_listOfProductsWithOnlyLastPrice() {
        ArrayList<Product> products = new ArrayList<>(
                testedProductRepository.findActiveUserProductsInShop(user2, shop2, null, true, null)
        );

        assertAll(
                () -> assertThat(products, contains(product2)),
                () -> assertThat(products.get(0).getPrices(), contains(price2_1))
        );
    }

    @Test
    void findActiveUsersProductsInShop_availabilityAndDiscount_listOfProductsWithOnlyLastPrice() {
        ArrayList<Product> products = new ArrayList<>(
                testedProductRepository.findActiveUserProductsWithLastPriceInShop(user2, shop2, true, true, null)
        );

        assertAll(
                () -> assertThat(products, contains(product2)),
                () -> assertThat(products.get(0).getPrices(), contains(price2_1))
        );
    }

    @Test
    void findActiveUsersProductsInShop_availabilityAndDiscount_emptyList() {
        ArrayList<Product> products = new ArrayList<>(
                testedProductRepository.findActiveUserProductsWithLastPriceInShop(user5, shop2, true, true, null)
        );

        assertTrue(products.isEmpty());
    }

    @Test
    void findActiveUserProductsWithLastPrice_allProducts_listOfProducts() {
        ArrayList<Product> products = new ArrayList<>(
                testedProductRepository.findActiveUserProductsWithLastPrice(
                        user6,
                        null,
                        null,
                        null
                )
        );

        assertAll(
                () -> assertThat(products, containsInAnyOrder(product6, product7)),
                () -> assertThat(products.stream()
                                .filter(product -> product.getId().equals(product6.getId()))
                                .findAny()
                                .get().getPrices(),
                        contains(price6_1)),
                () -> assertThat(products.stream()
                                .filter(product -> product.getId().equals(product7.getId()))
                                .findAny()
                                .get().getPrices(),
                        contains(price7_1))
        );
    }
}