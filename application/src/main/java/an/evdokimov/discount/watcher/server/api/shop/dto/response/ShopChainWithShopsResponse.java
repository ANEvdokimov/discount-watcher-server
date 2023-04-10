package an.evdokimov.discount.watcher.server.api.shop.dto.response;

import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Extension of a shop chain information with a shop list.
 */

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ShopChainWithShopsResponse extends ShopChainResponse {
    /**
     * A list of connected shops.
     */
    private List<ShopResponse> shops;

    /**
     * Information about shop.
     */
    @Data
    static class ShopResponse {
        private Long id;

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
}
