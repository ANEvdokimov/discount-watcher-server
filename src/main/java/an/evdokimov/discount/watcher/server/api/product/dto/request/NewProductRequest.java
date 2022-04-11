package an.evdokimov.discount.watcher.server.api.product.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.net.URL;

/**
 * Information about added product
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewProductRequest {
    /**
     * A link to a product page in a shop cite.
     */
    @NotNull
    private URL url;

    /**
     * An id of shop in which the product is sold.
     */
    @NotNull
    private Long shopId;
}
