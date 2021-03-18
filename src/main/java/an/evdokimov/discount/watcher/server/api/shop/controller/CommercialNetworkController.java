package an.evdokimov.discount.watcher.server.api.shop.controller;

import an.evdokimov.discount.watcher.server.api.shop.dto.response.CommercialNetworkResponse;
import an.evdokimov.discount.watcher.server.service.shop.CommercialNetworkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/commercial_networks")
@Slf4j
public class CommercialNetworkController {
    private final CommercialNetworkService commercialNetworkService;

    public CommercialNetworkController(CommercialNetworkService commercialNetworkService) {
        this.commercialNetworkService = commercialNetworkService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<CommercialNetworkResponse> getAllCommercialNetworks(
            @Nullable @RequestHeader("With-Shops") boolean withShops,
            @Nullable @RequestHeader("City-Id") Long cityId) {
        log.debug("Getting all commercial networks. withShops: {}, cityId: {}", withShops, cityId);

        return commercialNetworkService.getCommercialNetworks(withShops, cityId);
    }
}
