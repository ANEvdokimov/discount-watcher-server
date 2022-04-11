package an.evdokimov.discount.watcher.server.api.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;

/**
 * Base information about product
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInformationResponse {
    private Long id;
    /**
     * A name of the product.
     */
    private String name;

    /**
     * A link to page of product in shop cite.
     */
    private URL url;
}
