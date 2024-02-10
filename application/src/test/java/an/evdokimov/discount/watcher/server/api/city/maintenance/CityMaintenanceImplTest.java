package an.evdokimov.discount.watcher.server.api.city.maintenance;

import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import an.evdokimov.discount.watcher.server.database.city.model.City;
import an.evdokimov.discount.watcher.server.mapper.city.CityMapper;
import an.evdokimov.discount.watcher.server.service.city.CityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CityMaintenanceImpl.class)
class CityMaintenanceImplTest {
    @MockBean
    private CityService cityService;
    @MockBean
    private CityMapper mapper;

    @Autowired
    private CityMaintenanceImpl testedCityMaintenance;

    @Test
    @DisplayName("get all 3 cities")
    void getAll_3Cities_listOfCityResponse() {
        List<City> cities = List.of(
                City.builder().name("city 1").build(),
                City.builder().name("city 2").build(),
                City.builder().name("city 17").build());
        when(cityService.getAll()).thenReturn(cities);

        List<CityResponse> expectedCityResponses = List.of(
                CityResponse.builder().name("city 1").build(),
                CityResponse.builder().name("city 2").build(),
                CityResponse.builder().name("city 17").build()
        );

        for (int i = 0; i < cities.size(); i++) {
            when(mapper.map(cities.get(i))).thenReturn(expectedCityResponses.get(i));
        }

        Collection<CityResponse> result = testedCityMaintenance.getAll();

        assertEquals(expectedCityResponses, result);
    }

    @Test
    @DisplayName("get all 0 cities")
    void getAll_noCities_emptyList() {
        when(cityService.getAll()).thenReturn(new ArrayList<>());

        Collection<CityResponse> result = testedCityMaintenance.getAll();

        assertEquals(0, result.size());
    }
}