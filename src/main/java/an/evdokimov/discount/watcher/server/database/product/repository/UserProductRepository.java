package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserProductRepository extends JpaRepository<UserProduct, Long> {
    Collection<UserProduct> findByUser(User user);

    Optional<UserProduct> findByUserAndProduct(User user, Product product);

    default void saveOrUpdate(UserProduct userProduct) {
        Optional<UserProduct> userProductFromDb =
                findByUserAndProduct(userProduct.getUser(), userProduct.getProduct());
        if (userProductFromDb.isEmpty()) {
            save(userProduct);
        } else {
            userProduct.setId(userProductFromDb.get().getId());
            save(userProduct);
        }
    }
}
