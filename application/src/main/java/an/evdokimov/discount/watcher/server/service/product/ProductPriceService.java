package an.evdokimov.discount.watcher.server.service.product;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductPriceResponse;

import java.time.LocalDate;
import java.util.List;

public interface ProductPriceService {
    List<ProductPriceResponse> getPrices(Long productId, boolean group, LocalDate startDate) throws ServerException;
}
