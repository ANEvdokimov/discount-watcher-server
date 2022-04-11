package an.evdokimov.discount.watcher.server.api.shop.dto.response;

import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Information about shop.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopResponse {
    private Long id;

    /**
     * Information about a connected shop chain.
     */
    private ShopChainResponse shopChain;

    /**
     * A name of the shop.
     */
    private String name;

    /**
     * A city where the shop is.
     */
    private CityResponse city;

    /**
     * An address of the shop.
     */
    private String address;
}
