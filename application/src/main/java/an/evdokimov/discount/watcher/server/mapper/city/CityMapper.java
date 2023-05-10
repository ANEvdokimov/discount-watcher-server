package an.evdokimov.discount.watcher.server.mapper.city;

import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import an.evdokimov.discount.watcher.server.database.city.model.City;
import org.mapstruct.Mapper;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface CityMapper {
    CityResponse toDto(City city);

    List<CityResponse> toDto(List<City> cities);
}
