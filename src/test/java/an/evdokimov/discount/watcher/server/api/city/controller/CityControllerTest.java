package an.evdokimov.discount.watcher.server.api.city.controller;

import an.evdokimov.discount.watcher.server.api.TestConfig;
import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import an.evdokimov.discount.watcher.server.database.city.model.City;
import an.evdokimov.discount.watcher.server.security.JwtUtils;
import an.evdokimov.discount.watcher.server.service.city.CityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CityController.class)
@Import({TestConfig.class})
class CityControllerTest {
    @Value("${header.authentication}")
    private String authHeaderName;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private CityService cityService;

    @Test
    void getAllCities_validJwt_http200() throws Exception {
        List<City> cities = List.of(
                City.builder().name("city 1").build(),
                City.builder().name("city 2").build(),
                City.builder().name("city 17").build());
        when(cityService.getAll()).thenReturn(cities);

        MvcResult result = mvc.perform(get("/api/cities")
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user")))
                .andReturn();

        ArrayList<CityResponse> cityResponses = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, CityResponse.class)
        );

        ArrayList<CityResponse> expectedCityResponses =
                modelMapper.map(cities, new TypeToken<ArrayList<CityResponse>>() {
                }.getType());

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> verify(cityService, times(1)).getAll(),
                () -> assertThat(cityResponses, containsInAnyOrder(expectedCityResponses.toArray()))
        );
    }

    @Test
    void getAllCities_invalidJwt_http200() throws Exception {
        mvc.perform(get("/api/cities")
                .header(authHeaderName, "Bearer wrong_token"))
                .andExpect(status().isUnauthorized());
    }
}