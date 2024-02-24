package an.evdokimov.discount.watcher.server.api.product.controller;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductPriceResponse;
import an.evdokimov.discount.watcher.server.api.product.maintenance.ProductPriceMaintenance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/prices")
@Slf4j
@RequiredArgsConstructor
public class ProductPriceController {
    private final ProductPriceMaintenance priceMaintenance;

    /**
     * Getting price history for product.
     *
     * @param productId product ID for which you need to get a history.
     * @param group     group result by date and price (default = false).
     * @param startDate get prices after this date.
     * @return list of prices.
     * @throws ServerException any internal errors.
     */
    @GetMapping(value = "/byProduct/{productId}")
    public List<ProductPriceResponse> getPrices(@PathVariable("productId") Long productId,
                                                @RequestHeader("group") @Nullable Boolean group,
                                                @RequestHeader("start-date") @Nullable LocalDate startDate)
            throws ServerException {
        log.info("Getting prices for product [{}]. group={}, startDate={}", productId, group, startDate);
        if (group == null) {
            group = false;
        }

        return priceMaintenance.getByProduct(productId, group, startDate);
    }
}
