package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    default Product findOrCreateByProductInformationAndShop(ProductInformation productInformation, Shop shop) {
        Optional<Product> productFromDb =
                findByProductInformationAndShop(productInformation, shop);
        if (productFromDb.isEmpty()) {
            Product product = Product.builder().productInformation(productInformation).shop(shop).build();
            save(product);
            return product;
        } else {
            return productFromDb.get();
        }
    }

    Optional<Product> findByProductInformationAndShop(ProductInformation productInformation, Shop shop);

    @Query("""
            SELECT DISTINCT up.product FROM UserProduct up
                WHERE (
                    up.monitorAvailability = true OR
                    up.monitorDiscount = true OR
                    up.monitorPriceChanges = true
                )
            """)
    Collection<Product> findAllTrackedProducts();
}
