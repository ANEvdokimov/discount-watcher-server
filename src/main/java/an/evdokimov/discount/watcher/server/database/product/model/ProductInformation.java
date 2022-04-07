package an.evdokimov.discount.watcher.server.database.product.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.net.URL;
import java.util.Objects;

@Entity
@Table(name = "product_information")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private URL url;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductInformation that = (ProductInformation) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
