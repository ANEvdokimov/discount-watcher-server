package an.evdokimov.discount.watcher.server.database.shop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "shop_chain")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopChain {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "shop_chain_id_generator"
    )
    @SequenceGenerator(
            name = "shop_chain_id_generator",
            sequenceName = "shop_chain_sequence",
            allocationSize = 1
    )
    private Long id;
    @NotNull
    private String name;
    private String cyrillicName;
    @OneToMany(mappedBy = "shopChain")
    @ToString.Exclude
    private List<Shop> shops;
    @Version
    private Long version;

    public ShopChain(Long id, String name, String cyrillicName, List<Shop> shops) {
        this.id = id;
        this.name = name;
        this.cyrillicName = cyrillicName;
        this.shops = shops;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ShopChain shopChain = (ShopChain) o;
        return Objects.equals(getId(), shopChain.getId())
                && Objects.equals(getName(), shopChain.getName())
                && Objects.equals(getCyrillicName(), shopChain.getCyrillicName())
                && Objects.equals(getShops(), shopChain.getShops())
                && Objects.equals(getVersion(), shopChain.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getCyrillicName(), getShops(), getVersion());
    }
}
