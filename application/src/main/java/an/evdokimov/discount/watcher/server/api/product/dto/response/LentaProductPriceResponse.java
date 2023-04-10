package an.evdokimov.discount.watcher.server.api.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Extension for ProductPriceResponse for "Lenta" shop.
 */

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LentaProductPriceResponse extends ProductPriceResponse {
    /**
     * A price of a lenta product with a discount card.
     */
    private BigDecimal priceWithCard;
}
