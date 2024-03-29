package an.evdokimov.discount.watcher.server.api.shop.maintenance;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopResponse;
import an.evdokimov.discount.watcher.server.core.Maintenance;
import an.evdokimov.discount.watcher.server.mapper.shop.ShopMapper;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import an.evdokimov.discount.watcher.server.service.shop.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

@Maintenance
@Slf4j
@RequiredArgsConstructor
public class ShopMaintenanceImpl implements ShopMaintenance {
    private final ShopService shopService;
    private final ShopMapper shopMapper;

    @Override
    @NotNull
    public ShopResponse getById(@NotNull Long id) throws ServerException {
        log.trace("getting shop by id={}", id);

        return shopMapper.map(shopService.getById(id));
    }

    @Override
    @NotNull
    public Collection<ShopResponse> getAll() {
        log.trace("getting all shops");

        return shopService.getAll().stream()
                .map(shopMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    @NotNull
    public Collection<ShopResponse> getAllByUser(@NotNull User user) {
        log.trace("getting all user's shop: [user={}]", user.getLogin());

        return shopService.getByUser(user).stream()
                .map(shopMapper::map)
                .collect(Collectors.toList());
    }
}
