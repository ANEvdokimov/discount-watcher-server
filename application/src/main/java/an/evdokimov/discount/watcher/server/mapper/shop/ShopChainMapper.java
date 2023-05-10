package an.evdokimov.discount.watcher.server.mapper.shop;

import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainResponse;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainWithShopsResponse;
import an.evdokimov.discount.watcher.server.database.shop.model.ShopChain;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ShopChainMapper {
    @Named("without_shops")
    ShopChainResponse toDto(ShopChain shopChain);

    @IterableMapping(qualifiedByName = "without_shops")
    List<ShopChainResponse> toDto(List<ShopChain> shopChains);

    @Named("with_shops")
    ShopChainWithShopsResponse toDtoWithShops(ShopChain shopChain);

    @IterableMapping(qualifiedByName = "with_shops")
    List<ShopChainWithShopsResponse> toDtoWithShops(List<ShopChain> shopChains);
}
