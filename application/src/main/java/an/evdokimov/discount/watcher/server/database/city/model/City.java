package an.evdokimov.discount.watcher.server.database.city.model;

import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Immutable;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "city")
@Immutable
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @NotNull
    private String name;
    @NotNull
    private String cyrillicName;
    @OneToMany(mappedBy = "city")
    @ToString.Exclude
    private List<Shop> shops;
    @Version
    private Long version;

    public City(Long id, String name, String cyrillicName, List<Shop> shops) {
        this.id = id;
        this.name = name;
        this.cyrillicName = cyrillicName;
        this.shops = shops;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        City that = (City) o;
        return getId() != null
                && Objects.equals(getId(), that.getId())
                && Objects.equals(getName(), that.getCyrillicName())
                && Objects.equals(getCyrillicName(), that.getCyrillicName())
                && Objects.equals(getVersion(), that.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getCyrillicName(), getVersion());
    }
}
