package an.evdokimov.discount.watcher.server.api.product.dto.response;

import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceResponse {
    private Long id;
    private ProductInformationResponse productInformation;
    private ShopResponse shop;
    private BigDecimal price;
    private Double discount;
    private BigDecimal priceWithDiscount;
    private boolean isInStock;
    private String availabilityInformation;
    private LocalDateTime date;
}
