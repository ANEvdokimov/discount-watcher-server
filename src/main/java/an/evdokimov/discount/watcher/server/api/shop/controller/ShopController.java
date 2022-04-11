package an.evdokimov.discount.watcher.server.api.shop.controller;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopResponse;
import an.evdokimov.discount.watcher.server.service.shop.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("api/")
@Slf4j
@RequiredArgsConstructor
public class ShopController {
    private final ShopService service;

    /**
     * Getting all supported shops.
     *
     * @return list of shops.
     */
    @GetMapping(value = "shops", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<ShopResponse> getAllShops() {
        log.info("getting all shops.");
        return service.getAllShops();
    }

    /**
     * Getting a shop by a shop id.
     */
    @GetMapping(value = "shop/{shopId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ShopResponse getShopById(@PathVariable("shopId") Long shopId) throws ServerException {
        log.info("getting shop by id={}", shopId);
        return service.getShopById(shopId);
    }
}
