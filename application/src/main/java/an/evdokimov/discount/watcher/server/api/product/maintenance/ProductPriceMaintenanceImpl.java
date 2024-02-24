package an.evdokimov.discount.watcher.server.api.product.maintenance;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductPriceResponse;
import an.evdokimov.discount.watcher.server.core.Maintenance;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.mapper.product.ProductPriceMapper;
import an.evdokimov.discount.watcher.server.service.product.ProductPriceService;
import an.evdokimov.discount.watcher.server.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

@Maintenance
@Slf4j
@RequiredArgsConstructor
public class ProductPriceMaintenanceImpl implements ProductPriceMaintenance {
    private final ProductPriceService priceService;
    private final ProductService productService;
    private final ProductPriceMapper mapper;


    @Override
    @NotNull
    public List<ProductPriceResponse> getByProduct(@NotNull Long productId, boolean group, LocalDate startDate)
            throws ServerException {
        log.trace("getting prices [productId={}, group={}, startDate={}]", productId, group, startDate);

        Product product = productService.getById(productId);

        return priceService.getByProduct(product, group, startDate).stream()
                .map(mapper::map)
                .toList();
    }
}
