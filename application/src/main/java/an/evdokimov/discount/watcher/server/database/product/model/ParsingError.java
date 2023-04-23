package an.evdokimov.discount.watcher.server.database.product.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(name = "parsing_error")
@Getter
@Setter
@RequiredArgsConstructor
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
    private ProductPrice productPrice;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_information_id")
    private ProductInformation productInformation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ParsingError that = (ParsingError) o;
        return getId() != null
                && Objects.equals(getId(), that.getId())
                && Objects.equals(getMessage(), that.getMessage())
                && Objects.equals(getProductPrice().getId(), that.getProductPrice().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getMessage(), getProductPrice().getId());
    }

    @Override
    public String toString() {
        return "ParsingError{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", productPriceId=" + productPrice.getId() +
                ", productInformationId=" + productInformation.getId() +
                '}';
    }
}
