package an.evdokimov.discount.watcher.server.api.shop.controller;

import an.evdokimov.discount.watcher.server.api.TestConfig;
import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainResponse;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainWithShopsResponse;
import an.evdokimov.discount.watcher.server.api.shop.maintenance.ShopChainMaintenance;
import an.evdokimov.discount.watcher.server.configuration.SecurityConfiguration;
import an.evdokimov.discount.watcher.server.database.city.model.City;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.model.ShopChain;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ShopChainController.class)
@Import({TestConfig.class, SecurityConfiguration.class})
class ShopChainControllerTest {
    private static final String AUTH_HEADER_NAME = "Authenticated-User";
    private static final String AUTH_USER = "test_user";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShopChainMaintenance maintenance;

    private static List<ShopChainResponse> scResponsesInAllCity;
    private static List<ShopChainResponse> scResponsesInCity1;
    private static List<ShopChainResponse> scResponsesInCity17;
    private static List<ShopChainResponse> scWithShopsResponsesInAllCity;
    private static List<ShopChainResponse> scWithShopsResponsesInCity1;
    private static List<ShopChainResponse> scWithShopsResponsesInCity17;

    @BeforeAll
    public static void createCn() {
        City city1 = City.builder().id(1L).build();
        City city17 = City.builder().id(17L).build();

        CityResponse cityResponse1 = CityResponse.builder().id(city1.getId()).build();
        CityResponse cityResponse17 = CityResponse.builder().id(city17.getId()).build();

        Shop shop1InCity1 = Shop.builder().id(1L).city(city1).build();
        Shop shop2InCity1 = Shop.builder().id(2L).city(city1).build();
        Shop shop3InCity17 = Shop.builder().id(3L).city(city17).build();
        Shop shop4InCity17 = Shop.builder().id(4L).city(city17).build();

        ShopChainWithShopsResponse.ShopResponse shopResponse1InCity1 = ShopChainWithShopsResponse.ShopResponse
                .builder().id(shop1InCity1.getId()).city(cityResponse1).build();
        ShopChainWithShopsResponse.ShopResponse shopResponse2InCity1 = ShopChainWithShopsResponse.ShopResponse
                .builder().id(shop2InCity1.getId()).city(cityResponse1).build();
        ShopChainWithShopsResponse.ShopResponse shopResponse3InCity17 = ShopChainWithShopsResponse.ShopResponse
                .builder().id(shop3InCity17.getId()).city(cityResponse17).build();
        ShopChainWithShopsResponse.ShopResponse shopResponse4InCity17 = ShopChainWithShopsResponse.ShopResponse
                .builder().id(shop4InCity17.getId()).city(cityResponse17).build();

        ShopChain scInCity1 = ShopChain.builder().id(1L).shops(List.of(shop1InCity1)).build();
        ShopChain scInCities1And17 = ShopChain.builder().id(2L).shops(List.of(shop2InCity1, shop3InCity17)).build();
        ShopChain scInCity17 = ShopChain.builder().id(3L).shops(List.of(shop4InCity17)).build();

        ShopChainResponse scrInCity1 = ShopChainResponse.builder().id(scInCity1.getId()).build();
        ShopChainWithShopsResponse scrwsInCity1 = ShopChainWithShopsResponse.builder().id(scInCity1.getId())
                .shops(List.of(shopResponse1InCity1)).build();
        ShopChainResponse scrInCities1And17 = ShopChainResponse.builder().id(scInCities1And17.getId()).build();
        ShopChainWithShopsResponse scrwsInCities1And17 = ShopChainWithShopsResponse.builder()
                .id(scInCities1And17.getId()).shops(List.of(shopResponse2InCity1, shopResponse3InCity17)).build();
        ShopChainResponse scrInCity17 = ShopChainResponse.builder().id(scInCity17.getId()).build();
        ShopChainWithShopsResponse scrwsInCity17 = ShopChainWithShopsResponse.builder().id(scInCity17.getId())
                .shops(List.of(shopResponse4InCity17)).build();

        scResponsesInAllCity = List.of(scrInCity1, scrInCities1And17, scrInCity17);
        scResponsesInCity1 = List.of(scrInCity1, scrInCities1And17);
        scResponsesInCity17 = List.of(scrInCities1And17, scrInCity17);

        scWithShopsResponsesInAllCity = List.of(scrwsInCity1, scrwsInCities1And17, scrwsInCity17);
        scWithShopsResponsesInCity1 = List.of(scrwsInCity1, scrwsInCities1And17);
        scWithShopsResponsesInCity17 = List.of(scrwsInCities1And17, scrwsInCity17);
    }

    @BeforeEach
    public void mockRepository() {
        when(maintenance.getAll(false, null)).thenReturn(scResponsesInAllCity);
        when(maintenance.getAll(false, 1L)).thenReturn(scResponsesInCity1);
        when(maintenance.getAll(false, 17L)).thenReturn(scResponsesInCity17);
        when(maintenance.getAll(true, null)).thenReturn(scWithShopsResponsesInAllCity);
        when(maintenance.getAll(true, 1L)).thenReturn(scWithShopsResponsesInCity1);
        when(maintenance.getAll(true, 17L)).thenReturn(scWithShopsResponsesInCity17);
    }

    @Test
    void getAllShopChains_NoCityId_http200() throws Exception {
        MvcResult result = mvc.perform(get("/api/shop_chains")
                        .header(AUTH_HEADER_NAME, AUTH_USER))
                .andReturn();

        Object resultContent = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, ShopChainResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertEquals(scResponsesInAllCity, resultContent)
        );
    }

    @Test
    void getAllShopChains_withShopsNoCityId_http200() throws Exception {
        MvcResult result = mvc.perform(get("/api/shop_chains")
                        .header(AUTH_HEADER_NAME, AUTH_USER)
                        .header("With-Shops", "true"))
                .andReturn();

        Object resultContent = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(ArrayList.class, ShopChainWithShopsResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertEquals(scWithShopsResponsesInAllCity, resultContent)
        );
    }

    @Test
    void getAllShopChains_withShopsCityId17_http200() throws Exception {
        MvcResult result = mvc.perform(get("/api/shop_chains")
                        .header(AUTH_HEADER_NAME, AUTH_USER)
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
                () -> assertEquals(scWithShopsResponsesInCity17, resultContent)
        );
    }
}