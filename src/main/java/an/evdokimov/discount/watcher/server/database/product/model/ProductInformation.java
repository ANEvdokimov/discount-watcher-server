package an.evdokimov.discount.watcher.server.database.product.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.net.URL;

@Entity
@Table(name = "product_information")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private URL url;
}
