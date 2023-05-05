package an.evdokimov.discount.watcher.server.database.product.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(name = "parsing_error")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ParsingError {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "parsing_error_id_generator"
    )
    @SequenceGenerator(
            name = "parsing_error_id_generator",
            sequenceName = "parsing_error_sequence",
            allocationSize = 1
    )
    private Long id;
    private String message;
    @OneToOne
    @JoinColumn(name = "product_price_id")
    @ToString.Exclude
    private ProductPrice productPrice;
    @OneToOne
    @JoinColumn(name = "product_information_id")
    @ToString.Exclude
    private ProductInformation productInformation;
    @Version
    private Long version;

    public ParsingError(Long id, String message, ProductPrice productPrice, ProductInformation productInformation) {
        this.id = id;
        this.message = message;
        this.productPrice = productPrice;
        this.productInformation = productInformation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ParsingError that = (ParsingError) o;
        return getId() != null
                && Objects.equals(getId(), that.getId())
                && Objects.equals(getMessage(), that.getMessage())
                && Objects.equals(
                getProductPrice() != null ? getProductPrice().getId() : null,
                that.getProductPrice() != null ? that.getProductPrice().getId() : null)
                && Objects.equals(
                getProductInformation() != null ? getProductInformation().getId() : null,
                that.getProductInformation() != null ? that.getProductInformation().getId() : null)
                && Objects.equals(getVersion(), that.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                getMessage(),
                getProductPrice() != null ? getProductPrice().getId() : null,
                getProductInformation() != null ? getProductInformation().getId() : null,
                getVersion()
        );
    }

    // toString parameters for lombok
    @ToString.Include(name = "productPriceId")
    private Long getProductPriceId() {
        return getProductPrice().getId();
    }

    @ToString.Include(name = "productInformationId")
    private Long getProductInformationId() {
        return getProductInformation().getId();
    }
}
