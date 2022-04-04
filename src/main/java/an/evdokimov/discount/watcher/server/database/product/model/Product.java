package an.evdokimov.discount.watcher.server.database.product.model;

import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_information_id")
    private ProductInformation productInformation;
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;
    @OneToMany
    private List<ProductPrice> prices;

    public void addPrice(ProductPrice price) {
        ArrayList<ProductPrice> newPrices = new ArrayList<>(getPrices());
        newPrices.add(price);
        setPrices(newPrices);
    }
}
