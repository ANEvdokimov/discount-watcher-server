package an.evdokimov.discount.watcher.server.service.shop;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopResponse;

import java.util.Collection;

public interface ShopService {
    ShopResponse getShopById(Long id) throws ServerException;

    Collection<ShopResponse> getAllShops();
}
