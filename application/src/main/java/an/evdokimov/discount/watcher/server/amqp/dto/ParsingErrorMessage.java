package an.evdokimov.discount.watcher.server.amqp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParsingErrorMessage {
    private Long productPriceId;
    private Long productInformationId;
    private String message;
}
