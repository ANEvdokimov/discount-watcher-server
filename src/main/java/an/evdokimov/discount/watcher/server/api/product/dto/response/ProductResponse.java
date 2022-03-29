package an.evdokimov.discount.watcher.server.api.product.dto.response;

import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private ProductInformationResponse productInformation;
    private ShopResponse shop;
    private List<ProductPriceResponse> prices;
}
