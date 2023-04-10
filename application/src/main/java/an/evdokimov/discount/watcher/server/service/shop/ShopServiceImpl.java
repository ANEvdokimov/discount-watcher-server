package an.evdokimov.discount.watcher.server.service.shop;

import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopResponse;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.mapper.shop.ShopMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopServiceImpl implements ShopService {
    private final ShopRepository repository;
    private final ShopMapper mapper;

    @Override
    public ShopResponse getShopById(Long id) throws ServerException {
        log.debug("getting shop by id={}", id);
        return mapper.map(repository.findById(id)
                .orElseThrow(() -> new ServerException(ServerErrorCode.SHOP_NOT_FOUND)));
    }

    @Override
    public Collection<ShopResponse> getAllShops() {
        log.debug("getting all shops");
        return repository.findAll().stream().map(mapper::map).toList();
    }

    @Override
    public Collection<ShopResponse> getAllUserShops(User user) {
        log.debug("getting all shops by user: {}", user.getLogin());
        return repository.findAllUserShops(user).stream().map(mapper::map).toList();
    }
}
