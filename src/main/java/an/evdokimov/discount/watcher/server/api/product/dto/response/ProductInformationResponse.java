package an.evdokimov.discount.watcher.server.api.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInformationResponse {
    private Long id;
    private String name;
    private URL lentaUrl;
}
