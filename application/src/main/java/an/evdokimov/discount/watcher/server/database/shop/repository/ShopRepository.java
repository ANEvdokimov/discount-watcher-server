package an.evdokimov.discount.watcher.server.database.shop.repository;

import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    @Query("""
            SELECT DISTINCT upr.product.shop FROM UserProduct upr
            WHERE upr.user = :user AND (
                    upr.monitorAvailability = true OR
                    upr.monitorDiscount = true OR
                    upr.monitorPriceChanges = true
            )
            """)
    Collection<Shop> findAllUserShops(@Param("user") User user);

    Optional<Shop> findByCookie(String cookie);
}
