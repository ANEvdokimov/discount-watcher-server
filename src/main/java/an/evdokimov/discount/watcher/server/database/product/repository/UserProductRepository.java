package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface UserProductRepository extends CrudRepository<UserProduct, Long> {
    Collection<UserProduct> findByUser(User user);
//
//    @Query("""
//            SELECT UserProduct.product FROM UserProduct
//                WHERE (
//                    UserProduct.monitor_availability = true OR
//                    UserProduct.monitor_discount = true OR
//                    UserProduct.monitor_price_changes = true
//                )
//            """)
//    Collection<Product> findAllActiveProducts();
}
