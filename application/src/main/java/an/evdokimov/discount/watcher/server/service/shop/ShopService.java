package an.evdokimov.discount.watcher.server.service.shop;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ShopService {
    @NotNull
    Shop getById(@NotNull Long id) throws ServerException;

    @NotNull
    Shop getByCookie(@NotNull String cookie) throws ServerException;

    @NotNull
    Collection<Shop> getAll();

    @NotNull
    Collection<Shop> getByUser(@NotNull User user);
}
