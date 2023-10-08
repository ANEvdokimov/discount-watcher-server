package an.evdokimov.discount.watcher.server.api.product.dto.response;

import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A product in a specific shop.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;

    /**
     * Base information about product.
     */
    private ProductInformationResponse productInformation;

    /**
     * A shop where the product is sold.
     */
    private ShopResponse shop;

    /**
     * Actual (last) price.
     */
    private ProductPriceResponse lastPrice;
}
