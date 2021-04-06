package an.evdokimov.discount.watcher.server.database.shop.model;

import an.evdokimov.discount.watcher.server.database.city.model.City;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shop")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "commercial_network_id")
    private CommercialNetwork commercialNetwork;
    private String name;
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
    private String address;
    private String cookie;
}
