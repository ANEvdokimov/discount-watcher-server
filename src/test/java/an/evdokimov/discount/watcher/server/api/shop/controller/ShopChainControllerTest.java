package an.evdokimov.discount.watcher.server.api.shop.controller;

import an.evdokimov.discount.watcher.server.api.TestConfig;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainResponse;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainWithShopsResponse;
import an.evdokimov.discount.watcher.server.configuration.SecurityConfiguration;
import an.evdokimov.discount.watcher.server.database.city.model.City;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.model.ShopChain;
import an.evdokimov.discount.watcher.server.security.JwtUtils;
import an.evdokimov.discount.watcher.server.service.shop.ShopChainServiceImpl;
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
@WebMvcTest(ShopChainController.class)
@Import({TestConfig.class, SecurityConfiguration.class})
class ShopChainControllerTest {
    @Value("${header.authentication}")
    private String authHeaderName;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private ShopChainServiceImpl service;

    private static List<ShopChainResponse> cnResponsesInAllCity;
    private static List<ShopChainResponse> cnResponsesInCity1;
    private static List<ShopChainResponse> cnResponsesInCity17;
    private static List<ShopChainResponse> cnWithShopsResponsesInAllCity;
    private static List<ShopChainResponse> cnWithShopsResponsesInCity1;
    private static List<ShopChainResponse> cnWithShopsResponsesInCity17;

    @BeforeAll
    public static void createCn() {
        City city1 = City.builder().id(1L).build();
        City city17 = City.builder().id(17L).build();

        Shop shop1InCity1 = Shop.builder().id(1L).city(city1).build();
        Shop shop2InCity1 = Shop.builder().id(2L).city(city1).build();
        Shop shop3InCity17 = Shop.builder().id(3L).city(city17).build();
        Shop shop4InCity17 = Shop.builder().id(4L).city(city17).build();

        ShopChain cnInCity1 = ShopChain.builder().id(1L).shops(List.of(shop1InCity1)).build();
        ShopChain cnInCities1And17 =
                ShopChain.builder().id(2L).shops(List.of(shop2InCity1, shop3InCity17)).build();
        ShopChain cnInCity17 = ShopChain.builder().id(3L).shops(List.of(shop4InCity17)).build();

        ModelMapper modelMapper = new ModelMapper();

        cnResponsesInAllCity = modelMapper.map(List.of(cnInCity1, cnInCities1And17, cnInCity17),
                new TypeToken<ArrayList<ShopChainResponse>>() {
                }.getType());
        cnResponsesInCity1 = modelMapper.map(List.of(cnInCity1, cnInCities1And17),
                new TypeToken<ArrayList<ShopChainResponse>>() {
                }.getType());
        cnResponsesInCity17 = modelMapper.map(List.of(cnInCities1And17, cnInCity17),
                new TypeToken<ArrayList<ShopChainResponse>>() {
                }.getType());

        cnWithShopsResponsesInAllCity = modelMapper.map(List.of(cnInCity1, cnInCities1And17, cnInCity17),
                new TypeToken<ArrayList<ShopChainWithShopsResponse>>() {
                }.getType());
        cnWithShopsResponsesInCity1 = modelMapper.map(List.of(cnInCity1, cnInCities1And17),
                new TypeToken<ArrayList<ShopChainWithShopsResponse>>() {
                }.getType());
        cnWithShopsResponsesInCity17 = modelMapper.map(List.of(cnInCities1And17, cnInCity17),
                new TypeToken<ArrayList<ShopChainWithShopsResponse>>() {
                }.getType());
    }

    @BeforeEach
    public void mockRepository() {
        when(service.getShopChains(false, null)).thenReturn(cnResponsesInAllCity);
        when(service.getShopChains(false, 1L)).thenReturn(cnResponsesInCity1);
        when(service.getShopChains(false, 17L)).thenReturn(cnResponsesInCity17);
        when(service.getShopChains(true, null)).thenReturn(cnWithShopsResponsesInAllCity);
        when(service.getShopChains(true, 1L)).thenReturn(cnWithShopsResponsesInCity1);
        when(service.getShopChains(true, 17L)).thenReturn(cnWithShopsResponsesInCity17);
    }

    @Test
    void getAllCommercialNetworks_NoCityId_http200() throws Exception {
        MvcResult result = mvc.perform(get("/api/shop_chains")
                        .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user")))
                .andReturn();

        Object resultContent = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, ShopChainResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertEquals(cnResponsesInAllCity, resultContent)
        );
    }

    @Test
    void getAllCommercialNetworks_withShopsNoCityId_http200() throws Exception {
        MvcResult result = mvc.perform(get("/api/shop_chains")
                        .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                        .header("With-Shops", "true"))
                .andReturn();

        Object resultContent = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(ArrayList.class, ShopChainWithShopsResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertEquals(cnWithShopsResponsesInAllCity, resultContent)
        );
    }

    @Test
    void getAllCommercialNetworks_withShopsCityId17_http200() throws Exception {
        MvcResult result = mvc.perform(get("/api/shop_chains")
                        .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                        .header("With-Shops", "true")
                        .header("City-Id", 17))
                .andReturn();

        Object resultContent = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(ArrayList.class, ShopChainWithShopsResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertEquals(cnWithShopsResponsesInCity17, resultContent)
        );
    }

    @Test
    void getAllCommercialNetworks_wrongToken_http401() throws Exception {
        mvc.perform(get("/api/shop_chains")
                        .header(authHeaderName, "Bearer wrong token")
                        .header("With-Shops", "true")
                        .header("City-Id", 17))
                .andExpect(status().isUnauthorized());
    }
}