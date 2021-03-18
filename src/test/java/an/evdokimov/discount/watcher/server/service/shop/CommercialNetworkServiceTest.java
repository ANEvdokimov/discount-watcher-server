package an.evdokimov.discount.watcher.server.service.shop;

import an.evdokimov.discount.watcher.server.api.shop.dto.response.CommercialNetworkResponse;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.CommercialNetworkWithShopsResponse;
import an.evdokimov.discount.watcher.server.database.city.model.City;
import an.evdokimov.discount.watcher.server.database.shop.model.CommercialNetwork;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.CommercialNetworkRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class CommercialNetworkServiceTest {
    @Autowired
    private CommercialNetworkService service;

    @Autowired
    private ModelMapper modelMapper;

    @MockBean
    private CommercialNetworkRepository repository;

    private static CommercialNetwork cnInCity1;
    private static CommercialNetwork cnInCities1And17;
    private static CommercialNetwork cnInCity17;

    @BeforeAll
    public static void createCn() {
        City city1 = City.builder().id(1L).build();
        City city17 = City.builder().id(17L).build();

        Shop shop1InCity1 = Shop.builder().id(1L).city(city1).build();
        Shop shop2InCity1 = Shop.builder().id(2L).city(city1).build();
        Shop shop3InCity17 = Shop.builder().id(3L).city(city17).build();
        Shop shop4InCity17 = Shop.builder().id(4L).city(city17).build();

        cnInCity1 = CommercialNetwork.builder().id(1L).shops(List.of(shop1InCity1)).build();
        cnInCities1And17 = CommercialNetwork.builder().id(2L).shops(List.of(shop2InCity1, shop3InCity17)).build();
        cnInCity17 = CommercialNetwork.builder().id(3L).shops(List.of(shop4InCity17)).build();
    }

    @BeforeEach
    public void mockRepository() {
        when(repository.findAll()).thenReturn(List.of(cnInCity1, cnInCities1And17, cnInCity17));
        when(repository.findByCityId(1L)).thenReturn(List.of(cnInCity1, cnInCities1And17));
        when(repository.findByCityId(17L)).thenReturn(List.of(cnInCities1And17, cnInCity17));
    }

    @Test
    void getAllCommercialNetworks_viewShopsFalseCityNull_listOfCn() {
        Collection<CommercialNetworkResponse> result = service.getCommercialNetworks(false, null);

        List<CommercialNetworkResponse> expectedResult = modelMapper.map(
                List.of(cnInCity1, cnInCities1And17, cnInCity17),
                new TypeToken<ArrayList<CommercialNetworkResponse>>() {
                }.getType());

        assertEquals(expectedResult, result);
    }

    @Test
    void getAllCommercialNetworks_viewShopsTrueCityNull_listOfCn() {
        Collection<CommercialNetworkResponse> result = service.getCommercialNetworks(true, null);

        List<CommercialNetworkResponse> expectedResult = modelMapper.map(
                List.of(cnInCity1, cnInCities1And17, cnInCity17),
                new TypeToken<ArrayList<CommercialNetworkWithShopsResponse>>() {
                }.getType());

        assertEquals(expectedResult, result);
    }

    @Test
    void getAllCommercialNetworks_viewShopsFalseCity1_listOfCn() {
        Collection<CommercialNetworkResponse> result = service.getCommercialNetworks(false, 1L);

        List<CommercialNetworkResponse> expectedResult = modelMapper.map(
                List.of(cnInCity1, cnInCities1And17),
                new TypeToken<ArrayList<CommercialNetworkResponse>>() {
                }.getType());

        assertEquals(expectedResult, result);
    }

    @Test
    void getAllCommercialNetworks_viewShopsTrueCity17_listOfCn() {
        Collection<CommercialNetworkResponse> result = service.getCommercialNetworks(true, 17L);

        List<CommercialNetworkResponse> expectedResult = modelMapper.map(
                List.of(cnInCities1And17, cnInCity17),
                new TypeToken<ArrayList<CommercialNetworkWithShopsResponse>>() {
                }.getType());

        assertEquals(expectedResult, result);
    }
}