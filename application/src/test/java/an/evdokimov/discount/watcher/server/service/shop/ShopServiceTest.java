package an.evdokimov.discount.watcher.server.service.shop;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopResponse;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import an.evdokimov.discount.watcher.server.mapper.shop.ShopMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ShopServiceImpl.class)
class ShopServiceTest {
    @MockBean
    private ShopRepository shopRepository;
    @MockBean
    private ShopMapper shopMapper;

    @Autowired
    private ShopServiceImpl testedShopService;

    @Test
    void getShopById_existingShop_ShopResponse() throws ServerException {
        Shop shopInDb = Shop.builder()
                .id(1L)
                .name("shop1")
                .build();
        ShopResponse shopResponse = ShopResponse.builder()
                .id(shopInDb.getId())
                .name(shopInDb.getName())
                .build();

        when(shopRepository.findById(shopInDb.getId())).thenReturn(Optional.of(shopInDb));
        when(shopMapper.map(shopInDb)).thenReturn(shopResponse);

        ShopResponse returnedShopResponse = testedShopService.getShopById(1L);

        assertEquals(shopResponse, returnedShopResponse);
    }

    @Test
    void getShopById_nonexistentShop_ShopResponse() {
        when(shopRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                ServerException.class,
                () -> testedShopService.getShopById(1111L)
        );
    }

    @Test
    void getAllShops_existingShops_ShopResponses() {
        Shop shopInDb1 = Shop.builder()
                .id(1L)
                .name("shop1")
                .build();
        Shop shopInDb2 = Shop.builder()
                .id(2L)
                .name("shop2")
                .build();
        Shop shopInDb3 = Shop.builder()
                .id(3L)
                .name("shop3")
                .build();

        ShopResponse shopResponse1 = ShopResponse.builder()
                .id(shopInDb1.getId())
                .name(shopInDb1.getName())
                .build();
        ShopResponse shopResponse2 = ShopResponse.builder()
                .id(shopInDb2.getId())
                .name(shopInDb2.getName())
                .build();
        ShopResponse shopResponse3 = ShopResponse.builder()
                .id(shopInDb3.getId())
                .name(shopInDb3.getName())
                .build();

        when(shopRepository.findAll()).thenReturn(List.of(shopInDb1, shopInDb2, shopInDb3));
        when(shopMapper.map(shopInDb1)).thenReturn(shopResponse1);
        when(shopMapper.map(shopInDb2)).thenReturn(shopResponse2);
        when(shopMapper.map(shopInDb3)).thenReturn(shopResponse3);

        Collection<ShopResponse> returnedShops = testedShopService.getAllShops();
        assertThat(
                returnedShops,
                containsInAnyOrder(shopResponse1, shopResponse2, shopResponse3)
        );
    }

    @Test
    void getAllShops_nonexistentShops_emptyList() {
        when(shopRepository.findAll()).thenReturn(List.of());

        Collection<ShopResponse> returnedShops = testedShopService.getAllShops();
        assertTrue(returnedShops.isEmpty());
    }
}