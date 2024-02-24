package an.evdokimov.discount.watcher.server.api.product.maintenance;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductPriceResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;

public interface ProductPriceMaintenance {

    @NotNull
    List<ProductPriceResponse> getByProduct(@NotNull Long productId,
                                            boolean group,
                                            @Nullable LocalDate startDate) throws ServerException;
}
