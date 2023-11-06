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
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShopRepositoryTest {
    @Autowired
    private ShopRepository testedShopRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProductRepository userProductRepository;
    @Autowired
    private ProductInformationRepository informationRepository;
    @Autowired
    private ProductPriceRepository priceRepository;
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

        shop1 = testedShopRepository.save(Shop.builder().name("shop1").address("adr").shopChain(shopChain)
                .city(city17).build());
        shop2 = testedShopRepository.save(Shop.builder().name("shop2").address("adr").shopChain(shopChain)
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
        productRepository.deleteAll();
        informationRepository.deleteAll();
        testedShopRepository.deleteAll();
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
    void findAllUserShops_user1_shop1() {
        assertThat(
                testedShopRepository.findAllUserShops(user1),
                contains(shop1)
        );
    }

    @Test
    void findAllUserShops_user2_listOfShops() {
        assertThat(
                testedShopRepository.findAllUserShops(user2),
                containsInAnyOrder(shop1, shop2)
        );
    }

    @Test
    void findAllUserShops_user3_emptyList() {
        assertTrue(testedShopRepository.findAllUserShops(user3).isEmpty());
    }

    @Test
    void findAllUserShops_user4_listOfShops() {
        assertFalse(testedShopRepository.findAllUserShops(user4).isEmpty());
    }
}
