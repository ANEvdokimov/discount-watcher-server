package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.database.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;


@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProductRepository userProductRepository;

    @Autowired
    private ProductInformationRepository productInformationRepository;

    @BeforeEach
    public void fillDb() {
        user1 = userRepository.save(User.builder().name("user1").build());
        user2 = userRepository.save(User.builder().name("user2").build());
        user3 = userRepository.save(User.builder().name("user3").build());

        ProductInformation productInformation1 =
                productInformationRepository.save(ProductInformation.builder().name("product1").build());
        ProductInformation productInformation2 =
                productInformationRepository.save(ProductInformation.builder().name("product2").build());
        ProductInformation productInformation3 =
                productInformationRepository.save(ProductInformation.builder().name("product3").build());
        ProductInformation productInformation4 =
                productInformationRepository.save(ProductInformation.builder().name("product4").build());

        product1 = productRepository.save(Product.builder().productInformation(productInformation1).build());
        product2 = productRepository.save(Product.builder().productInformation(productInformation2).build());
        product3 = productRepository.save(Product.builder().productInformation(productInformation3).build());
        product4 = productRepository.save(Product.builder().productInformation(productInformation4).build());

        userProductRepository.save(UserProduct.builder().user(user1).product(product1).build());
        userProductRepository.save(UserProduct.builder().user(user1).product(product2).build());

        userProductRepository.save(UserProduct.builder().user(user2).product(product3).build());
        userProductRepository.save(UserProduct.builder().user(user2).product(product4).build());
    }

    private User user1;
    private User user2;
    private User user3;
    private Product product1;
    private Product product2;
    private Product product3;
    private Product product4;

    @Test
    void findAllByUser_getProductByUser_productList() {
        assertThat(
                productRepository.findAllUsersProducts(user1),
                containsInAnyOrder(product1, product2)
        );
    }
}