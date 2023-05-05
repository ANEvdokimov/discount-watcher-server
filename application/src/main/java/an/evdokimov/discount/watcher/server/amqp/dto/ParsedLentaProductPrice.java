package an.evdokimov.discount.watcher.server.amqp.dto;

import an.evdokimov.discount.watcher.server.amqp.dto.deserializer.ParsedProductPriceDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = ParsedProductPriceDeserializer.class)
public class ParsedLentaProductPrice extends ParsedProductPrice {
    private BigDecimal priceWithCard;
}
