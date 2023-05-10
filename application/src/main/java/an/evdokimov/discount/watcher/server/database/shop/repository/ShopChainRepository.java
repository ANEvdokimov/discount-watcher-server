package an.evdokimov.discount.watcher.server.database.shop.repository;

import an.evdokimov.discount.watcher.server.database.shop.model.ShopChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopChainRepository extends JpaRepository<ShopChain, Long> {
    @Query("SELECT sch FROM ShopChain sch " +
            "LEFT JOIN FETCH Shop s ON sch.id = s.shopChain.id " +
            "WHERE s.city.id = ?1")
    List<ShopChain> findByCityId(Long id);
}
