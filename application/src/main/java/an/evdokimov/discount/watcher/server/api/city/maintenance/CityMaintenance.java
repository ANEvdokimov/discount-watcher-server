package an.evdokimov.discount.watcher.server.api.city.maintenance;

import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface CityMaintenance {
    @NotNull
    Collection<CityResponse> getAll();
}
