package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ProductService {
    @NotNull
    Product getById(@NotNull Long id) throws ServerException;

    @NotNull
    Product getOrCreateByProductInformationAndShop(ProductInformation information, Shop shop);

    @NotNull
    Collection<Product> getAllTrackedProducts();

    void saveProduct(@NotNull Product product);
}
