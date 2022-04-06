package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
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

        product1 = productRepository.save(Product.builder().productInformation(productInformation1)
                .prices(List.of(price1, price2, price3)).build());
        product2 = productRepository.save(Product.builder().productInformation(productInformation2)
                .prices(List.of(price4)).build());
        product3 = productRepository.save(Product.builder().productInformation(productInformation3)
                .prices(new ArrayList<>()).build());
        product4 = productRepository.save(Product.builder().productInformation(productInformation4)
                .prices(new ArrayList<>()).build());
        productRepository.flush();

        price1.setProduct(product1);
        price2.setProduct(product1);
        price3.setProduct(product1);
        price4.setProduct(product2);
        productPriceRepository.save(price1);
        productPriceRepository.save(price2);
        productPriceRepository.save(price3);
        productPriceRepository.save(price4);
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
        userProductRepository.flush();
    }

    private User user1;
    private User user2;
    private User user3;
    ProductPrice price1;
    ProductPrice price2;
    ProductPrice price3;
    ProductPrice price4;
    private Product product1;
    private Product product2;
    private Product product3;
    private Product product4;

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
                productRepository.findAllActiveProducts().stream().map(Product::getId).collect(Collectors.toList()),
                containsInAnyOrder(product1.getId(), product2.getId(), product3.getId())
        );
    }

    @Test
    void findByIdWithLastPrice_productWith3Prices_productWithLastPrice() {
//        assertAll(
//                () -> assertThat(
//                        productRepository.findByIdWithLastPrice(product1.getId()).get().getPrices(),
//                        contains(price3)
//                ),
//                () -> assertThat(
//                        productRepository.findById(product1.getId()).get().getPrices(),
//                        contains(price3, price2, price1)
//                )
//        );
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
}