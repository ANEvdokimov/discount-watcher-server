package an.evdokimov.discount.watcher.server.database.product.model;

import an.evdokimov.discount.watcher.server.database.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "user_product")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private boolean monitor_discount;
    private boolean monitor_availability;
    private boolean monitor_price_changes;
}
