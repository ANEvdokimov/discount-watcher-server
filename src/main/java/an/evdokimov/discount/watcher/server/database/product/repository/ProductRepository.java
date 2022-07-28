package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT up.product FROM UserProduct up WHERE up.user = :user")
    Collection<Product> findAllUsersProducts(@Param("user") User user);

    @Query("""
            SELECT DISTINCT up.product FROM UserProduct up
                WHERE (
                    up.user = :user AND (
                        up.monitorAvailability = true OR
                        up.monitorDiscount = true OR
                        up.monitorPriceChanges = true
                    )
                )
            """)
    Collection<Product> findAllActiveUsersProducts(@Param("user") User user);

    @Query("""
            SELECT p FROM Product p
                LEFT JOIN FETCH p.prices pp
            WHERE
                p.id in (SELECT up.product.id FROM UserProduct up WHERE up.user = :user) AND
                pp.date = (SELECT MAX(pp2.date) FROM ProductPrice pp2 WHERE pp2.product = p)
            """)
    Collection<Product> findAllUsersProductsWithLastPrice(@Param("user") User user);

    @Query("""
            SELECT p FROM Product p
                LEFT JOIN FETCH p.prices pp
            WHERE
                pp.date = (SELECT MAX(pp2.date) FROM ProductPrice pp2 WHERE pp2.product = p) AND
                p.id in (
                    SELECT DISTINCT up.product FROM UserProduct up
                        WHERE (
                            up.user = :user AND (
                                up.monitorAvailability = true OR
                                up.monitorDiscount = true OR
                                up.monitorPriceChanges = true
                            )
                        )
                    )
            """)
    Collection<Product> findAllActiveUsersProductsWithLastPrice(@Param("user") User user);

    @Query("""
            SELECT up.product FROM UserProduct up
                WHERE
                    up.user = :user AND
                    up.product.shop = :shop
            """)
    Collection<Product> findAllUsersProductsInShop(@Param("user") User user, @Param("shop") Shop shop);

    @Query("""
            SELECT p FROM Product p
                LEFT JOIN FETCH p.prices pp
            WHERE
                pp.date = (SELECT MAX(pp2.date) FROM ProductPrice pp2 WHERE pp2.product = p) AND
                p.shop = :shop AND
                p.id in (
                    SELECT DISTINCT up.product FROM UserProduct up
                        WHERE up.user = :user
                )
            """)
    Collection<Product> findAllUsersProductsWithLastPriceInShop(@Param("user") User user, @Param("shop") Shop shop);

    @Query("""
            SELECT DISTINCT up.product FROM UserProduct up
                WHERE (
                    up.user = :user AND
                    up.product.shop = :shop AND (
                        up.monitorAvailability = true OR
                        up.monitorDiscount = true OR
                        up.monitorPriceChanges = true
                    )
                )
            """)
    Collection<Product> findAllActiveUsersProductsInShop(@Param("user") User user, @Param("shop") Shop shop);

    @Query("""
            SELECT p FROM Product p
                LEFT JOIN FETCH p.prices pp
            WHERE
                pp.date = (SELECT MAX(pp2.date) FROM ProductPrice pp2 WHERE pp2.product = p) AND
                p.shop = :shop AND
                p.id in (
                    SELECT DISTINCT up.product FROM UserProduct up
                        WHERE (
                            up.user = :user AND (
                                up.monitorAvailability = true OR
                                up.monitorDiscount = true OR
                                up.monitorPriceChanges = true
                            )
                        )
                )
            """)
    Collection<Product> findAllActiveUserProductsWithLastPriceInShop(@Param("user") User user,
                                                                     @Param("shop") Shop shop);

    @Query("""
            SELECT DISTINCT up.product FROM UserProduct up
                WHERE (
                    up.monitorAvailability = true OR
                    up.monitorDiscount = true OR
                    up.monitorPriceChanges = true
                )
            """)
    Collection<Product> findAllActiveProducts();

    @Query("""
            SELECT p FROM Product p
                LEFT JOIN FETCH p.prices pp
            WHERE p.id = :id AND pp.date = (SELECT MAX(pp2.date) FROM ProductPrice pp2 WHERE pp2.product = p)
            """)
    Optional<Product> findByIdWithLastPrice(@Param("id") Long id);

    Optional<Product> findByProductInformationAndShop(ProductInformation productInformation, Shop shop);

    default void saveIfAbsent(Product product) {
        Optional<Product> productFromDb =
                findByProductInformationAndShop(product.getProductInformation(), product.getShop());
        if (productFromDb.isEmpty()) {
            save(product);
        } else {
            product.setId(productFromDb.get().getId());
        }
    }
}
