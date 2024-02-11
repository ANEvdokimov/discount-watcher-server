package an.evdokimov.discount.watcher.server.api.shop.maintenance;

import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopResponse;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.mapper.shop.ShopMapper;
import an.evdokimov.discount.watcher.server.service.shop.ShopService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ShopMaintenanceImpl.class)
class ShopMaintenanceImplTest {
    @MockBean
    private ShopService shopService;
    @MockBean
    private ShopMapper shopMapper;

    @Autowired
    private ShopMaintenanceImpl testedShopMaintenance;

    @Test
    @DisplayName("get shop by id")
    void getShopById_existingShop_ShopResponse() throws ServerException {
        Shop shopInDb = Shop.builder()
                .id(1L)
                .name("shop1")
                .build();
        ShopResponse shopResponse = ShopResponse.builder()
                .id(shopInDb.getId())
                .name(shopInDb.getName())
                .build();

        when(shopService.getShopById(shopInDb.getId())).thenReturn(shopInDb);
        when(shopMapper.map(shopInDb)).thenReturn(shopResponse);

        ShopResponse returnedShopResponse = testedShopMaintenance.getShopById(1L);

        assertEquals(shopResponse, returnedShopResponse);
    }

    @SneakyThrows
    @Test
    @DisplayName("get nonexistent shop by id")
    void getShopById_nonexistentShop_ShopResponse() {
        when(shopService.getShopById(anyLong())).thenThrow(ServerErrorCode.SHOP_NOT_FOUND.getException());

        assertThrows(
                ServerException.class,
                () -> testedShopMaintenance.getShopById(1111L)
        );
    }

    @Test
    @DisplayName("get all shops")
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

        when(shopService.getAllShops()).thenReturn(List.of(shopInDb1, shopInDb2, shopInDb3));
        when(shopMapper.map(shopInDb1)).thenReturn(shopResponse1);
        when(shopMapper.map(shopInDb2)).thenReturn(shopResponse2);
        when(shopMapper.map(shopInDb3)).thenReturn(shopResponse3);

        Collection<ShopResponse> returnedShops = testedShopMaintenance.getAllShops();
        assertThat(
                returnedShops,
                containsInAnyOrder(shopResponse1, shopResponse2, shopResponse3)
        );
    }

    @Test
    @DisplayName("get all nonexistent shops")
    void getAllShops_nonexistentShops_emptyList() {
        when(shopService.getAllShops()).thenReturn(List.of());

        Collection<ShopResponse> returnedShops = testedShopMaintenance.getAllShops();
        assertTrue(returnedShops.isEmpty());
    }
}