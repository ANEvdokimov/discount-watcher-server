package an.evdokimov.discount.watcher.server.api.shop.maintenance;

import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ShopChainMaintenance {
    @NotNull
    Collection<ShopChainResponse> getShopChains(boolean withShops, @Nullable Long cityId);
}
