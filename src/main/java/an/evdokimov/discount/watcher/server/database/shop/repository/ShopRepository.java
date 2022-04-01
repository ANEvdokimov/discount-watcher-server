package an.evdokimov.discount.watcher.server.database.shop.repository;

import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
}
