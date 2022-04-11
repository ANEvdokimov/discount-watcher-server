package an.evdokimov.discount.watcher.server.service.city;

import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;

import java.util.Collection;

public interface CityService {
    Collection<CityResponse> getAll();
}
