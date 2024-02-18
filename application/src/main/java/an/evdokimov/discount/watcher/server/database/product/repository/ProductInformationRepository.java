package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.net.URL;
import java.util.Optional;

@Repository
public interface ProductInformationRepository extends JpaRepository<ProductInformation, Long> {
    Optional<ProductInformation> findByUrl(URL url);
}
