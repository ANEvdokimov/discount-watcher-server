package an.evdokimov.discount.watcher.server.database.product.model;

import an.evdokimov.discount.watcher.server.security.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
import org.hibernate.annotations.DynamicUpdate;

import java.util.Objects;

@Entity
@Table(name = "user_product")
@DynamicUpdate
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProduct {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_product_id_generator"
    )
    @SequenceGenerator(
            name = "user_product_id_generator",
            sequenceName = "user_product_sequence",
            allocationSize = 1
    )
    private Long id;
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_login")
    @ToString.Exclude
    private User user;
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    @ToString.Exclude
    private Product product;
    private boolean monitorDiscount;
    private boolean monitorAvailability;
    private boolean monitorPriceChanges;
    @Version
    private Long version;

    public UserProduct(Long id, User user, Product product, boolean monitorDiscount, boolean monitorAvailability,
                       boolean monitorPriceChanges) {
        this.id = id;
        this.user = user;
        this.product = product;
        this.monitorDiscount = monitorDiscount;
        this.monitorAvailability = monitorAvailability;
        this.monitorPriceChanges = monitorPriceChanges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserProduct that = (UserProduct) o;
        return isMonitorDiscount() == that.isMonitorDiscount()
                && isMonitorAvailability() == that.isMonitorAvailability()
                && isMonitorPriceChanges() == that.isMonitorPriceChanges()
                && Objects.equals(getId(), that.getId())
                && Objects.equals(getUser().getLogin(), that.getUser().getLogin())
                && Objects.equals(getProduct().getId(), that.getProduct().getId())
                && Objects.equals(getVersion(), that.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUser().getLogin(), getProduct().getId(), isMonitorDiscount(),
                isMonitorAvailability(), isMonitorPriceChanges(), getVersion());
    }

    // toString parameters for lombok
    @ToString.Include(name = "UserId")
    private String getUserLogin() {
        return getUser().getLogin();
    }

    @ToString.Include(name = "ProductId")
    private Long getProductId() {
        return getProduct().getId();
    }
}
