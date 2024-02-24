package an.evdokimov.discount.watcher.server.service.shop;

import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.database.shop.model.Shop;
import an.evdokimov.discount.watcher.server.database.shop.repository.ShopRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ShopServiceImpl.class)
class ShopServiceTest {
    @MockBean
    private ShopRepository shopRepository;

    @Autowired
    private ShopServiceImpl testedShopService;

    @Test
    void getShopById_existingShop_shop() throws ServerException {
        Shop shopInDb = Shop.builder()
                .id(1L)
                .name("shop1")
                .build();

        when(shopRepository.findById(shopInDb.getId())).thenReturn(Optional.of(shopInDb));

        Shop returnedShop = testedShopService.getById(1L);

        assertEquals(shopInDb, returnedShop);
    }

    @Test
    void getShopById_nonexistentShop_ShopResponse() {
        when(shopRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                ServerException.class,
                () -> testedShopService.getById(1111L)
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

        Collection<Shop> returnedShops = testedShopService.getAll();
        assertThat(
                returnedShops,
                containsInAnyOrder(shopInDb1, shopInDb2, shopInDb3)
        );
    }

    @Test
    void getAllShops_nonexistentShops_emptyList() {
        when(shopRepository.findAll()).thenReturn(List.of());

        Collection<Shop> returnedShops = testedShopService.getAll();
        assertTrue(returnedShops.isEmpty());
    }
}