package an.evdokimov.discount.watcher.server.api.product.maintenance;

import an.evdokimov.discount.watcher.server.amqp.dto.ParsedProductInformation;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductWithCookiesRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import org.jetbrains.annotations.NotNull;

public interface ProductMaintenance {
    @NotNull
    ProductResponse getProduct(@NotNull Long id) throws ServerException;

    @Deprecated
    void addProduct(@NotNull User user, @NotNull NewProductRequest newProduct) throws ServerException;

    void addProduct(@NotNull User user, @NotNull NewProductWithCookiesRequest newProduct) throws ServerException;

    void saveParsedProduct(@NotNull ParsedProductInformation parsedProduct) throws ServerException;

    void updateProduct(@NotNull Long id) throws ServerException;

    void updateTrackedProducts();
}
