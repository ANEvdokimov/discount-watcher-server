package an.evdokimov.discount.watcher.server.database.product.model;

import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "product")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_id_generator"
    )
    @SequenceGenerator(
            name = "product_id_generator",
            sequenceName = "product_sequence",
            allocationSize = 1
    )
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_information_id")
    private ProductInformation productInformation;
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;
    @OneToMany(mappedBy = "product")
    @OrderBy("date DESC")
    @ToString.Exclude
    @Builder.Default
    private List<ProductPrice> prices = new ArrayList<>();

    public void addPrice(ProductPrice price) {
        prices.add(price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Product that = (Product) o;
        return id != null
                && Objects.equals(getId(), that.getId())
                && Objects.equals(getProductInformation().getId(), that.getProductInformation().getId())
                && Objects.equals(getShop().getId(), that.getShop().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getProductInformation().getId(), getShop().getId());
    }
}
