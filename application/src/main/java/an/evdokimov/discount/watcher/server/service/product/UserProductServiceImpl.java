package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.UserProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.UserProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.product.repository.UserProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.mapper.product.UserProductMapper;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProductServiceImpl implements UserProductService {
    private final UserProductRepository repository;
    private final ShopRepository shopRepository;//todo replace to service
    private final UserProductMapper mapper;

    @Override
    @NotNull
    public UserProductResponse getById(@NotNull User user, @NotNull Long id) throws ServerException {
        UserProduct userProduct = repository.findByIdAndUser(id, user).orElseThrow(() ->
                ServerErrorCode.USER_PRODUCT_NOT_FOUND.getException("user=%s, user_product=%s"
                        .formatted(user.getLogin(), id)));
        return mapper.map(userProduct);
    }

    @Override
    @NotNull
    public List<UserProductResponse> getUserProducts(@NotNull User user,
                                                     boolean onlyActive,
                                                     @Nullable Boolean monitorAvailability,
                                                     @Nullable Boolean monitorDiscount,
                                                     @Nullable Boolean monitorPriceChanges) {
        List<UserProduct> userProducts;
        if (onlyActive) {
            userProducts = repository.findActiveUserProducts(user, monitorAvailability, monitorDiscount,
                    monitorPriceChanges);
        } else {
            userProducts = repository.findAllUserProducts(user, monitorAvailability, monitorDiscount,
                    monitorPriceChanges);
        }

        return userProducts.stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    @Override
    @NotNull
    public List<UserProductResponse> getUserProductsInShop(@NotNull User user,
                                                           @NotNull Long shopId,
                                                           boolean onlyActive,
                                                           @Nullable Boolean monitorAvailability,
                                                           @Nullable Boolean monitorDiscount,
                                                           @Nullable Boolean monitorPriceChanges)
            throws ServerException {
        Shop shop = shopRepository
                .findById(shopId).orElseThrow(() -> new ServerException(ServerErrorCode.SHOP_NOT_FOUND));

        List<UserProduct> userProducts;
        if (onlyActive) {
            userProducts = repository.findActiveUserProductsInShop(user, shop, monitorAvailability,
                    monitorDiscount, monitorPriceChanges);
        } else {
            userProducts = repository.findAllUserProductsInShop(user, shop, monitorAvailability,
                    monitorDiscount, monitorPriceChanges);
        }

        return userProducts.stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    @Transactional
    public void update(@NotNull User user, @NotNull UserProductRequest updatedUserProduct) throws ServerException {
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
        UserProduct userProduct = repository.findByIdAndUser(userProductId, user).orElseThrow(() ->
                ServerErrorCode.USER_PRODUCT_NOT_FOUND.getException("user_product_id=%s, user_login=%s"
                        .formatted(userProductId, user.getLogin())));

        repository.delete(userProduct);
    }

    @Override
    @Transactional
    public void addOrUpdate(@NotNull UserProduct userProduct) {
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