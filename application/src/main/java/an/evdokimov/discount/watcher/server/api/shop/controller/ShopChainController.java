package an.evdokimov.discount.watcher.server.api.shop.controller;

import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainResponse;
import an.evdokimov.discount.watcher.server.service.shop.ShopChainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/shop_chains")
@Slf4j
public class ShopChainController {
    private final ShopChainService shopChainService;

    public ShopChainController(ShopChainService shopChainService) {
        this.shopChainService = shopChainService;
    }

    /**
     * Getting all supported shop chains.
     *
     * @param withShops flag - return a shop chain with all connected shops [true].
     * @param cityId    return all shop chains in the city.
     * @return a list of shop chains.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<ShopChainResponse> getAllCommercialNetworks(
            @Nullable @RequestHeader("With-Shops") boolean withShops,
            @Nullable @RequestHeader("City-Id") Long cityId) {
        log.debug("Getting all commercial networks. withShops: {}, cityId: {}", withShops, cityId);

        return shopChainService.getShopChains(withShops, cityId);
    }
}
