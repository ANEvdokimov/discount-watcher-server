package an.evdokimov.discount.watcher.server.service.shop;

import an.evdokimov.discount.watcher.server.api.shop.dto.response.CommercialNetworkResponse;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.CommercialNetworkWithShopsResponse;
import an.evdokimov.discount.watcher.server.database.shop.model.CommercialNetwork;
import an.evdokimov.discount.watcher.server.database.shop.repository.CommercialNetworkRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
public class CommercialNetworkService {
    private final CommercialNetworkRepository repository;
    private final ModelMapper modelMapper;

    public CommercialNetworkService(CommercialNetworkRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    public Collection<CommercialNetworkResponse> getCommercialNetworks(boolean withShops, Long cityId) {
        log.debug("Getting all commercial networks. withShops: {}, cityId: {}", withShops, cityId);

        Iterable<CommercialNetwork> commercialNetworks;
        if (cityId != null) {
            commercialNetworks = repository.findByCityId(cityId);
        } else {
            commercialNetworks = repository.findAll();
        }

        if (withShops) {
            return modelMapper.map(commercialNetworks, new TypeToken<ArrayList<CommercialNetworkWithShopsResponse>>() {
            }.getType());
        } else {
            return modelMapper.map(commercialNetworks, new TypeToken<ArrayList<CommercialNetworkResponse>>() {
            }.getType());
        }
    }
}
