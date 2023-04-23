package an.evdokimov.discount.watcher.server.database.product.model;

import jakarta.persistence.*;
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
    private Product product;
    private BigDecimal price;
    private Double discount;
    private BigDecimal priceWithDiscount;
    private Boolean isInStock;
    private String availabilityInformation;
    private LocalDateTime date;
    @Enumerated(EnumType.STRING)
    private ParsingStatus parsingStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductPrice that = (ProductPrice) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
