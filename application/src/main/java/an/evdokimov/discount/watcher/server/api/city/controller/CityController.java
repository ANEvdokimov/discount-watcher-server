package an.evdokimov.discount.watcher.server.api.city.controller;

import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import an.evdokimov.discount.watcher.server.service.city.CityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/cities")
@Slf4j
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    /**
     * Getting supported cities.
     *
     * @return A list of cities.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<CityResponse> getAllCities() {
        log.debug("getting all cities");

        return cityService.getAll();
    }
}