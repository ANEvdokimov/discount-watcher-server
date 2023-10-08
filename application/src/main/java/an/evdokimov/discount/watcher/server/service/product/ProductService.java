package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.amqp.dto.ParsedProductInformation;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductWithCookiesRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ProductService {
    ProductResponse getProduct(@NotNull Long id) throws ServerException;

    Collection<ProductResponse> getUserProducts(@NotNull User user,
                                                boolean onlyActive,
                                                @Nullable Boolean monitorAvailability,
                                                @Nullable Boolean monitorDiscount,
                                                @Nullable Boolean monitorPriceChanges);

    Collection<ProductResponse> getUserProductsInShop(@NotNull User user,
                                                      @NotNull Long shopId,
                                                      boolean onlyActive,
                                                      @Nullable Boolean monitorAvailability,
                                                      @Nullable Boolean monitorDiscount,
                                                      @Nullable Boolean monitorPriceChanges)
            throws ServerException;

    void addProduct(@NotNull User user, @NotNull NewProductRequest newProduct) throws ServerException;

    void addProduct(@NotNull User user, @NotNull NewProductWithCookiesRequest newProduct) throws ServerException;

    void updateProduct(@NotNull Long id) throws ServerException;

    void updateProduct(@NotNull Product product);

    void saveParsedProduct(@NotNull ParsedProductInformation parsedProduct) throws ServerException;
}
