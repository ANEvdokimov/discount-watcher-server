package an.evdokimov.discount.watcher.server.api.product.maintenance;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.UserProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.UserProductResponse;
import an.evdokimov.discount.watcher.server.core.Maintenance;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.mapper.product.UserProductMapper;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import an.evdokimov.discount.watcher.server.service.product.UserProductService;
import an.evdokimov.discount.watcher.server.service.shop.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Maintenance
@Slf4j
@RequiredArgsConstructor
public class UserProductMaintenanceImpl implements UserProductMaintenance {
    private final UserProductService userProductService;
    private final ShopService shopService;
    private final UserProductMapper mapper;

    @Override
    @NotNull
    public UserProductResponse getById(@NotNull User user, @NotNull Long id) throws ServerException {
        log.trace("getting UserProducts by id={} for user={}", id, user.getLogin());

        UserProduct userProduct = userProductService.getById(user, id);
        return mapper.map(userProduct);
    }

    @Override
    @NotNull
    public List<UserProductResponse> getAll(@NotNull User user,
                                            boolean onlyActive,
                                            Boolean monitorAvailability,
                                            Boolean monitorDiscount,
                                            Boolean monitorPriceChanges) {
        log.trace("get all UserProducts for user={}", user.getLogin());

        List<UserProduct> userProducts = userProductService
                .getAll(user, onlyActive, monitorAvailability, monitorDiscount, monitorPriceChanges);

        return userProducts.stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    @Override
    @NotNull
    public List<UserProductResponse> getAll(@NotNull User user,
                                            boolean onlyActive,
                                            Boolean monitorAvailability,
                                            Boolean monitorDiscount,
                                            Boolean monitorPriceChanges,
                                            @NotNull Long shopId) throws ServerException {
        log.trace("getting UserProducts for user={} in shop={}", user.getLogin(), shopId);

        Shop shop = shopService.getById(shopId);

        List<UserProduct> userProducts = userProductService.getAll(user, onlyActive,
                monitorAvailability, monitorDiscount, monitorPriceChanges, shop);

        return userProducts.stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    @Transactional
    public void update(@NotNull User user, @NotNull UserProductRequest userProduct) throws ServerException {
        log.trace("updating UserProducts for user={}", user.getLogin());

        userProductService.update(user, mapper.map(userProduct));
    }

    @Override
    @Transactional
    public void delete(@NotNull User user, @NotNull Long userProductId) throws ServerException {
        log.trace("updating UserProduct id={} for user={}", userProductId, user.getLogin());

        userProductService.delete(user, userProductId);
    }
}
