package an.evdokimov.discount.watcher.server.amqp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedProductInformation {
    private Long id;
    private String name;
    private ParsedProductPrice productPrice;
}
