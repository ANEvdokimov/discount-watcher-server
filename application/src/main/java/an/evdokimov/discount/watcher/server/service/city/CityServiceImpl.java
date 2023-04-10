package an.evdokimov.discount.watcher.server.service.city;

import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import an.evdokimov.discount.watcher.server.database.city.repository.CityRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
public class CityServiceImpl implements CityService {
    private final CityRepository repository;
    private final ModelMapper modelMapper;

    public CityServiceImpl(CityRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Collection<CityResponse> getAll() {
        log.debug("getting all cities");

        return modelMapper.map(repository.findAll(), new TypeToken<ArrayList<CityResponse>>() {
        }.getType());
    }
}
