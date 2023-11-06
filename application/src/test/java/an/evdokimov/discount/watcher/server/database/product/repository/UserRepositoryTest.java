package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.city.model.City;
import an.evdokimov.discount.watcher.server.database.city.repository.CityRepository;
import an.evdokimov.discount.watcher.server.database.product.model.ParsingStatus;
import an.evdokimov.discount.watcher.server.database.product.model.PriceChange;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.model.ShopChain;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopChainRepository;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import an.evdokimov.discount.watcher.server.security.user.model.UserRole;
import an.evdokimov.discount.watcher.server.security.user.service.UserRepository;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRepositoryTest {
    @Autowired
    private UserProductRepository testedRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
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
        user1 = userRepository.save(User.builder().login("login1").role(UserRole.ROLE_USER).enabled(true).build());
        user2 = userRepository.save(User.builder().login("login2").role(UserRole.ROLE_USER).enabled(true).build());
        user3 = userRepository.save(User.builder().login("login3").role(UserRole.ROLE_USER).enabled(true).build());
        user4 = userRepository.save(User.builder().login("login4").role(UserRole.ROLE_USER).enabled(true).build());
        user5 = userRepository.save(User.builder().login("login5").role(UserRole.ROLE_USER).enabled(true).build());
        user6 = userRepository.save(User.builder().login("login6").role(UserRole.ROLE_USER).enabled(true).build());

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
        ProductInformation productInformation8 = informationRepository.save(ProductInformation.builder()
                .name("product8").parsingStatus(ParsingStatus.COMPLETE).url(new URL("http://url.test")).build());

        product1 = productRepository.save(Product.builder().productInformation(productInformation1).shop(shop1)
                .build());
        product2 = productRepository.save(Product.builder().productInformation(productInformation2).shop(shop2)
                .build());
        product3 = productRepository.save(Product.builder().productInformation(productInformation3).shop(shop1)
                .build());
        product4 = productRepository.save(Product.builder().productInformation(productInformation4).shop(shop2)
                .build());
        product5 = productRepository.save(Product.builder().productInformation(productInformation5).shop(shop1)
                .build());
        product6 = productRepository.save(Product.builder().productInformation(productInformation6).shop(shop2)
                .build());
        product7 = productRepository.save(Product.builder().productInformation(productInformation7).shop(shop1)
                .build());
        product8 = productRepository.save(Product.builder().productInformation(productInformation8).shop(shop1)
                .build());

        price1_1 = priceRepository.save(ProductPrice.builder()
                .product(product1)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.EQUAL)
                .price(new BigDecimal("100.00"))
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 1, 0, 0))
                .build());
        price1_2 = priceRepository.save(ProductPrice.builder()
                .product(product1)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.EQUAL)
                .price(new BigDecimal("200.00"))
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 2, 0, 0))
                .build());
        price1_3 = priceRepository.save(ProductPrice.builder()
                .product(product1)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.EQUAL)
                .price(new BigDecimal("300.00"))
                .isInStock(true)
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 2, 12, 0))
                .build());

        price2_1 = priceRepository.save(ProductPrice.builder()
                .product(product2)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.DOWN)
                .price(new BigDecimal("222.00"))
                .discount(50.0)
                .isInStock(true)
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 6, 0, 0))
                .build());

        price3_1 = priceRepository.save(ProductPrice.builder()
                .product(product3)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.DOWN)
                .isInStock(false)
                .price(new BigDecimal("10.00"))
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 12, 0, 0))
                .build());
        price3_2 = priceRepository.save(ProductPrice.builder()
                .product(product3)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.DOWN)
                .price(new BigDecimal("15.00"))
                .discount(20.0)
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 13, 0, 0))
                .build());

        price5_1 = priceRepository.save(ProductPrice.builder()
                .product(product5)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.UP)
                .price(new BigDecimal("100.00"))
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 13, 0, 0))
                .build());

        price6_1 = priceRepository.save(ProductPrice.builder()
                .product(product6)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.EQUAL)
                .price(new BigDecimal("100.00"))
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 13, 0, 0))
                .isInStock(true)
                .build());

        price7_1 = priceRepository.save(ProductPrice.builder()
                .product(product7)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.EQUAL)
                .price(new BigDecimal("100.00"))
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 13, 0, 0))
                .discount(100.0)
                .isInStock(false)
                .build());

        price8_1 = priceRepository.save(ProductPrice.builder()
                .product(product8)
                .parsingStatus(ParsingStatus.COMPLETE)
                .priceChange(PriceChange.DOWN)
                .price(new BigDecimal("15.00"))
                .discount(20.0)
                .creationDate(LocalDateTime.of(2000, 4, 1, 0, 0))
                .parsingDate(LocalDateTime.of(2022, 4, 13, 0, 0))
                .build());

        product1.setPrices(List.of(price1_1, price1_2, price1_3));
        product1.setLastPrice(price1_3);
        product2.setPrices(List.of(price2_1));
        product2.setLastPrice(price2_1);
        product3.setPrices(List.of(price3_1, price3_2));
        product3.setLastPrice(price3_2);
        product5.setPrices(List.of(price5_1));
        product5.setLastPrice(price5_1);
        product6.setPrices(List.of(price6_1));
        product6.setLastPrice(price6_1);
        product7.setPrices(List.of(price7_1));
        product7.setLastPrice(price7_1);
        product8.setPrices(List.of(price8_1));
        product8.setLastPrice(price8_1);

        user1_product1 = testedRepository.save(UserProduct.builder().user(user1).monitorAvailability(true)
                .monitorDiscount(false).monitorPriceChanges(false).product(product1).build());
        user1_product2 = testedRepository.save(UserProduct.builder().user(user1).monitorAvailability(false)
                .monitorDiscount(false).monitorPriceChanges(false).product(product2).build());

        user2_product2 = testedRepository.save(UserProduct.builder().user(user2).monitorAvailability(true)
                .monitorDiscount(true).monitorPriceChanges(true).product(product2).build());
        user2_product3 = testedRepository.save(UserProduct.builder().user(user2).monitorAvailability(false)
                .monitorDiscount(true).monitorPriceChanges(false).product(product3).build());
        user2_product4 = testedRepository.save(UserProduct.builder().user(user2).monitorAvailability(false)
                .monitorDiscount(false).monitorPriceChanges(false).product(product4).build());
        user2_product5 = testedRepository.save(UserProduct.builder().user(user2).monitorAvailability(false)
                .monitorDiscount(false).monitorPriceChanges(true).product(product5).build());
        user2_product8 = testedRepository.save(UserProduct.builder().user(user2).monitorAvailability(false)
                .monitorDiscount(true).monitorPriceChanges(true).product(product8).build());

        user4_product3 = testedRepository.save(UserProduct.builder().user(user4).monitorAvailability(false)
                .monitorDiscount(false).monitorPriceChanges(true).product(product3).build());
        user4_product5 = testedRepository.save(UserProduct.builder().user(user4).monitorAvailability(false)
                .monitorDiscount(false).monitorPriceChanges(true).product(product5).build());

        user5_product1 = testedRepository.save(UserProduct.builder().user(user5).monitorAvailability(true)
                .monitorDiscount(true).monitorPriceChanges(true).product(product1).build());
        user5_product3 = testedRepository.save(UserProduct.builder().user(user5).monitorAvailability(true)
                .monitorDiscount(true).monitorPriceChanges(true).product(product3).build());

        user6_product6 = testedRepository.save(UserProduct.builder().user(user6).monitorAvailability(false)
                .monitorDiscount(false).monitorPriceChanges(true).product(product6).build());
        user6_product7 = testedRepository.save(UserProduct.builder().user(user6).monitorAvailability(false)
                .monitorDiscount(false).monitorPriceChanges(false).product(product7).build());
    }

    @AfterAll
    public void afterAll() {
        testedRepository.deleteAll();
        priceRepository.deleteAll();
        productRepository.deleteAll();
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
    private ProductPrice price8_1;
    private Shop shop1;
    private Shop shop2;
    private Product product1;
    private Product product2;
    private Product product3;
    private Product product4;
    private Product product5;
    private Product product6;
    private Product product7;
    private Product product8;

    private UserProduct user1_product1;
    private UserProduct user1_product2;
    private UserProduct user2_product2;
    private UserProduct user2_product3;
    private UserProduct user2_product4;
    private UserProduct user2_product5;
    private UserProduct user2_product8;
    private UserProduct user4_product3;
    private UserProduct user4_product5;
    private UserProduct user5_product1;
    private UserProduct user5_product3;
    private UserProduct user6_product6;
    private UserProduct user6_product7;

    @Test
    void findAllByUser_getProductByUser_productList() {
        assertThat(
                testedRepository.findByUser(user1),
                containsInAnyOrder(user1_product1, user1_product2)
        );
    }

    @Test
    void findAllUsersProducts_monitorAvailability_emptyList() {
        ArrayList<UserProduct> products = new ArrayList<>(testedRepository.findAllUserProducts(
                user4, true, null, null));

        assertTrue(products.isEmpty());
    }

    @Test
    void findAllUsersProducts_monitorAvailability_products() {
        ArrayList<UserProduct> products =
                new ArrayList<>(testedRepository.findAllUserProducts(user1, true, null, null));

        assertAll(
                () -> assertThat(products, contains(user1_product1)),
                () -> assertThat(products.get(0).getProduct().getPrices(), contains(price1_3, price1_2, price1_1))
        );
    }

    @Test
    void findAllUsersProducts_monitorDiscount_emptyList() {
        ArrayList<UserProduct> products =
                new ArrayList<>(testedRepository.findAllUserProducts(user1, null, true, null));

        assertTrue(products.isEmpty());
    }

    @Test
    void findAllUsersProducts_monitorDiscount_products() {
        ArrayList<UserProduct> products =
                new ArrayList<>(testedRepository.findAllUserProducts(user2, null, true, null));

        assertThat(products, containsInAnyOrder(user2_product2, user2_product3, user2_product8));
    }

    @Test
    void findAllUsersProducts_onlyDiscount_products() {
        ArrayList<UserProduct> products =
                new ArrayList<>(testedRepository.findAllUserProducts(user2, false, true, false));

        assertAll(
                () -> assertThat(products, contains(user2_product3)),
                () -> assertThat(products.get(0).getProduct().getPrices(), contains(price3_2, price3_1))
        );
    }

    @Test
    void findAllUsersProducts_monitorPriceChanges_emptyList() {
        ArrayList<UserProduct> products =
                new ArrayList<>(testedRepository.findAllUserProducts(user1, null, null, true));

        assertTrue(products.isEmpty());
    }

    @Test
    void findAllUsersProducts_monitorPriceChanges_products() {
        ArrayList<UserProduct> products =
                new ArrayList<>(testedRepository.findAllUserProducts(user2, null, null, true));

        assertThat(products, containsInAnyOrder(user2_product2, user2_product5, user2_product8));
    }

    @Test
    void findAllUsersProducts_monitorAvailabilityAndDiscountAndPriceChanges_products() {
        ArrayList<UserProduct> products =
                new ArrayList<>(testedRepository.findAllUserProducts(user2, true, true, true));

        assertAll(
                () -> assertThat(products, contains(user2_product2)),
                () -> assertThat(products.get(0).getProduct().getPrices(), contains(price2_1))
        );
    }

    @Test
    void findAllUsersProductsInShop_user1AndShop1_AvailableProducts() {
        ArrayList<UserProduct> products = new ArrayList<>(testedRepository
                .findAllUserProductsInShop(user1, shop1, true, null, null));

        assertAll(
                () -> assertThat(products, contains(user1_product1)),
                () -> assertThat(products.get(0).getProduct().getPrices(), contains(price1_3, price1_2, price1_1))
        );
    }

    @Test
    void findAllUsersProductsInShop_user1AndShop1_emptyList() {
        ArrayList<UserProduct> products = new ArrayList<>(testedRepository
                .findAllUserProductsInShop(user1, shop1, null, true, null));

        assertTrue(products.isEmpty());
    }

    @Test
    void findAllUsersProductsInShop_user4AndShop1_priceChangedProducts() {
        ArrayList<UserProduct> products = new ArrayList<>(testedRepository
                .findAllUserProductsInShop(user4, shop1, null, null, true));

        assertAll(
                () -> assertThat(products, contains(user4_product3, user4_product5)),
                () -> assertThat(
                        products.stream()
                                .filter(product -> product.getId().equals(user4_product3.getId()))
                                .findAny().get().getProduct().getPrices(),
                        contains(price3_2, price3_1)
                ),
                () -> assertThat(
                        products.stream()
                                .filter(product -> product.getId().equals(user4_product5.getId()))
                                .findAny().get().getProduct().getPrices(),
                        contains(price5_1)
                )
        );
    }

    @Test
    void findAllUsersProductsInShop_user1AndShop1_allProducts() {
        Collection<UserProduct> allUsersProductsInShop = testedRepository.findAllUserProductsInShop(user1, shop1);

        assertAll(
                () -> assertThat(allUsersProductsInShop, contains(user1_product1)),
                () -> assertThat(
                        allUsersProductsInShop.stream().findAny().get().getProduct().getPrices(),
                        contains(price1_3, price1_2, price1_1)
                )
        );
    }

    @Test
    void findActiveUsersProducts_availability_availableProducts() {
        ArrayList<UserProduct> products = new ArrayList<>(
                testedRepository.findActiveUserProducts(user1, true, null, null)
        );

        assertAll(
                () -> assertThat(products, contains(user1_product1)),
                () -> assertThat(products.get(0).getProduct().getPrices(), contains(price1_3, price1_2, price1_1))
        );
    }

    @Test
    void findActiveUsersProducts_discount_listOfProducts() {
        ArrayList<UserProduct> products = new ArrayList<>(
                testedRepository.findActiveUserProducts(user2, null, true, null)
        );

        assertThat(products, contains(user2_product2, user2_product3, user2_product8));
    }

    @Test
    void findActiveUsersProducts_availabilityAndDiscount_listOfProducts() {
        ArrayList<UserProduct> products = new ArrayList<>(
                testedRepository.findActiveUserProducts(user2, true, true, null)
        );

        assertAll(
                () -> assertThat(products, contains(user2_product2)),
                () -> assertThat(products.get(0).getProduct().getPrices(), contains(price2_1))
        );
    }

    @Test
    void findActiveUsersProducts_priceDecreased_listOfProducts() {
        ArrayList<UserProduct> products = new ArrayList<>(
                testedRepository.findActiveUserProducts(user2, null, null, true)
        );

        assertThat(products, containsInAnyOrder(user2_product2, user2_product8));
    }

    @Test
    void findActiveUsersProducts_priceDecreasedAndAvailability_listOfProducts() {
        ArrayList<UserProduct> products = new ArrayList<>(
                testedRepository.findActiveUserProducts(user2, true, null, true)
        );

        assertThat(products, contains(user2_product2));
    }

    @Test
    void findActiveUsersProductsInShop_availability_listOfProducts() {
        ArrayList<UserProduct> products = new ArrayList<>(
                testedRepository.findActiveUserProductsInShop(user1, shop1, true, null, null)
        );

        assertAll(
                () -> assertThat(products, contains(user1_product1)),
                () -> assertThat(products.get(0).getProduct().getPrices(), contains(price1_3, price1_2, price1_1))
        );
    }

    @Test
    void findActiveUsersProductsInShop_discount_listOfProducts() {
        ArrayList<UserProduct> products = new ArrayList<>(
                testedRepository.findActiveUserProductsInShop(user2, shop2, null, true, null)
        );

        assertAll(
                () -> assertThat(products, contains(user2_product2)),
                () -> assertThat(products.get(0).getProduct().getPrices(), contains(price2_1))
        );
    }

    @Test
    void findActiveUsersProductsInShop_availabilityAndDiscount_listOfProducts() {
        ArrayList<UserProduct> products = new ArrayList<>(
                testedRepository.findActiveUserProductsInShop(user2, shop2, true, true, null)
        );

        assertAll(
                () -> assertThat(products, contains(user2_product2)),
                () -> assertThat(products.get(0).getProduct().getPrices(), contains(price2_1))
        );
    }
}
