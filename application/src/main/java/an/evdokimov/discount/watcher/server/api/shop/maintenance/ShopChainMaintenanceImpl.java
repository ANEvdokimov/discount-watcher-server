package an.evdokimov.discount.watcher.server.api.shop.maintenance;

import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainResponse;
import an.evdokimov.discount.watcher.server.core.Maintenance;
import an.evdokimov.discount.watcher.server.database.shop.model.ShopChain;
import an.evdokimov.discount.watcher.server.mapper.shop.ShopChainMapper;
import an.evdokimov.discount.watcher.server.service.shop.ShopChainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

@Maintenance
@Slf4j
@RequiredArgsConstructor
public class ShopChainMaintenanceImpl implements ShopChainMaintenance {
    private final ShopChainService shopChainService;
    private final ShopChainMapper shopChainMapper;

    @Override
    @NotNull
    public Collection<ShopChainResponse> getAll(boolean withShops, Long cityId) {
        log.trace("getting all shop chains with parameters: [withShops={}; cityId={}]", withShops, cityId);

        Collection<ShopChain> shopChains = shopChainService.getAll(cityId);

        if (withShops) {
            return shopChains.stream()
                    .map(shopChainMapper::toDtoWithShops)
                    .collect(Collectors.toList());
        } else {
            return shopChains.stream()
                    .map(shopChainMapper::toDto)
                    .collect(Collectors.toList());
        }
    }
}
