package an.evdokimov.discount.watcher.server.api.shop.controller;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopResponse;
import an.evdokimov.discount.watcher.server.api.shop.maintenance.ShopMaintenance;
import an.evdokimov.discount.watcher.server.security.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("api/")
@Slf4j
@RequiredArgsConstructor
public class ShopController {
    private final ShopMaintenance maintenance;

    /**
     * Getting all supported shops.
     *
     * @param onlyCurrentUser if true - return only current user shops
     * @return list of shops.
     */
    @GetMapping(value = "shops", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<ShopResponse> getAllShops(Authentication authentication,
                                                @Nullable @RequestHeader("only-my") Boolean onlyCurrentUser) {
        if (onlyCurrentUser == null || !onlyCurrentUser) {
            log.info("getting all shops.");
            return maintenance.getAll();
        } else {
            User currentUser = (User) authentication.getPrincipal();
            log.info("getting shop by user={}", currentUser.getLogin());
            return maintenance.getAllByUser(currentUser);
        }
    }

    /**
     * Getting a shop by a shop id.
     */
    @GetMapping(value = "shop/{shopId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ShopResponse getShopById(@PathVariable("shopId") Long shopId) throws ServerException {
        log.info("getting shop by id={}", shopId);
        return maintenance.getById(shopId);
    }
}
