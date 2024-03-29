package an.evdokimov.discount.watcher.server.mapper.product;

import an.evdokimov.discount.watcher.server.amqp.dto.ParsedLentaProductPrice;
import an.evdokimov.discount.watcher.server.amqp.dto.ParsedProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.LentaProductPrice;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Map;
import java.util.function.BiConsumer;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(
        componentModel = SPRING,
        nullValuePropertyMappingStrategy = IGNORE
)
public abstract class ParsedProductPriceMapper {
    private final Map<Class<? extends ProductPrice>, BiConsumer<ParsedProductPrice, ProductPrice>> mappers =
            Map.of(ProductPrice.class, this::updateNotNullFieldsToProductPrice,
                    LentaProductPrice.class, this::castAndUpdateNotNullFieldsToLentaProductPrice);

    public void updateNotNullFields(ParsedProductPrice input, @MappingTarget ProductPrice output) {
        BiConsumer<ParsedProductPrice, ProductPrice> mapper = mappers.entrySet().stream()
                .filter(entry -> Hibernate.getClass(output).equals(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow();

        mapper.accept(input, output);
    }

    @Mapping(target = "output.parsingStatus", constant = "COMPLETE")
    @Mapping(target = "output.product", ignore = true)
    @Mapping(target = "output.creationDate", ignore = true)
    @Mapping(target = "output.priceChange", ignore = true)
    @Mapping(target = "output.version", ignore = true)
    public abstract void updateNotNullFieldsToProductPrice(ParsedProductPrice input,
                                                           @MappingTarget ProductPrice output);

    private void castAndUpdateNotNullFieldsToLentaProductPrice(ParsedProductPrice parsedProductPrice,
                                                               @MappingTarget ProductPrice output) {
        updateNotNullFieldsToLentaProductPrice(
                (ParsedLentaProductPrice) parsedProductPrice,
                (LentaProductPrice) output
        );
    }

    @Mapping(target = "output.parsingStatus", constant = "COMPLETE")
    @Mapping(target = "output.product", ignore = true)
    @Mapping(target = "output.creationDate", ignore = true)
    @Mapping(target = "output.priceChange", ignore = true)
    @Mapping(target = "output.version", ignore = true)
    public abstract void updateNotNullFieldsToLentaProductPrice(ParsedLentaProductPrice input,
                                                                @MappingTarget LentaProductPrice output);
}
