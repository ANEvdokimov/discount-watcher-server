package an.evdokimov.discount.watcher.server.database.product.model;

import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_price")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductInformation productInformation;
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;
    private BigDecimal price;
    private Double discount;
    private BigDecimal priceWithDiscount;
    private boolean isInStock;
    private String availabilityInformation;
    private LocalDateTime date;

    public Class<? extends ProductResponse> getDtoClass() {
        return ProductResponse.class;
    }
}
