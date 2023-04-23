package an.evdokimov.discount.watcher.server.amqp.dto;

import an.evdokimov.discount.watcher.server.amqp.dto.deserializer.ParsedProductPriceDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = ParsedProductPriceDeserializer.class)
public class ParsedProductPrice {
    private Long id;
    private BigDecimal price;
    private Double discount;
    private BigDecimal priceWithDiscount;
    private Boolean isInStock;
    private String availabilityInformation;
    private LocalDateTime date;
}
