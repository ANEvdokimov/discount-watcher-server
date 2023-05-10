package an.evdokimov.discount.watcher.server.service.city;

import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import an.evdokimov.discount.watcher.server.database.city.repository.CityRepository;
import an.evdokimov.discount.watcher.server.mapper.city.CityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {
    private final CityRepository repository;
    private final CityMapper mapper;

    @Override
    public Collection<CityResponse> getAll() {
        log.debug("getting all cities");
        return mapper.toDto(repository.findAll());
    }
}
