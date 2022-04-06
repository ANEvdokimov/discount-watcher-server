package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.id in (SELECT up.id FROM UserProduct up WHERE up.user = :user)")
    Collection<Product> findAllUsersProducts(@Param("user") User user);

    @Query("""
            SELECT p FROM Product p
                LEFT JOIN FETCH p.prices pp
            WHERE
                p.id in (SELECT up.id FROM UserProduct up WHERE up.user = :user) AND
                pp.date = (SELECT MAX(pp2.date) FROM ProductPrice pp2 WHERE pp2.product = p)
            """)
    Collection<Product> findAllUsersProductsWithLastPrice(@Param("user") User user);

    @Query("""
            SELECT DISTINCT up.product FROM UserProduct up
                WHERE (
                    up.monitor_availability = true OR
                    up.monitor_discount = true OR
                    up.monitor_price_changes = true
                )
            """)
    Collection<Product> findAllActiveProducts();

    @Query("""
            SELECT p FROM Product p
                LEFT JOIN FETCH p.prices pp
            WHERE p.id = :id AND pp.date = (SELECT MAX(pp2.date) FROM ProductPrice pp2 WHERE pp2.product = p)
            """)
    Optional<Product> findByIdWithLastPrice(@Param("id") Long id);
}
