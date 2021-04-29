package an.evdokimov.discount.watcher.server.database.product.model;

import an.evdokimov.discount.watcher.server.api.product.dto.response.LentaProductResponse;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "product_price_lenta")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LentaProduct extends Product {
    private BigDecimal priceWithCard;

    @Override
    public Class<? extends ProductResponse> getDtoClass() {
        return LentaProductResponse.class;
    }
}
