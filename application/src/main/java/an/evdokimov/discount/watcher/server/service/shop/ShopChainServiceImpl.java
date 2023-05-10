package an.evdokimov.discount.watcher.server.service.shop;

import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainResponse;
import an.evdokimov.discount.watcher.server.database.shop.model.ShopChain;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopChainRepository;
import an.evdokimov.discount.watcher.server.mapper.shop.ShopChainMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopChainServiceImpl implements ShopChainService {
    private final ShopChainRepository repository;
    private final ShopChainMapper mapper;

    @Override
    public Collection<ShopChainResponse> getShopChains(boolean withShops, Long cityId) {
        log.debug("Getting all commercial networks. withShops: {}, cityId: {}", withShops, cityId);

        List<ShopChain> shopChains;
        if (cityId != null) {
            shopChains = repository.findByCityId(cityId);
        } else {
            shopChains = repository.findAll();
        }

        if (withShops) {
            return mapper.toDtoWithShops(shopChains).stream()
                    .map(response -> (ShopChainResponse) response)
                    .toList();
        } else {
            return mapper.toDto(shopChains);
        }
    }
}
