package an.evdokimov.discount.watcher.server.service.city;

import an.evdokimov.discount.watcher.server.database.city.model.City;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface CityService {
    @NotNull
    Collection<City> getAll();
}
