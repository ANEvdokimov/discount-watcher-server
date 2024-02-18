package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

public interface ProductInformationService {
    @NotNull
    ProductInformation getById(@NotNull Long id) throws ServerException;

    @NotNull
    ProductInformation getOrCreateByUrl(@NotNull URL url);
}
