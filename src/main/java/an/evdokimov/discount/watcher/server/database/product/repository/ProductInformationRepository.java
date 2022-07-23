package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.net.URL;
import java.util.Optional;

@Repository
public interface ProductInformationRepository extends JpaRepository<ProductInformation, Long> {
    public abstract Optional<ProductInformation> findByUrl(URL url);

    default void saveIfAbsent(ProductInformation productInformation) {
        Optional<ProductInformation> productInformationFromDb = findByUrl(productInformation.getUrl());
        if (productInformationFromDb.isEmpty()) {
            save(productInformation);
        } else {
            productInformation.setId(productInformationFromDb.get().getId());
        }
    }
}
