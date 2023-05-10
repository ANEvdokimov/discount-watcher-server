package an.evdokimov.discount.watcher.server.service.city;

import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import an.evdokimov.discount.watcher.server.database.city.model.City;
import an.evdokimov.discount.watcher.server.database.city.repository.CityRepository;
import an.evdokimov.discount.watcher.server.mapper.city.CityMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CityServiceImpl.class)
class CityServiceTest {
    @MockBean
    private CityRepository repository;
    @MockBean
    private CityMapper mapper;

    @Autowired
    private CityServiceImpl testedCityService;

    @Test
    void getAll_3Cities_listOfCityResponse() {
        List<City> cities = List.of(
                City.builder().name("city 1").build(),
                City.builder().name("city 2").build(),
                City.builder().name("city 17").build());
        when(repository.findAll()).thenReturn(cities);

        List<CityResponse> expectedCityResponses = List.of(
                CityResponse.builder().name("city 1").build(),
                CityResponse.builder().name("city 2").build(),
                CityResponse.builder().name("city 17").build()
        );

        when(mapper.toDto(cities)).thenReturn(expectedCityResponses);


        Collection<CityResponse> result = testedCityService.getAll();

        assertEquals(expectedCityResponses, result);
    }

    @Test
    void getAll_noCities_emptyList() {
        when(repository.findAll()).thenReturn(new ArrayList<>());
        when(mapper.toDto(new ArrayList<>())).thenReturn(new ArrayList<>());

        Collection<CityResponse> result = testedCityService.getAll();

        assertEquals(0, result.size());
    }
}