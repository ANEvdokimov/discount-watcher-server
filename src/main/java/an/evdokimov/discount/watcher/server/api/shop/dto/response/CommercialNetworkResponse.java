package an.evdokimov.discount.watcher.server.api.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommercialNetworkResponse {
    private Long id;
    private String name;
    private String cyrillicName;
}
