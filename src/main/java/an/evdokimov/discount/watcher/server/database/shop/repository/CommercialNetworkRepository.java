package an.evdokimov.discount.watcher.server.database.shop.repository;

import an.evdokimov.discount.watcher.server.database.shop.model.CommercialNetwork;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommercialNetworkRepository extends CrudRepository<CommercialNetwork, Long> {
    @Query("SELECT cn FROM CommercialNetwork cn " +
            "LEFT JOIN FETCH Shop s ON cn.id = s.commercialNetwork.id " +
            "WHERE s.city.id = ?1")
    Iterable<CommercialNetwork> findByCityId(Long id);
}
