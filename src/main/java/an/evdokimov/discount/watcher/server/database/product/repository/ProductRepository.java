package an.evdokimov.discount.watcher.server.database.product.repository;

import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ---------------- BASIC OPERATIONS ----------------

    default void saveIfAbsent(Product product) {
        Optional<Product> productFromDb =
                findByProductInformationAndShop(product.getProductInformation(), product.getShop());
        if (productFromDb.isEmpty()) {
            save(product);
        } else {
            product.setId(productFromDb.get().getId());
        }
    }

    Optional<Product> findByProductInformationAndShop(ProductInformation productInformation, Shop shop);

    @Query("""
            SELECT p FROM Product p
                LEFT JOIN FETCH p.prices pp
            WHERE p.id = :id AND pp.date = (SELECT MAX(pp2.date) FROM ProductPrice pp2 WHERE pp2.product = p)
            """)
    Optional<Product> findByIdWithLastPrice(@Param("id") Long id);

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
            SELECT p FROM Product p
                LEFT JOIN FETCH p.prices pp
            WHERE
                pp.date = (SELECT MAX(pp2.date) FROM ProductPrice pp2 WHERE pp2.product = p) AND
                p.id in (
                    SELECT DISTINCT up.product FROM UserProduct up
                        WHERE up.user = :user
                          AND (:monitorAvailability is null OR up.monitorAvailability = :monitorAvailability)
                          AND (:monitorDiscount is null OR up.monitorDiscount = :monitorDiscount)
                          AND (:monitorPriceChanges is null OR up.monitorPriceChanges = :monitorPriceChanges)
                        )
            """)
    Collection<Product> findAllUserProductsWithLastPrice(@Param("user") User user,
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

    @Query("""
            SELECT p FROM Product p
                LEFT JOIN FETCH p.prices pp
            WHERE
                pp.date = (SELECT MAX(pp2.date) FROM ProductPrice pp2 WHERE pp2.product = p) AND
                p.shop = :shop AND
                p.id in (
                    SELECT DISTINCT up.product FROM UserProduct up
                        WHERE up.user = :user
                          AND (:monitorAvailability is null OR up.monitorAvailability = :monitorAvailability)
                          AND (:monitorDiscount is null OR up.monitorDiscount = :monitorDiscount)
                          AND (:monitorPriceChanges is null OR up.monitorPriceChanges = :monitorPriceChanges)
                )
            """)
    Collection<Product> findAllUserProductsWithLastPriceInShop(@Param("user") User user,
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
                .filter(product -> monitorAvailability == null || isProductAvailable(product))
                .filter(product -> monitorDiscount == null || doesProductHaveDiscount(product))
                .toList();//todo filter for price changes
    }

    default Collection<Product> findActiveUserProductsWithLastPrice(User user,
                                                                    @Nullable Boolean monitorAvailability,
                                                                    @Nullable Boolean monitorDiscount,
                                                                    @Nullable Boolean monitorPriceChanges) {
        Collection<Product> allUsersProductsWithLastPrice =
                findAllUserProductsWithLastPrice(user, monitorAvailability, monitorDiscount, monitorPriceChanges);

        return allUsersProductsWithLastPrice.stream()
                .filter(product -> monitorAvailability == null || isProductAvailable(product))
                .filter(product -> monitorDiscount == null || doesProductHaveDiscount(product))
                .toList();//todo filter for price changes
    }

    default Collection<Product> findActiveUserProductsInShop(User user,
                                                             Shop shop,
                                                             @Nullable Boolean monitorAvailability,
                                                             @Nullable Boolean monitorDiscount,
                                                             @Nullable Boolean monitorPriceChanges) {
        Collection<Product> allUsersProductsInShop =
                findAllUserProductsInShop(user, shop, monitorAvailability, monitorDiscount, monitorPriceChanges);

        return allUsersProductsInShop.stream()
                .filter(product -> monitorAvailability == null || isProductAvailable(product))
                .filter(product -> monitorDiscount == null || doesProductHaveDiscount(product))
                .toList();//todo filter for price changes
    }

    default Collection<Product> findActiveUserProductsWithLastPriceInShop(User user,
                                                                          Shop shop,
                                                                          @Nullable Boolean monitorAvailability,
                                                                          @Nullable Boolean monitorDiscount,
                                                                          @Nullable Boolean monitorPriceChanges) {
        Collection<Product> allUsersProductsWithLastPriceInShop =
                findAllUserProductsWithLastPriceInShop(
                        user, shop, monitorAvailability, monitorDiscount, monitorPriceChanges
                );
        return allUsersProductsWithLastPriceInShop.stream()
                .filter(product -> monitorAvailability == null || isProductAvailable(product))
                .filter(product -> monitorDiscount == null || doesProductHaveDiscount(product))
                .toList();//todo filter for price changes
    }

    private boolean isProductAvailable(Product product) {
        if (product == null) {
            return false;
        }
        if (product.getPrices().isEmpty()) {
            return false;
        }
        return product.getPrices().get(0).getIsInStock();
    }

    private boolean doesProductHaveDiscount(Product product) {
        if (product == null) {
            return false;
        }
        if (product.getPrices().isEmpty()) {
            return false;
        }
        return product.getPrices().get(0).getDiscount() != null;
    }


    // ---------------- OTHER OPERATIONS ----------------

    @Query("SELECT up.product FROM UserProduct up WHERE up.user = :user")
    Collection<Product> findAllUserProducts(@Param("user") User user);

    @Query("""
            SELECT p FROM Product p
                LEFT JOIN FETCH p.prices pp
            WHERE
                p.id in (SELECT up.product.id FROM UserProduct up WHERE up.user = :user) AND
                pp.date = (SELECT MAX(pp2.date) FROM ProductPrice pp2 WHERE pp2.product = p)
            """)
    Collection<Product> findAllUserProductsWithLastPrice(@Param("user") User user);

    @Query("""
            SELECT up.product FROM UserProduct up
                WHERE
                    up.user = :user AND
                    up.product.shop = :shop
            """)
    Collection<Product> findAllUserProductsInShop(@Param("user") User user, @Param("shop") Shop shop);

    @Query("""
            SELECT p FROM Product p
                LEFT JOIN FETCH p.prices pp
            WHERE
                pp.date = (SELECT MAX(pp2.date) FROM ProductPrice pp2 WHERE pp2.product = p) AND
                p.shop = :shop AND
                p.id in (
                    SELECT DISTINCT up.product FROM UserProduct up
                        WHERE up.user = :user
                )
            """)
    Collection<Product> findAllUserProductsWithLastPriceInShop(@Param("user") User user, @Param("shop") Shop shop);
}
