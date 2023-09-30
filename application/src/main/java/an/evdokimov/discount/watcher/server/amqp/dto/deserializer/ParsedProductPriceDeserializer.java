package an.evdokimov.discount.watcher.server.amqp.dto.deserializer;

import an.evdokimov.discount.watcher.server.amqp.dto.ParsedLentaProductPrice;
import an.evdokimov.discount.watcher.server.amqp.dto.ParsedProductPrice;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;

public class ParsedProductPriceDeserializer extends StdDeserializer<ParsedProductPrice> {

    protected ParsedProductPriceDeserializer() {
        super(ParsedProductPrice.class);
    }

    @Override
    public ParsedProductPrice deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        JsonNode jsonNode = deserializationContext.readTree(jsonParser);

        if (jsonNode.has("priceWithCard")) {
            return ParsedLentaProductPrice.builder()
                    .id(jsonNode.get("id").longValue())
                    .price(jsonNode.get("price").isNull()
                            ? null
                            : jsonNode.get("price").decimalValue())
                    .discount(jsonNode.get("discount").isNull()
                            ? null
                            : jsonNode.get("discount").asDouble())
                    .priceWithDiscount(jsonNode.get("priceWithDiscount").isNull()
                            ? null :
                            jsonNode.get("priceWithDiscount").decimalValue())
                    .priceWithCard(jsonNode.get("priceWithCard").isNull()
                            ? null
                            : jsonNode.get("priceWithCard").decimalValue())
                    .isInStock(jsonNode.get("isInStock").booleanValue())
                    .availabilityInformation(jsonNode.get("availabilityInformation").textValue())
                    .date(LocalDateTime.parse(jsonNode.get("date").textValue()))
                    .build();
        } else {
            return ParsedProductPrice.builder()
                    .id(jsonNode.get("id").asLong())
                    .price(jsonNode.get("price").isNull()
                            ? null
                            : jsonNode.get("price").decimalValue())
                    .discount(jsonNode.get("discount").isNull()
                            ? null
                            : jsonNode.get("discount").asDouble())
                    .priceWithDiscount(jsonNode.get("priceWithDiscount").isNull()
                            ? null :
                            jsonNode.get("priceWithDiscount").decimalValue())
                    .isInStock(jsonNode.get("isInStock").booleanValue())
                    .availabilityInformation(jsonNode.get("availabilityInformation").textValue())
                    .date(LocalDateTime.parse(jsonNode.get("date").textValue()))
                    .build();
        }
    }
}
