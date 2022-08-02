package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;

import java.util.Collection;

public interface ProductService {
    ProductResponse getProduct(@NotNull Long id, boolean withPriceHistory) throws ServerException;

    Collection<ProductResponse> getUserProducts(@NotNull User user,
                                                boolean withPriceHistory,
                                                boolean onlyActive,
                                                @Nullable Boolean monitorAvailability,
                                                @Nullable Boolean monitorDiscount,
                                                @Nullable Boolean monitorPriceChanges);

    Collection<ProductResponse> getUserProductsInShop(@NotNull User user,
                                                      @NotNull Long shopId,
                                                      boolean withPriceHistory,
                                                      boolean onlyActive,
                                                      @Nullable Boolean monitorAvailability,
                                                      @Nullable Boolean monitorDiscount,
                                                      @Nullable Boolean monitorPriceChanges)
            throws ServerException;

    ProductResponse addProduct(@NotNull User user, @NotNull NewProductRequest newProduct) throws ServerException;

    Product updateProduct(@NotNull Product product) throws ServerException;
}
