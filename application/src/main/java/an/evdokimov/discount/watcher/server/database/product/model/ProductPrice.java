package an.evdokimov.discount.watcher.server.database.product.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private LocalDateTime creationDate;
    private LocalDateTime parsingDate;
    @Enumerated(EnumType.STRING)
    @NotNull
    private ParsingStatus parsingStatus;
    @Enumerated(EnumType.STRING)
    private PriceChange priceChange;
    @Version
    private Long version;

    public ProductPrice(Long id, Product product, BigDecimal price, Double discount, BigDecimal priceWithDiscount,
                        Boolean isInStock, String availabilityInformation, LocalDateTime creationDate,
                        LocalDateTime parsingDate, ParsingStatus parsingStatus, PriceChange priceChange) {
        this.id = id;
        this.product = product;
        this.price = price;
        this.discount = discount;
        this.priceWithDiscount = priceWithDiscount;
        this.isInStock = isInStock;
        this.availabilityInformation = availabilityInformation;
        this.creationDate = creationDate;
        this.parsingDate = parsingDate;
        this.parsingStatus = parsingStatus;
        this.priceChange = priceChange;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductPrice otherPrice = (ProductPrice) o;
        return Objects.equals(getId(), otherPrice.getId())
                && Objects.equals(getProduct().getId(), otherPrice.getProduct().getId())
                && Objects.equals(getPrice(), otherPrice.getPrice())
                && Objects.equals(getDiscount(), otherPrice.getDiscount())
                && Objects.equals(getPriceWithDiscount(), otherPrice.getPriceWithDiscount())
                && Objects.equals(getIsInStock(), otherPrice.getIsInStock())
                && Objects.equals(getAvailabilityInformation(), otherPrice.getAvailabilityInformation())
                && Objects.equals(getCreationDate(), otherPrice.getCreationDate())
                && Objects.equals(getParsingDate(), otherPrice.getParsingDate())
                && getPriceChange() == otherPrice.getPriceChange()
                && getParsingStatus() == otherPrice.getParsingStatus()
                && Objects.equals(getVersion(), otherPrice.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getProduct().getId(), getPrice(), getDiscount(), getPriceWithDiscount(),
                getIsInStock(), getAvailabilityInformation(), getCreationDate(), getParsingDate(), getParsingStatus(),
                getPriceChange(), getVersion());
    }

    // toString parameters for lombok
    @ToString.Include(name = "productId")
    private Long getProductId() {
        return getProduct().getId();
    }
}
