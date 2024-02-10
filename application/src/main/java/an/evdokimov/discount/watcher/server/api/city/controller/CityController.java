package an.evdokimov.discount.watcher.server.api.city.controller;

import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import an.evdokimov.discount.watcher.server.api.city.maintenance.CityMaintenance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/cities")
@Slf4j
@RequiredArgsConstructor
public class CityController {
    private final CityMaintenance cityMaintenance;

    /**
     * Getting supported cities.
     *
     * @return A list of cities.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<CityResponse> getAllCities() {
        log.debug("getting all cities");

        return cityMaintenance.getAll();
    }
}
