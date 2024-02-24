package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface UserProductService {
    @NotNull
    UserProduct getById(@NotNull User user, @NotNull Long id) throws ServerException;

    @NotNull
    List<UserProduct> getAll(@NotNull User user,
                             boolean onlyActive,
                             @Nullable Boolean monitorAvailability,
                             @Nullable Boolean monitorDiscount,
                             @Nullable Boolean monitorPriceChanges);

    @NotNull
    List<UserProduct> getAll(@NotNull User user,
                             boolean onlyActive,
                             @Nullable Boolean monitorAvailability,
                             @Nullable Boolean monitorDiscount,
                             @Nullable Boolean monitorPriceChanges,
                             @NotNull Shop shop)
            throws ServerException;

    void update(@NotNull User user, @NotNull UserProduct userProduct) throws ServerException;

    void delete(@NotNull User user, @NotNull Long userProductId) throws ServerException;

    void saveOrUpdate(@NotNull UserProduct userProduct);
}
