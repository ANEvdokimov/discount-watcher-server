package an.evdokimov.discount.watcher.server.service.shop;

import an.evdokimov.discount.watcher.server.database.shop.model.ShopChain;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopChainRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopChainServiceImpl implements ShopChainService {
    private final ShopChainRepository repository;

    @Override
    @NotNull
    public Collection<ShopChain> getAll(@Nullable Long cityId) {
        log.trace("Getting all shop chains in city [cityId={}]", cityId);

        if (cityId != null) {
            return repository.findByCityId(cityId);
        } else {
            return repository.findAll();
        }
    }
}
