package an.evdokimov.discount.watcher.server.service.shop;

import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopServiceImpl implements ShopService {
    private final ShopRepository repository;

    @Override
    @NotNull
    public Shop getById(@NotNull Long id) throws ServerException {
        log.trace("getting shop by id [{}]", id);

        return repository.findById(id)
                .orElseThrow(() -> ServerErrorCode.SHOP_NOT_FOUND.getException("id=%s".formatted(id)));
    }

    @Override
    @NotNull
    public Shop getByCookie(@NotNull String cookie) throws ServerException {
        log.trace("getting shop by cookie [{}]", cookie);

        return repository.findByCookie(cookie)
                .orElseThrow(() -> new ServerException(ServerErrorCode.SHOP_NOT_FOUND));
    }

    @Override
    @NotNull
    public Collection<Shop> getAll() {
        log.trace("getting all shops");

        return repository.findAll();
    }

    @Override
    @NotNull
    public Collection<Shop> getByUser(@NotNull User user) {
        log.trace("getting all shops by user [{}]", user.getLogin());

        return repository.findAllUserShops(user);
    }
}
