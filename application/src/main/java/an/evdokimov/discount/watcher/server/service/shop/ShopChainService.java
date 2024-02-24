package an.evdokimov.discount.watcher.server.service.shop;

import an.evdokimov.discount.watcher.server.database.shop.model.ShopChain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ShopChainService {
    @NotNull
    Collection<ShopChain> getAll(@Nullable Long cityId);
}
