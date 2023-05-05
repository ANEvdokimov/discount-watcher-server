package an.evdokimov.discount.watcher.server.database.product.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicUpdate;

import java.net.URL;
import java.util.Objects;

@Entity
@Table(name = "product_information")
@DynamicUpdate
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInformation {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_information_id_generator"
    )
    @SequenceGenerator(
            name = "product_information_id_generator",
            sequenceName = "product_information_sequence",
            allocationSize = 1
    )
    private Long id;
    private String name;
    @NotNull
    private URL url;
    @Enumerated(EnumType.STRING)
    @NotNull
    private ParsingStatus parsingStatus;
    @Version
    private Long version;

    public ProductInformation(Long id, String name, URL url, ParsingStatus parsingStatus) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.parsingStatus = parsingStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductInformation that = (ProductInformation) o;
        return id != null
                && Objects.equals(getId(), that.getId())
                && Objects.equals(getName(), that.getName())
                && Objects.equals(getUrl(), that.getUrl())
                && Objects.equals(getParsingStatus(), that.getParsingStatus())
                && Objects.equals(getVersion(), that.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getUrl(), getParsingStatus(), getVersion());
    }
}
