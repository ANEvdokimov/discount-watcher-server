package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.UserProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.UserProductResponse;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface UserProductService {
    UserProductResponse getById(@NotNull User user, @NotNull Long id) throws ServerException;

    List<UserProductResponse> getUserProducts(@NotNull User user,
                                              boolean onlyActive,
                                              @Nullable Boolean monitorAvailability,
                                              @Nullable Boolean monitorDiscount,
                                              @Nullable Boolean monitorPriceChanges);

    List<UserProductResponse> getUserProductsInShop(@NotNull User user,
                                                    @NotNull Long shopId,
                                                    boolean onlyActive,
                                                    @Nullable Boolean monitorAvailability,
                                                    @Nullable Boolean monitorDiscount,
                                                    @Nullable Boolean monitorPriceChanges)
            throws ServerException;

    void update(User user, UserProductRequest userProduct) throws ServerException;

    void delete(User user, Long userProductId) throws ServerException;
}
