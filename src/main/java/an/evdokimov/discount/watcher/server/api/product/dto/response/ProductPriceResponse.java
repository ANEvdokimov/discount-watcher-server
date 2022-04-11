package an.evdokimov.discount.watcher.server.api.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Status of a product in a specific date and in a specific shop.
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceResponse {
    private Long id;
    /**
     * A regular price of the product.
     */
    private BigDecimal price;

    /**
     * A discount of the product in percent.
     */
    private Double discount;

    /**
     * A price of the product with discount.
     */
    private BigDecimal priceWithDiscount;

    /**
     * Availability of the product.
     */
    private Boolean isInStock;

    /**
     * Detailed information about availability of the product.
     */
    private String availabilityInformation;

    /**
     * An updating date.
     */
    private LocalDateTime date;
}
