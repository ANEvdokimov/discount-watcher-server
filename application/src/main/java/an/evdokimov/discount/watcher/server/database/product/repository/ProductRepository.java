package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.PriceChange;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ---------------- BASIC OPERATIONS ----------------

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


    // ------------- USER'S PRODUCTS SEARCH -------------

    // -------- Find active and inactive products -------

    @Query("""
            SELECT DISTINCT up.product FROM UserProduct up
                WHERE up.user = :user
                  AND (:monitorAvailability is null OR up.monitorAvailability = :monitorAvailability)
                  AND (:monitorDiscount is null OR up.monitorDiscount = :monitorDiscount)
                  AND (:monitorPriceChanges is null OR up.monitorPriceChanges = :monitorPriceChanges)
            """)
    Collection<Product> findAllUserProducts(@Param("user") User user,
                                            @Param("monitorAvailability") @Nullable Boolean monitorAvailability,
                                            @Param("monitorDiscount") @Nullable Boolean monitorDiscount,
                                            @Param("monitorPriceChanges") @Nullable Boolean monitorPriceChanges
    );

    @Query("""
            SELECT DISTINCT up.product FROM UserProduct up
                WHERE up.user = :user
                  AND up.product.shop = :shop
                  AND (:monitorAvailability is null OR up.monitorAvailability = :monitorAvailability)
                  AND (:monitorDiscount is null OR up.monitorDiscount = :monitorDiscount)
                  AND (:monitorPriceChanges is null OR up.monitorPriceChanges = :monitorPriceChanges)
            """)
    Collection<Product> findAllUserProductsInShop(@Param("user") User user,
                                                  @Param("shop") Shop shop,
                                                  @Param("monitorAvailability") @Nullable Boolean monitorAvailability,
                                                  @Param("monitorDiscount") @Nullable Boolean monitorDiscount,
                                                  @Param("monitorPriceChanges") @Nullable Boolean monitorPriceChanges
    );


    // ------------ Find only active products -----------

    default Collection<Product> findActiveUserProducts(User user,
                                                       @Nullable Boolean monitorAvailability,
                                                       @Nullable Boolean monitorDiscount,
                                                       @Nullable Boolean monitorPriceChanges) {
        Collection<Product> allUsersProducts =
                findAllUserProducts(user, monitorAvailability, monitorDiscount, monitorPriceChanges);

        return allUsersProducts.stream()
                .filter(product -> filterActive(product, monitorAvailability, monitorDiscount, monitorPriceChanges))
                .toList();
    }

    default Collection<Product> findActiveUserProductsInShop(User user,
                                                             Shop shop,
                                                             @Nullable Boolean monitorAvailability,
                                                             @Nullable Boolean monitorDiscount,
                                                             @Nullable Boolean monitorPriceChanges) {
        Collection<Product> allUsersProductsInShop =
                findAllUserProductsInShop(user, shop, monitorAvailability, monitorDiscount, monitorPriceChanges);

        return allUsersProductsInShop.stream()
                .filter(product -> filterActive(product, monitorAvailability, monitorDiscount, monitorPriceChanges))
                .toList();
    }

    private boolean filterActive(@NotNull Product product,
                                 Boolean isAvailable,
                                 Boolean hasDiscount,
                                 Boolean isPriceChange) {
        ProductPrice price = product.getLastPrice();
        if (price == null) {
            return false;
        }

        boolean isNoFilter = isAvailable == null && hasDiscount == null && isPriceChange == null;

        return (((isAvailable != null || isNoFilter) && Optional.ofNullable(price.getIsInStock()).orElse(false))
                || ((hasDiscount != null || isNoFilter) && price.getDiscount() != null)
                || ((isPriceChange != null || isNoFilter) && price.getPriceChange() == PriceChange.DOWN)
        );
    }


    // ---------------- OTHER OPERATIONS ----------------

    @Query("SELECT up.product FROM UserProduct up WHERE up.user = :user")
    Collection<Product> findAllUserProducts(@Param("user") User user);

    @Query("""
            SELECT up.product FROM UserProduct up
                WHERE
                    up.user = :user AND
                    up.product.shop = :shop
            """)
    Collection<Product> findAllUserProductsInShop(@Param("user") User user, @Param("shop") Shop shop);
}
