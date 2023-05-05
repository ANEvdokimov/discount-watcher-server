package an.evdokimov.discount.watcher.server.database.shop.model;

import an.evdokimov.discount.watcher.server.database.city.model.City;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(name = "shop")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @ToString.Exclude
    @NotNull
    private ShopChain shopChain;
    @NotNull
    private String name;
    private String cyrillicName;
    @ManyToOne
    @JoinColumn(name = "city_id")
    @ToString.Exclude
    @NotNull
    private City city;
    @NotNull
    private String address;
    private String cookie;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Shop shop = (Shop) o;
        return Objects.equals(getId(), shop.getId())
                && Objects.equals(getShopChain().getId(), shop.getShopChain().getId())
                && Objects.equals(getName(), shop.getName())
                && Objects.equals(getCyrillicName(), shop.getCyrillicName())
                && Objects.equals(getCity().getId(), shop.getCity().getId())
                && Objects.equals(getAddress(), shop.getAddress())
                && Objects.equals(getCookie(), shop.getCookie());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getShopChain().getId(), getName(), getCyrillicName(), getCity().getId(),
                getAddress(), getCookie());
    }

    // toString parameters for lombok
    @ToString.Include(name = "ShopChainId")
    private Long getShopChainId() {
        return getShopChain().getId();
    }

    @ToString.Include(name = "CityId")
    private Long getCityId() {
        return getCity().getId();
    }
}
