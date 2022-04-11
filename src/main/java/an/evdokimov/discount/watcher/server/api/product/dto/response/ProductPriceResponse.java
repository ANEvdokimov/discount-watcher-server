package an.evdokimov.discount.watcher.server.api.product.dto.response;

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
    private BigDecimal price;
    private Double discount;
    private BigDecimal priceWithDiscount;
    private Boolean isInStock;
    private String availabilityInformation;
    private LocalDateTime date;
}
