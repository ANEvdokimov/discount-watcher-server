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

@SpringBootTest
class ShopServiceTest {
    @MockBean
    private ShopRepository shopRepository;

    @Autowired
    private ShopServiceImpl shopService;

    @Autowired
    private ShopMapper shopMapper;

    @Test
    void getShopById_existingShop_ShopResponse() throws ServerException {
        Shop shopInDb = Shop.builder()
                .id(1L)
                .name("shop1")
                .build();
        when(shopRepository.findById(shopInDb.getId())).thenReturn(Optional.of(shopInDb));

        ShopResponse expectedShop = shopMapper.map(shopInDb);
        ShopResponse returnedShopResponse = shopService.getShopById(1L);

        assertEquals(expectedShop, returnedShopResponse);
    }

    @Test
    void getShopById_nonexistentShop_ShopResponse() {
        when(shopRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                ServerException.class,
                () -> shopService.getShopById(1111L)
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
        when(shopRepository.findAll()).thenReturn(List.of(shopInDb1, shopInDb2, shopInDb3));

        Collection<ShopResponse> returnedShops = shopService.getAllShops();
        assertThat(
                returnedShops,
                containsInAnyOrder(
                        shopMapper.map(shopInDb1),
                        shopMapper.map(shopInDb2),
                        shopMapper.map(shopInDb3)
                )
        );
    }

    @Test
    void getAllShops_nonexistentShops_emptyList() {
        when(shopRepository.findAll()).thenReturn(List.of());

        Collection<ShopResponse> returnedShops = shopService.getAllShops();
        assertTrue(returnedShops.isEmpty());
    }
}