package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.UserProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.UserProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface UserProductService {
    @NotNull
    UserProductResponse getById(@NotNull User user, @NotNull Long id) throws ServerException;

    @NotNull
    List<UserProductResponse> getUserProducts(@NotNull User user,
                                              boolean onlyActive,
                                              @Nullable Boolean monitorAvailability,
                                              @Nullable Boolean monitorDiscount,
                                              @Nullable Boolean monitorPriceChanges);

    @NotNull
    List<UserProductResponse> getUserProductsInShop(@NotNull User user,
                                                    @NotNull Long shopId,
                                                    boolean onlyActive,
                                                    @Nullable Boolean monitorAvailability,
                                                    @Nullable Boolean monitorDiscount,
                                                    @Nullable Boolean monitorPriceChanges)
            throws ServerException;

    void update(@NotNull User user, @NotNull UserProductRequest userProduct) throws ServerException;

    void delete(@NotNull User user, @NotNull Long userProductId) throws ServerException;

    void addOrUpdate(@NotNull UserProduct userProduct);
}
