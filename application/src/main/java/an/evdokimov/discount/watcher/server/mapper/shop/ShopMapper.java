package an.evdokimov.discount.watcher.server.mapper.shop;

import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopResponse;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ShopMapper {
    ShopResponse map(Shop shop);
}
