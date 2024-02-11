package an.evdokimov.discount.watcher.server.mapper.shop;

import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainResponse;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainWithShopsResponse;
import an.evdokimov.discount.watcher.server.database.shop.model.ShopChain;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ShopChainMapper {
    @Named("without_shops")
    ShopChainResponse toDto(ShopChain shopChain);

    @Named("with_shops")
    ShopChainWithShopsResponse toDtoWithShops(ShopChain shopChain);
}
