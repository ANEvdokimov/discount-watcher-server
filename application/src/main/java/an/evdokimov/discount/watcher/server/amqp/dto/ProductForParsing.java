package an.evdokimov.discount.watcher.server.amqp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductForParsing {
    private Long productInformationId;
    private Long productPriceId;
    private URL url;
    private String cookie;
}
