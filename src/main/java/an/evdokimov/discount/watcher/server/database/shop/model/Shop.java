package an.evdokimov.discount.watcher.server.database.shop.model;

import an.evdokimov.discount.watcher.server.database.city.model.City;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shop")
public class Shop {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "shop_id_generator"
    )
    @SequenceGenerator(
            name = "shop_id_generator",
            sequenceName = "shop_sequence",
            allocationSize = 1
    )
    private Long id;
    @ManyToOne
    @JoinColumn(name = "shop_chain_id")
    private ShopChain shopChain;
    private String name;
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
    private String address;
    private String cookie;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Shop shop = (Shop) o;
        return id != null && Objects.equals(id, shop.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
