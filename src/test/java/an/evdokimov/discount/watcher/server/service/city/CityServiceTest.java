package an.evdokimov.discount.watcher.server.service.city;

import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import an.evdokimov.discount.watcher.server.database.city.model.City;
import an.evdokimov.discount.watcher.server.database.city.repository.CityRepository;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class CityServiceTest {
    @Autowired
    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    private CityService cityService;

    @MockBean
    private CityRepository repository;

    @Test
    void getAll_3Cities_listOfCityResponse() {
        List<City> cities = List.of(
                City.builder().name("city 1").build(),
                City.builder().name("city 2").build(),
                City.builder().name("city 17").build());
        when(repository.findAll()).thenReturn(cities);

        ArrayList<CityResponse> expectedCityResponses =
                mapper.map(cities, new TypeToken<ArrayList<CityResponse>>() {
                }.getType());

        Collection<City> result = cityService.getAll();

        assertThat(result, containsInAnyOrder(expectedCityResponses.toArray()));
    }

    @Test
    void getAll_noCities_emptyList() {
        when(repository.findAll()).thenReturn(new ArrayList<>());

        Collection<City> result = cityService.getAll();

        assertEquals(0, result.size());
    }
}