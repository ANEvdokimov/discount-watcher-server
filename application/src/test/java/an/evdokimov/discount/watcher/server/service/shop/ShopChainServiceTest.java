package an.evdokimov.discount.watcher.server.service.shop;

import an.evdokimov.discount.watcher.server.api.city.dto.response.CityResponse;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainResponse;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopChainWithShopsResponse;
import an.evdokimov.discount.watcher.server.database.city.model.City;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.model.ShopChain;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopChainRepository;
import an.evdokimov.discount.watcher.server.mapper.shop.ShopChainMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ShopChainServiceImpl.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShopChainServiceTest {
    @MockBean
    private ShopChainRepository repository;
    @MockBean
    private ShopChainMapper mapper;

    @Autowired
    private ShopChainServiceImpl testedService;

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
    public void mockRepository() {
        when(repository.findAll()).thenReturn(List.of(scInCity1, scInCities1And17, scInCity17));
        when(repository.findByCityId(1L)).thenReturn(List.of(scInCity1, scInCities1And17));
        when(repository.findByCityId(17L)).thenReturn(List.of(scInCities1And17, scInCity17));
    }

    @Test
    void getAllShopChains_viewShopsFalseCityNull_listOfSc() {
        when(mapper.toDto(List.of(scInCity1, scInCities1And17, scInCity17)))
                .thenReturn(List.of(scrInCity1, scrInCities1And17, scrInCity17));

        Collection<ShopChainResponse> result = testedService.getShopChains(false, null);

        assertEquals(List.of(scrInCity1, scrInCities1And17, scrInCity17), result);
    }

    @Test
    void getAllShopChains_viewShopsTrueCityNull_listOfSc() {
        when(mapper.toDtoWithShops(List.of(scInCity1, scInCities1And17, scInCity17)))
                .thenReturn(List.of(scrwsInCity1, scrwsInCities1And17, scrwsInCity17));

        Collection<ShopChainResponse> result = testedService.getShopChains(true, null);

        assertEquals(List.of(scrwsInCity1, scrwsInCities1And17, scrwsInCity17), result);
    }

    @Test
    void getAllShopChains_viewShopsFalseCity1_listOfSc() {
        when(mapper.toDto(List.of(scInCity1, scInCities1And17)))
                .thenReturn(List.of(scrwsInCity1, scrwsInCities1And17));

        Collection<ShopChainResponse> result = testedService.getShopChains(false, 1L);

        assertEquals(List.of(scrwsInCity1, scrwsInCities1And17), result);
    }

    @Test
    void getAllShopChains_viewShopsTrueCity17_listOfSc() {
        when(mapper.toDtoWithShops(List.of(scInCities1And17, scInCity17)))
                .thenReturn(List.of(scrwsInCities1And17, scrwsInCity17));

        Collection<ShopChainResponse> result = testedService.getShopChains(true, 17L);

        assertEquals(List.of(scrwsInCities1And17, scrwsInCity17), result);
    }
}
