package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.response.UserProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.product.repository.UserProductRepository;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.mapper.product.UserProductMapper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProductServiceImpl implements UserProductService {
    private final UserProductRepository repository;
    private final ShopRepository shopRepository;
    private final UserProductMapper mapper;

    @Override
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
}