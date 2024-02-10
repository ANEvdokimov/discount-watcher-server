package an.evdokimov.discount.watcher.server.service.city;

import an.evdokimov.discount.watcher.server.database.city.model.City;
import an.evdokimov.discount.watcher.server.database.city.repository.CityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CityServiceImpl.class)
class CityServiceTest {
    @MockBean
    private CityRepository repository;

    @Autowired
    private CityServiceImpl testedCityService;

    @Test
    void getAll_3Cities_listOfCityResponse() {
        List<City> expectedCities = List.of(
                City.builder().name("city 1").build(),
                City.builder().name("city 2").build(),
                City.builder().name("city 17").build());
        when(repository.findAll()).thenReturn(expectedCities);

        Collection<City> result = testedCityService.getAll();

        assertEquals(expectedCities, result);
    }

    @Test
    void getAll_noCities_emptyList() {
        when(repository.findAll()).thenReturn(new ArrayList<>());

        Collection<City> result = testedCityService.getAll();

        assertTrue(result.isEmpty());
    }
}