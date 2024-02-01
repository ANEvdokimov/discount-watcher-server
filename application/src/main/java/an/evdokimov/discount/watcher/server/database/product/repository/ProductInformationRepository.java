package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.ParsingStatus;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.net.URL;
import java.util.Optional;

@Repository
public interface ProductInformationRepository extends JpaRepository<ProductInformation, Long> {
    Optional<ProductInformation> findByUrl(URL url);

    default ProductInformation findOrCreateByUrl(URL url) {
        Optional<ProductInformation> informationFromDb = findByUrl(url);
        if (informationFromDb.isEmpty()) {
            ProductInformation information =
                    ProductInformation.builder().url(url).parsingStatus(ParsingStatus.PROCESSING).build();
            save(information);
            return information;
        } else {
            return informationFromDb.get();
        }
    }

    @Modifying
    @Query("update ProductInformation pi set pi.name = :name, pi.parsingStatus = 'COMPLETE' where pi.id = :id")
    int updateNameById(@Param("id") Long id, @Param("name") String name);
}
