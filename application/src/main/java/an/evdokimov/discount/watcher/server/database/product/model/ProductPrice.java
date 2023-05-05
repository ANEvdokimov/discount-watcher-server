package an.evdokimov.discount.watcher.server.database.product.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "product_price")
@Inheritance(strategy = InheritanceType.JOINED)
@DynamicUpdate
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPrice {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_price_id_generator"
    )
    @SequenceGenerator(
            name = "product_price_id_generator",
            sequenceName = "product_price_sequence",
            allocationSize = 1
    )
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id")
    @NotNull
    @ToString.Exclude
    private Product product;
    private BigDecimal price;
    private Double discount;
    private BigDecimal priceWithDiscount;
    private Boolean isInStock;
    private String availabilityInformation;
    private LocalDateTime date;
    @Enumerated(EnumType.STRING)
    @NotNull
    private ParsingStatus parsingStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductPrice price1 = (ProductPrice) o;
        return Objects.equals(getId(), price1.getId())
                && Objects.equals(getProduct().getId(), price1.getProduct().getId())
                && Objects.equals(getPrice(), price1.getPrice())
                && Objects.equals(getDiscount(), price1.getDiscount())
                && Objects.equals(getPriceWithDiscount(), price1.getPriceWithDiscount())
                && Objects.equals(getIsInStock(), price1.getIsInStock())
                && Objects.equals(getAvailabilityInformation(), price1.getAvailabilityInformation())
                && Objects.equals(getDate(), price1.getDate())
                && getParsingStatus() == price1.getParsingStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getProduct().getId(), getPrice(), getDiscount(), getPriceWithDiscount(),
                getIsInStock(), getAvailabilityInformation(), getDate(), getParsingStatus());
    }

    // toString parameters for lombok
    @ToString.Include(name = "productId")
    private Long getProductId() {
        return getProduct().getId();
    }
}
