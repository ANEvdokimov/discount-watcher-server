package an.evdokimov.discount.watcher.server.service.shop;

import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopResponse;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.mapper.shop.ShopMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {
    private final ShopRepository repository;
    private final ShopMapper mapper;

    public ShopResponse getShopById(Long id) throws ServerException {
        log.debug("getting shop by id={}", id);
        return mapper.map(repository.findById(id)
                .orElseThrow(() -> new ServerException(ServerErrorCode.SHOP_NOT_FOUND)));
    }

    public Collection<ShopResponse> getAllShops() {
        log.debug("getting all shops");
        return repository.findAll().stream().map(mapper::map).toList();
    }
}
