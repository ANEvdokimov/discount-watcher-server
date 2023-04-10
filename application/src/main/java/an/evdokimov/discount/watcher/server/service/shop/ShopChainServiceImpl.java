package an.evdokimov.discount.watcher.server.service.shop;

import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainResponse;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainWithShopsResponse;
import an.evdokimov.discount.watcher.server.database.shop.model.ShopChain;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopChainRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
public class ShopChainServiceImpl implements ShopChainService {
    private final ShopChainRepository repository;
    private final ModelMapper modelMapper;

    public ShopChainServiceImpl(ShopChainRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Collection<ShopChainResponse> getShopChains(boolean withShops, Long cityId) {
        log.debug("Getting all commercial networks. withShops: {}, cityId: {}", withShops, cityId);

        Iterable<ShopChain> commercialNetworks;
        if (cityId != null) {
            commercialNetworks = repository.findByCityId(cityId);
        } else {
            commercialNetworks = repository.findAll();
        }

        if (withShops) {
            return modelMapper.map(commercialNetworks, new TypeToken<ArrayList<ShopChainWithShopsResponse>>() {
            }.getType());
        } else {
            return modelMapper.map(commercialNetworks, new TypeToken<ArrayList<ShopChainResponse>>() {
            }.getType());
        }
    }
}
