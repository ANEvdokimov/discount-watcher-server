package an.evdokimov.discount.watcher.server.database.shop.repository;

import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    @Query("""
            SELECT pr.shop FROM Product pr
            JOIN UserProduct upr ON upr = pr
            WHERE upr.user = :user AND (
                    upr.monitorAvailability = true OR
                    upr.monitorDiscount = true OR
                    upr.monitorPriceChanges = true
            )
            """)
    Collection<Shop> findAllUserShops(@Param("user") User user);
}
