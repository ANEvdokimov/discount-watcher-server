package an.evdokimov.discount.watcher.server.mapper.product;

import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductWithCookiesRequest;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProductMapper {
    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "version", ignore = true)
    UserProduct map(NewProductRequest request, User user, Product product);

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "version", ignore = true)
    UserProduct map(NewProductWithCookiesRequest request, User user, Product product);
}
