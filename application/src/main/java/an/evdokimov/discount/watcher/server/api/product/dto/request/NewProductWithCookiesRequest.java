package an.evdokimov.discount.watcher.server.api.product.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;

/**
 * Information about added product
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewProductWithCookiesRequest {
    /**
     * A link to a product page in a shop cite.
     */
    @NotNull
    private URL url;

    /**
     * A shop cookie from product page.
     */
    @NotNull
    private String cookies;

    /**
     * A product discount tracking flag.
     */
    @NotNull
    private Boolean monitorDiscount;

    /**
     * A product availability in the shop tracking flag.
     */
    @NotNull
    private Boolean monitorAvailability;

    /**
     * A price change tracking flag.
     */
    @NotNull
    private Boolean monitorPriceChanges;
}
