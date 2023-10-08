package an.evdokimov.discount.watcher.server.database.product.model;

import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
    @NotNull
    @ToString.Exclude
    private ProductInformation productInformation;
    @ManyToOne
    @JoinColumn(name = "shop_id")
    @NotNull
    @ToString.Exclude
    private Shop shop;
    @OneToMany(mappedBy = "product")
    @OrderBy("parsingDate DESC")
    @NotNull
    @ToString.Exclude
    @Builder.Default
    private List<ProductPrice> prices = new ArrayList<>();
    @Version
    private Long version;

    public Product(Long id, ProductInformation productInformation, Shop shop, List<ProductPrice> prices) {
        this.id = id;
        this.productInformation = productInformation;
        this.shop = shop;
        this.prices = prices;
    }

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
                && Objects.equals(getShop().getId(), that.getShop().getId())
                && Objects.equals(getVersion(), that.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getProductInformation().getId(), getShop().getId(), getVersion());
    }

    // toString parameters for lombok
    @ToString.Include(name = "productInformationId")
    private Long getProductInformationId() {
        return getProductInformation().getId();
    }

    @ToString.Include(name = "shopId")
    private Long getShopId() {
        return getShop().getId();
    }
}
