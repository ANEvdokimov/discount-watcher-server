package an.evdokimov.discount.watcher.server.api.city.controller;

import an.evdokimov.discount.watcher.server.api.TestConfig;
import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import an.evdokimov.discount.watcher.server.api.city.maintenance.CityMaintenance;
import an.evdokimov.discount.watcher.server.configuration.SecurityConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CityController.class)
@Import({TestConfig.class, SecurityConfiguration.class})
class CityControllerTest {
    private static final String AUTH_HEADER_NAME = "Authenticated-User";
    private static final String AUTH_USER = "test_user";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CityMaintenance cityMaintenance;

    @Test
    void getAllCities_validJwt_http200() throws Exception {
        List<CityResponse> cities = List.of(
                CityResponse.builder().name("city 1").build(),
                CityResponse.builder().name("city 2").build(),
                CityResponse.builder().name("city 17").build());
        when(cityMaintenance.getAll()).thenReturn(cities);

        MvcResult result = mvc.perform(get("/api/cities")
                        .header(AUTH_HEADER_NAME, AUTH_USER))
                .andReturn();

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> verify(cityMaintenance, times(1)).getAll(),
                () -> assertThat(
                        objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, CityResponse.class)
                        ),
                        containsInAnyOrder(cities.toArray()))
        );
    }

    @Test
    void getAllCities_invalidJwt_http401() throws Exception {
        mvc.perform(get("/api/cities"))
                .andExpect(status().isUnauthorized());
    }
}