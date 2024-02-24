package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.product.repository.UserProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProductServiceImpl implements UserProductService {
    private final UserProductRepository repository;

    @Override
    @NotNull
    public UserProduct getById(@NotNull User user, @NotNull Long id) throws ServerException {
        log.trace("getting user products by id={} for user={}", id, user);

        return repository.findByIdAndUser(id, user).orElseThrow(() ->
                ServerErrorCode.USER_PRODUCT_NOT_FOUND.getException("user=%s, user_product=%s"
                        .formatted(user.getLogin(), id)));
    }

    @Override
    @NotNull
    public List<UserProduct> getAll(@NotNull User user,
                                    boolean onlyActive,
                                    @Nullable Boolean monitorAvailability,
                                    @Nullable Boolean monitorDiscount,
                                    @Nullable Boolean monitorPriceChanges) {
        log.trace("get all products for user={}", user.getLogin());

        List<UserProduct> userProducts;
        if (onlyActive) {
            userProducts = repository.findActiveUserProducts(user, monitorAvailability, monitorDiscount,
                    monitorPriceChanges);
        } else {
            userProducts = repository.findAllUserProducts(user, monitorAvailability, monitorDiscount,
                    monitorPriceChanges);
        }

        return userProducts;
    }

    @Override
    @NotNull
    public List<UserProduct> getAll(@NotNull User user,
                                    boolean onlyActive,
                                    @Nullable Boolean monitorAvailability,
                                    @Nullable Boolean monitorDiscount,
                                    @Nullable Boolean monitorPriceChanges,
                                    @NotNull Shop shop) {
        log.trace("getting products for user={} in shop={}", user.getLogin(), shop.getId());

        List<UserProduct> userProducts;
        if (onlyActive) {
            userProducts = repository.findActiveUserProductsInShop(user, shop, monitorAvailability,
                    monitorDiscount, monitorPriceChanges);
        } else {
            userProducts = repository.findAllUserProductsInShop(user, shop, monitorAvailability,
                    monitorDiscount, monitorPriceChanges);
        }

        return userProducts;
    }

    @Override
    @Transactional
    public void update(@NotNull User user, @NotNull UserProduct updatedUserProduct) throws ServerException {
        log.trace("updating UserProducts for user={}", user.getLogin());

        UserProduct userProduct = repository.findByIdAndUser(updatedUserProduct.getId(), user).orElseThrow(() ->
                ServerErrorCode.USER_PRODUCT_NOT_FOUND.getException("user_product_id=%s, user_login=%s"
                        .formatted(updatedUserProduct.getId(), user.getLogin())));

        userProduct.setMonitorDiscount(updatedUserProduct.isMonitorDiscount());
        userProduct.setMonitorAvailability(updatedUserProduct.isMonitorAvailability());
        userProduct.setMonitorPriceChanges(updatedUserProduct.isMonitorPriceChanges());
    }

    @Override
    @Transactional
    public void delete(@NotNull User user, @NotNull Long userProductId) throws ServerException {
        log.trace("updating UserProduct id={} for user={}", userProductId, user.getLogin());

        UserProduct userProduct = repository.findByIdAndUser(userProductId, user).orElseThrow(() ->
                ServerErrorCode.USER_PRODUCT_NOT_FOUND.getException("user_product_id=%s, user_login=%s"
                        .formatted(userProductId, user.getLogin())));

        repository.delete(userProduct);
    }

    @Override
    @Transactional
    public void saveOrUpdate(@NotNull UserProduct userProduct) {
        Optional<UserProduct> userProductFromDb =
                repository.findByUserAndProduct(userProduct.getUser(), userProduct.getProduct());
        if (userProductFromDb.isEmpty()) {
            repository.save(userProduct);
        } else {
            userProduct.setId(userProductFromDb.get().getId());
            repository.save(userProduct);
        }
    }
}