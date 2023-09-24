package an.evdokimov.discount.watcher.server.database.product.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    public LentaProductPrice(Long id, Product product, BigDecimal price, Double discount, BigDecimal priceWithDiscount,
                             Boolean isInStock, String availabilityInformation, LocalDateTime date,
                             PriceChange priceChange, ParsingStatus parsingStatus, BigDecimal priceWithCard) {
        super(id, product, price, discount, priceWithDiscount, isInStock, availabilityInformation, date, parsingStatus,
                priceChange);
        this.priceWithCard = priceWithCard;
    }

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
