package an.evdokimov.discount.watcher.server.api.shop.controller;

import an.evdokimov.discount.watcher.server.api.TestConfig;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.CommercialNetworkResponse;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.CommercialNetworkWithShopsResponse;
import an.evdokimov.discount.watcher.server.database.city.model.City;
import an.evdokimov.discount.watcher.server.database.shop.model.CommercialNetwork;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.security.JwtUtils;
import an.evdokimov.discount.watcher.server.service.shop.CommercialNetworkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CommercialNetworkController.class)
@Import({TestConfig.class})
class CommercialNetworkControllerTest {
    @Value("${header.authentication}")
    private String authHeaderName;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private CommercialNetworkService service;

    private static List<CommercialNetworkResponse> cnResponsesInAllCity;
    private static List<CommercialNetworkResponse> cnResponsesInCity1;
    private static List<CommercialNetworkResponse> cnResponsesInCity17;
    private static List<CommercialNetworkResponse> cnWithShopsResponsesInAllCity;
    private static List<CommercialNetworkResponse> cnWithShopsResponsesInCity1;
    private static List<CommercialNetworkResponse> cnWithShopsResponsesInCity17;

    @BeforeAll
    public static void createCn() {
        City city1 = City.builder().id(1L).build();
        City city17 = City.builder().id(17L).build();

        Shop shop1InCity1 = Shop.builder().id(1L).city(city1).build();
        Shop shop2InCity1 = Shop.builder().id(2L).city(city1).build();
        Shop shop3InCity17 = Shop.builder().id(3L).city(city17).build();
        Shop shop4InCity17 = Shop.builder().id(4L).city(city17).build();

        CommercialNetwork cnInCity1 = CommercialNetwork.builder().id(1L).shops(List.of(shop1InCity1)).build();
        CommercialNetwork cnInCities1And17 =
                CommercialNetwork.builder().id(2L).shops(List.of(shop2InCity1, shop3InCity17)).build();
        CommercialNetwork cnInCity17 = CommercialNetwork.builder().id(3L).shops(List.of(shop4InCity17)).build();

        ModelMapper modelMapper = new ModelMapper();

        cnResponsesInAllCity = modelMapper.map(List.of(cnInCity1, cnInCities1And17, cnInCity17),
                new TypeToken<ArrayList<CommercialNetworkResponse>>() {
                }.getType());
        cnResponsesInCity1 = modelMapper.map(List.of(cnInCity1, cnInCities1And17),
                new TypeToken<ArrayList<CommercialNetworkResponse>>() {
                }.getType());
        cnResponsesInCity17 = modelMapper.map(List.of(cnInCities1And17, cnInCity17),
                new TypeToken<ArrayList<CommercialNetworkResponse>>() {
                }.getType());

        cnWithShopsResponsesInAllCity = modelMapper.map(List.of(cnInCity1, cnInCities1And17, cnInCity17),
                new TypeToken<ArrayList<CommercialNetworkWithShopsResponse>>() {
                }.getType());
        cnWithShopsResponsesInCity1 = modelMapper.map(List.of(cnInCity1, cnInCities1And17),
                new TypeToken<ArrayList<CommercialNetworkWithShopsResponse>>() {
                }.getType());
        cnWithShopsResponsesInCity17 = modelMapper.map(List.of(cnInCities1And17, cnInCity17),
                new TypeToken<ArrayList<CommercialNetworkWithShopsResponse>>() {
                }.getType());
    }

    @BeforeEach
    public void mockRepository() {
        when(service.getCommercialNetworks(false, null)).thenReturn(cnResponsesInAllCity);
        when(service.getCommercialNetworks(false, 1L)).thenReturn(cnResponsesInCity1);
        when(service.getCommercialNetworks(false, 17L)).thenReturn(cnResponsesInCity17);
        when(service.getCommercialNetworks(true, null)).thenReturn(cnWithShopsResponsesInAllCity);
        when(service.getCommercialNetworks(true, 1L)).thenReturn(cnWithShopsResponsesInCity1);
        when(service.getCommercialNetworks(true, 17L)).thenReturn(cnWithShopsResponsesInCity17);
    }

    @Test
    void getAllCommercialNetworks_NoCityId_http200() throws Exception {
        MvcResult result = mvc.perform(get("/api/commercial_networks")
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user")))
                .andReturn();

        Object resultContent = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, CommercialNetworkResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertEquals(cnResponsesInAllCity, resultContent)
        );
    }

    @Test
    void getAllCommercialNetworks_withShopsNoCityId_http200() throws Exception {
        MvcResult result = mvc.perform(get("/api/commercial_networks")
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                .header("With-Shops", "true"))
                .andReturn();

        Object resultContent = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(ArrayList.class, CommercialNetworkWithShopsResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertEquals(cnWithShopsResponsesInAllCity, resultContent)
        );
    }

    @Test
    void getAllCommercialNetworks_withShopsCityId17_http200() throws Exception {
        MvcResult result = mvc.perform(get("/api/commercial_networks")
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                .header("With-Shops", "true")
                .header("City-Id", 17))
                .andReturn();

        Object resultContent = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(ArrayList.class, CommercialNetworkWithShopsResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertEquals(cnWithShopsResponsesInCity17, resultContent)
        );
    }

    @Test
    void getAllCommercialNetworks_wrongToken_http401() throws Exception {
        mvc.perform(get("/api/commercial_networks")
                .header(authHeaderName, "Bearer wrong token")
                .header("With-Shops", "true")
                .header("City-Id", 17))
                .andExpect(status().isUnauthorized());
    }
}