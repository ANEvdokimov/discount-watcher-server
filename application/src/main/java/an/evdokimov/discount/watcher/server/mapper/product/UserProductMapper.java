package an.evdokimov.discount.watcher.server.mapper.product;

import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductWithCookiesRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.request.UserProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.UserProductResponse;
import an.evdokimov.discount.watcher.server.database.product.model.Product;
import an.evdokimov.discount.watcher.server.database.product.model.UserProduct;
import an.evdokimov.discount.watcher.server.database.product.repository.ProductRepository;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING,
        uses = {
                ProductMapper.class,
                ProductRepository.class
        })
public abstract class UserProductMapper {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductRepository productRepository;

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "version", ignore = true)
    public abstract UserProduct map(NewProductRequest request, User user, Product product);

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "version", ignore = true)
    public abstract UserProduct map(NewProductWithCookiesRequest request, User user, Product product);

    public UserProductResponse map(UserProduct userProduct) {
        if (userProduct == null) {
            return null;
        }

        return UserProductResponse.builder()
                .id(userProduct.getId())
                .product(productMapper.map(userProduct.getProduct()))
                .monitorDiscount(userProduct.isMonitorDiscount())
                .monitorAvailability(userProduct.isMonitorAvailability())
                .monitorPriceChanges(userProduct.isMonitorPriceChanges())
                .build();
    }

    public UserProduct map(UserProductRequest userProduct) {
        if (userProduct == null) {
            return null;
        }

        return UserProduct.builder()
                .id(userProduct.getId())
                .product(productRepository.getReferenceById(userProduct.getProductId()))
                .monitorDiscount(userProduct.isMonitorDiscount())
                .monitorAvailability(userProduct.isMonitorAvailability())
                .monitorPriceChanges(userProduct.isMonitorPriceChanges())
                .build();
    }
}
