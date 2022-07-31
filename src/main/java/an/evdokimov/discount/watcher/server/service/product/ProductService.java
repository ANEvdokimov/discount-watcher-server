package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ProductService {
    ProductResponse getProduct(@NotNull Long id, boolean withPriceHistory) throws ServerException;

    Collection<ProductResponse> getUserProducts(@NotNull User user,
                                                boolean withPriceHistory,
                                                boolean onlyActive,
                                                boolean monitorAvailability,
                                                boolean monitorDiscount,
                                                boolean monitorPriceChanges);

    Collection<ProductResponse> getUserProductsInShop(@NotNull User user,
                                                      @NotNull Long shopId,
                                                      boolean withPriceHistory,
                                                      boolean onlyActive,
                                                      boolean monitorAvailability,
                                                      boolean monitorDiscount,
                                                      boolean monitorPriceChanges)
            throws ServerException;

    ProductResponse addProduct(@NotNull User user, @NotNull NewProductRequest newProduct) throws ServerException;

    Product updateProduct(@NotNull Product product) throws ServerException;
}
