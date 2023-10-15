package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.PriceChange;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProductRepository extends JpaRepository<UserProduct, Long> {

    // -------- Find active and inactive products -------

    @Query("""
            SELECT DISTINCT up FROM UserProduct up
                WHERE up.user = :user
                  AND (:monitorAvailability is null OR up.monitorAvailability = :monitorAvailability)
                  AND (:monitorDiscount is null OR up.monitorDiscount = :monitorDiscount)
                  AND (:monitorPriceChanges is null OR up.monitorPriceChanges = :monitorPriceChanges)
            """)
    List<UserProduct> findAllUserProducts(@Param("user") User user,
                                          @Param("monitorAvailability") @Nullable Boolean monitorAvailability,
                                          @Param("monitorDiscount") @Nullable Boolean monitorDiscount,
                                          @Param("monitorPriceChanges") @Nullable Boolean monitorPriceChanges
    );

    @Query("""
            SELECT DISTINCT up FROM UserProduct up
                WHERE up.user = :user
                  AND up.product.shop = :shop
                  AND (:monitorAvailability is null OR up.monitorAvailability = :monitorAvailability)
                  AND (:monitorDiscount is null OR up.monitorDiscount = :monitorDiscount)
                  AND (:monitorPriceChanges is null OR up.monitorPriceChanges = :monitorPriceChanges)
            """)
    List<UserProduct> findAllUserProductsInShop(@Param("user") User user,
                                                @Param("shop") Shop shop,
                                                @Param("monitorAvailability") @Nullable Boolean monitorAvailability,
                                                @Param("monitorDiscount") @Nullable Boolean monitorDiscount,
                                                @Param("monitorPriceChanges") @Nullable Boolean monitorPriceChanges
    );


    // ------------ Find only active products -----------

    default List<UserProduct> findActiveUserProducts(User user,
                                                     @Nullable Boolean monitorAvailability,
                                                     @Nullable Boolean monitorDiscount,
                                                     @Nullable Boolean monitorPriceChanges) {
        List<UserProduct> allUsersProducts =
                findAllUserProducts(user, monitorAvailability, monitorDiscount, monitorPriceChanges);

        return allUsersProducts.stream()
                .filter(product -> filterActive(product, monitorAvailability, monitorDiscount, monitorPriceChanges))
                .toList();
    }

    default List<UserProduct> findActiveUserProductsInShop(User user,
                                                           Shop shop,
                                                           @Nullable Boolean monitorAvailability,
                                                           @Nullable Boolean monitorDiscount,
                                                           @Nullable Boolean monitorPriceChanges) {
        List<UserProduct> allUsersProductsInShop =
                findAllUserProductsInShop(user, shop, monitorAvailability, monitorDiscount, monitorPriceChanges);

        return allUsersProductsInShop.stream()
                .filter(product -> filterActive(product, monitorAvailability, monitorDiscount, monitorPriceChanges))
                .toList();
    }

    private boolean filterActive(@NotNull UserProduct userProduct,
                                 Boolean isAvailable,
                                 Boolean hasDiscount,
                                 Boolean isPriceChange) {
        ProductPrice price = userProduct.getProduct().getLastPrice();
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

    List<UserProduct> findByUser(User user);

    Optional<UserProduct> findByIdAndUser(Long id, User user);

    @Query("""
            SELECT up FROM UserProduct up
                WHERE
                    up.user = :user AND
                    up.product.shop = :shop
            """)
    List<UserProduct> findAllUserProductsInShop(@Param("user") User user, @Param("shop") Shop shop);

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
