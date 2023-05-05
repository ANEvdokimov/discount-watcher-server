package an.evdokimov.discount.watcher.server.mapper.product;

import an.evdokimov.discount.watcher.server.database.product.model.ParsingError;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import an.evdokimov.discount.watcher.server.database.product.model.ProductPrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ParsingErrorMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    ParsingError map(String message, ProductInformation productInformation, ProductPrice productPrice);
}
