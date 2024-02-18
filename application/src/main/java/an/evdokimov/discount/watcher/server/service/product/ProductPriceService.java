package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductPriceResponse;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductPriceService {
    @NotNull
    Optional<ProductPrice> findLastCompletedPriceByProduct(Product product);

    @NotNull
    ProductPrice getById(@NotNull Long id) throws ServerException;

    @NotNull
    List<ProductPriceResponse> getPrices(@NotNull Long productId, boolean group, LocalDate startDate) throws ServerException;

    void addPrice(@NotNull ProductPrice price);
}
