package an.evdokimov.discount.watcher.server.database.city.model;

import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "city")
public class City {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "city_id_generator"
    )
    @SequenceGenerator(
            name = "city_id_generator",
            sequenceName = "city_sequence",
            allocationSize = 1
    )
    private Long id;
    private String name;
    private String cyrillicName;
    @OneToMany(mappedBy = "city")
    @ToString.Exclude
    private List<Shop> shops;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        City city = (City) o;
        return id != null && Objects.equals(id, city.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
