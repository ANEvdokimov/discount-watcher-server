package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.database.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;


@DataJpaTest
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
        userProductRepository.deleteAll();
        userProductRepository.flush();
        productRepository.deleteAll();
        productRepository.flush();
        productInformationRepository.deleteAll();
        productInformationRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();

        user1 = userRepository.save(User.builder().name("user1").build());
        user2 = userRepository.save(User.builder().name("user2").build());
        user3 = userRepository.save(User.builder().name("user3").build());
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

        userProductRepository.save(UserProduct.builder().user(user1).monitor_availability(true).monitor_discount(false)
                .monitor_price_changes(false).product(product1).build());
        userProductRepository.save(UserProduct.builder().user(user1).monitor_availability(false).monitor_discount(false)
                .monitor_price_changes(false).product(product2).build());

        userProductRepository.save(UserProduct.builder().user(user2).monitor_availability(true).monitor_discount(true)
                .monitor_price_changes(true).product(product2).build());
        userProductRepository.save(UserProduct.builder().user(user2).monitor_availability(false).monitor_discount(true)
                .monitor_price_changes(false).product(product3).build());
        userProductRepository.save(UserProduct.builder().user(user2).monitor_availability(false).monitor_discount(false)
                .monitor_price_changes(false).product(product4).build());
        userProductRepository.save(UserProduct.builder().user(user2).monitor_availability(false).monitor_discount(false)
                .monitor_price_changes(true).product(product5).build());
        userProductRepository.flush();
    }

    private User user1;
    private User user2;
    private User user3;
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
    void findAllByUser_getProductByUser_productList() {
        assertThat(
                productRepository.findAllUsersProducts(user1).stream().map(Product::getId).collect(Collectors.toList()),
                containsInAnyOrder(product1.getId(), product2.getId())
        );
    }

    @Test
    void findAllActiveProducts_Products_listOfActiveProducts() {
        assertThat(
                productRepository.findAllActiveProducts(),
                containsInAnyOrder(product1, product2, product3, product5)
        );
    }

    @Test
    void findByIdWithLastPrice_productWith3Prices_productWithLastPrice() {
        assertThat(
                productRepository.findByIdWithLastPrice(product1.getId()).get().getPrices(),
                contains(price3)
        );
    }

    @Test
    void findAllUsersProductWithLastPrice_products_usersProductsWithLastPrice() {
        Collection<Product> allUsersProducts = productRepository.findAllUsersProductsWithLastPrice(user1);

        assertAll(
                () -> assertThat(
                        allUsersProducts.stream().map(Product::getId).collect(Collectors.toList()),
                        containsInAnyOrder(product1.getId(), product2.getId())
                ),
                () -> assertThat(
                        allUsersProducts.stream().filter(product -> product.getId().equals(product1.getId()))
                                .toList().get(0).getPrices(),
                        contains(price3)
                ),
                () -> assertThat(
                        allUsersProducts.stream().filter(product -> product.getId().equals(product2.getId()))
                                .toList().get(0).getPrices(),
                        contains(price4)
                )
        );
    }

    @Test
    void findAllActiveUsersProductsWithLastPrice_products_activeUsersProductsWithLastPrice() {
        ArrayList<Product> products = new ArrayList<>(productRepository.findAllActiveUsersProductsWithLastPrice(user1));

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price3))
        );
    }

    @Test
    void findAllActiveUsersProducts_products_activeUsersProductsWithLastPrice() {
        ArrayList<Product> products = new ArrayList<>(productRepository.findAllActiveUsersProducts(user1));

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price3, price2, price1))
        );
    }

    @Test
    void findAllActiveUsersProductsInShop_user1AndShop1_listOfProducts() {
        ArrayList<Product> products = new ArrayList<>(productRepository.findAllActiveUsersProductsInShop(user1, shop1));

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price3, price2, price1))
        );
    }

    @Test
    void findAllActiveUsersProductsInShop_user2AndShop1_listOfProducts() {
        ArrayList<Product> products = new ArrayList<>(productRepository.findAllActiveUsersProductsInShop(user2, shop1));

        assertAll(
                () -> assertThat(products, contains(product3, product5)),
                () -> assertThat(
                        products.stream().filter(product -> product.getId() == 3).findAny().get().getPrices(),
                        contains(price3_2, price3_1)
                ),
                () -> assertThat(
                        products.stream().filter(product -> product.getId() == 5).findAny().get().getPrices(),
                        contains(price5_1)
                )
        );
    }

    @Test
    void findAllActiveUserProductsWithLastPriceInShop_user1AndShop1_listOfProducts() {
        ArrayList<Product> products =
                new ArrayList<>(productRepository.findAllActiveUserProductsWithLastPriceInShop(user1, shop1));

        assertAll(
                () -> assertThat(products, contains(product1)),
                () -> assertThat(products.get(0).getPrices(), contains(price3))
        );
    }
}