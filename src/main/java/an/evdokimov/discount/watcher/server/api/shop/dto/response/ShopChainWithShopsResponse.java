package an.evdokimov.discount.watcher.server.api.shop.dto.response;

import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ShopChainWithShopsResponse extends ShopChainResponse {
    private List<ShopResponse> shops;

    @Data
    static class ShopResponse {
        private Long id;
        private String name;
        private CityResponse city;
        private String address;
    }
}
