package an.evdokimov.discount.watcher.server.service.shop;

import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainResponse;

import java.util.Collection;

public interface ShopChainService {
    Collection<ShopChainResponse> getShopChains(boolean withShops, Long cityId);
}
