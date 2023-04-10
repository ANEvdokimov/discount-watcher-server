package an.evdokimov.discount.watcher.server.database.city.repository;

import an.evdokimov.discount.watcher.server.database.city.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

}
