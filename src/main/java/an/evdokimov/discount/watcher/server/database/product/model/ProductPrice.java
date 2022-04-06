package an.evdokimov.discount.watcher.server.database.product.model;

import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductPriceResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "product_price")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private BigDecimal price;
    private Double discount;
    private BigDecimal priceWithDiscount;
    private boolean isInStock;
    private String availabilityInformation;
    private LocalDateTime date;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductPrice)) return false;
        ProductPrice that = (ProductPrice) o;
        return isInStock() == that.isInStock() &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(getProduct().getId(), that.getProduct().getId()) &&
                Objects.equals(getPrice(), that.getPrice()) &&
                Objects.equals(getDiscount(), that.getDiscount()) &&
                Objects.equals(getPriceWithDiscount(), that.getPriceWithDiscount()) &&
                Objects.equals(getAvailabilityInformation(), that.getAvailabilityInformation()) &&
                Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getProduct().getId(), getPrice(), getDiscount(), getPriceWithDiscount(),
                isInStock(), getAvailabilityInformation(), getDate());
    }

    public Class<? extends ProductPriceResponse> getDtoClass() {
        return ProductPriceResponse.class;
    }

    @Override
    public String toString() {
        return "ProductPrice{" +
                "id=" + id +
                ", productId=" + product.getId() +
                ", price=" + price +
                ", discount=" + discount +
                ", priceWithDiscount=" + priceWithDiscount +
                ", isInStock=" + isInStock +
                ", availabilityInformation='" + availabilityInformation + '\'' +
                ", date=" + date +
                '}';
    }
}
