package an.evdokimov.discount.watcher.server.database.product.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "product_price_lenta")
@DynamicUpdate
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LentaProductPrice extends ProductPrice {
    private BigDecimal priceWithCard;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        LentaProductPrice that = (LentaProductPrice) o;
        return super.equals(that)
                && Objects.equals(getPriceWithCard(), that.getPriceWithCard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPriceWithCard());
    }
}
