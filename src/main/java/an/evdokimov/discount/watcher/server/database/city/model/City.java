package an.evdokimov.discount.watcher.server.database.city.model;

import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "city")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String cyrillicName;
    @OneToMany(mappedBy = "city")
    private List<Shop> shops;
}
