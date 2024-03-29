package an.evdokimov.discount.watcher.server.api.shop.maintenance;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopResponse;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ShopMaintenance {
    @NotNull
    ShopResponse getById(@NotNull Long id) throws ServerException;

    @NotNull
    Collection<ShopResponse> getAll();

    @NotNull
    Collection<ShopResponse> getAllByUser(@NotNull User user);
}
