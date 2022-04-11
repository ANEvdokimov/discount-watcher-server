package an.evdokimov.discount.watcher.server.api.shop.dto.response;

import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopResponse {
    private Long id;
    private ShopChainResponse shopChain;
    private String name;
    private CityResponse city;
    private String address;
}
