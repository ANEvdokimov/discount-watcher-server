package an.evdokimov.discount.watcher.server.mapper.product;

import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductInformationResponse;
import an.evdokimov.discount.watcher.server.database.product.model.ProductInformation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductInformationMapper {
    ProductInformationResponse map(ProductInformation productInformation);
}
