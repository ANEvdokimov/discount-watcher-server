package an.evdokimov.discount.watcher.server.api.city.maintenance;

import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import an.evdokimov.discount.watcher.server.core.Maintenance;
import an.evdokimov.discount.watcher.server.mapper.city.CityMapper;
import an.evdokimov.discount.watcher.server.service.city.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

@Maintenance
@RequiredArgsConstructor
@Slf4j
public class CityMaintenanceImpl implements CityMaintenance {
    private final CityService cityService;
    private final CityMapper cityMapper;

    @Override
    @NotNull
    public Collection<CityResponse> getAll() {
        return cityService.getAll().stream()
                .map(cityMapper::map)
                .collect(Collectors.toList());
    }
}
