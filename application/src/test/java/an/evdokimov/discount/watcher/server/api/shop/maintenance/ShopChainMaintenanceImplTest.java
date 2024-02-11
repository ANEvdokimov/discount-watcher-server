package an.evdokimov.discount.watcher.server.api.shop.maintenance;

import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainResponse;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainWithShopsResponse;
import an.evdokimov.discount.watcher.server.database.city.model.City;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.model.ShopChain;
import an.evdokimov.discount.watcher.server.mapper.shop.ShopChainMapper;
import an.evdokimov.discount.watcher.server.service.shop.ShopChainService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShopChainMaintenanceImplTest {
    @MockBean
    private ShopChainService shopChainService;
    @MockBean
    private ShopChainMapper mapper;

    @Autowired
    private ShopChainMaintenanceImpl testedMaintenance;

    private ShopChain scInCity1;
    private ShopChain scInCities1And17;
    private ShopChain scInCity17;

    private ShopChainResponse scrInCity1;
    private ShopChainWithShopsResponse scrwsInCity1;
    private ShopChainResponse scrInCities1And17;
    private ShopChainWithShopsResponse scrwsInCities1And17;
    private ShopChainResponse scrInCity17;
    private ShopChainWithShopsResponse scrwsInCity17;

    @BeforeAll
    public void createShopChains() {
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

        scInCity1 = ShopChain.builder().id(1L).shops(List.of(shop1InCity1)).build();
        scInCities1And17 = ShopChain.builder().id(2L).shops(List.of(shop2InCity1, shop3InCity17)).build();
        scInCity17 = ShopChain.builder().id(3L).shops(List.of(shop4InCity17)).build();

        scrInCity1 = ShopChainResponse.builder().id(scInCity1.getId()).build();
        scrwsInCity1 = ShopChainWithShopsResponse.builder().id(scInCity1.getId())
                .shops(List.of(shopResponse1InCity1)).build();
        scrInCities1And17 = ShopChainResponse.builder().id(scInCities1And17.getId()).build();
        scrwsInCities1And17 = ShopChainWithShopsResponse.builder().id(scInCities1And17.getId())
                .shops(List.of(shopResponse2InCity1, shopResponse3InCity17)).build();
        scrInCity17 = ShopChainResponse.builder().id(scInCity17.getId()).build();
        scrwsInCity17 = ShopChainWithShopsResponse.builder().id(scInCity17.getId())
                .shops(List.of(shopResponse4InCity17)).build();
    }

    @BeforeEach
    public void mockServiceAndMapper() {
        when(shopChainService.getShopChains(null)).thenReturn(List.of(scInCity1, scInCities1And17, scInCity17));
        when(shopChainService.getShopChains(1L)).thenReturn(List.of(scInCity1, scInCities1And17));
        when(shopChainService.getShopChains(17L)).thenReturn(List.of(scInCities1And17, scInCity17));

        when(mapper.toDto(scInCity1)).thenReturn(scrInCity1);
        when(mapper.toDto(scInCities1And17)).thenReturn(scrInCities1And17);
        when(mapper.toDto(scInCity17)).thenReturn(scrInCity17);
        when(mapper.toDtoWithShops(scInCity1)).thenReturn(scrwsInCity1);
        when(mapper.toDtoWithShops(scInCities1And17)).thenReturn(scrwsInCities1And17);
        when(mapper.toDtoWithShops(scInCity17)).thenReturn(scrwsInCity17);
    }

    @Test
    @DisplayName("get all shop chains")
    void getAllShopChains_withShopsFalseCityNull_listOfSc() {
        Collection<ShopChainResponse> result = testedMaintenance.getShopChains(false, null);

        assertEquals(List.of(scrInCity1, scrInCities1And17, scrInCity17), result);
    }

    @Test
    @DisplayName("get all shop chains with shops")
    void getAllShopChains_withShopsTrueCityNull_listOfSc() {
        Collection<ShopChainResponse> result = testedMaintenance.getShopChains(true, null);

        assertEquals(List.of(scrwsInCity1, scrwsInCities1And17, scrwsInCity17), result);
    }

    @Test
    @DisplayName("get all shop chains in city")
    void getAllShopChains_withShopsFalseCity1_listOfSc() {
        Collection<ShopChainResponse> result = testedMaintenance.getShopChains(false, 1L);

        assertEquals(List.of(scrInCity1, scrInCities1And17), result);
    }

    @Test
    @DisplayName("get all shop chain with shops in city")
    void getAllShopChains_withShopsTrueCity17_listOfSc() {
        Collection<ShopChainResponse> result = testedMaintenance.getShopChains(true, 17L);

        assertEquals(List.of(scrwsInCities1And17, scrwsInCity17), result);
    }
}