package an.evdokimov.discount.watcher.server.api.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Information about shop chain.
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ShopChainResponse {
    private Long id;

    /**
     * A name of the shop chain in English.
     */
    private String name;

    /**
     * A name of the shop chain in Russian.
     */
    private String cyrillicName;
}
