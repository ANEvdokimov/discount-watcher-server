package an.evdokimov.discount.watcher.server.api.shop.controller;

import an.evdokimov.discount.watcher.server.api.TestConfig;
import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopResponse;
import an.evdokimov.discount.watcher.server.api.shop.maintenance.ShopMaintenance;
import an.evdokimov.discount.watcher.server.configuration.SecurityConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ShopController.class)
@Import({TestConfig.class, SecurityConfiguration.class})
class ShopControllerTest {
    private static final String AUTH_HEADER_NAME = "Authenticated-User";
    private static final String AUTH_USER = "test_user";

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ShopMaintenance shopMaintenance;

    @Autowired
    private MockMvc mvc;

    @Test
    void getAllShops_validJwt_http200() throws Exception {
        ShopResponse shop1 = ShopResponse.builder()
                .id(1L)
                .name("shop1")
                .build();
        ShopResponse shop2 = ShopResponse.builder()
                .id(2L)
                .name("shop2")
                .build();
        ShopResponse shop3 = ShopResponse.builder()
                .id(3L)
                .name("shop3")
                .build();
        when(shopMaintenance.getAllShops()).thenReturn(List.of(shop1, shop2, shop3));
        when(shopMaintenance.getAllUserShops(testConfig.getTestUser())).thenReturn(List.of(shop1, shop2));

        MvcResult result = mvc.perform(get("/api/shops")
                        .header(AUTH_HEADER_NAME, AUTH_USER))
                .andReturn();

        ArrayList<ShopResponse> returnedShopResponses = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, ShopResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(
                        returnedShopResponses,
                        containsInAnyOrder(shop1, shop2, shop3)
                )
        );
    }

    @Test
    void getAllShops_validJwtAndHeaderParameterFalse_http200() throws Exception {
        ShopResponse shop1 = ShopResponse.builder()
                .id(1L)
                .name("shop1")
                .build();
        ShopResponse shop2 = ShopResponse.builder()
                .id(2L)
                .name("shop2")
                .build();
        ShopResponse shop3 = ShopResponse.builder()
                .id(3L)
                .name("shop3")
                .build();
        when(shopMaintenance.getAllShops()).thenReturn(List.of(shop1, shop2, shop3));
        when(shopMaintenance.getAllUserShops(testConfig.getTestUser())).thenReturn(List.of(shop1, shop2));

        MvcResult result = mvc.perform(get("/api/shops")
                        .header(AUTH_HEADER_NAME, AUTH_USER)
                        .header("only-my", "false"))
                .andReturn();

        ArrayList<ShopResponse> returnedShopResponses = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, ShopResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(
                        returnedShopResponses,
                        containsInAnyOrder(shop1, shop2, shop3)
                )
        );
    }

    @Test
    void getAllShops_validJwtAndHeaderParameterTrue_http200() throws Exception {
        ShopResponse shop1 = ShopResponse.builder()
                .id(1L)
                .name("shop1")
                .build();
        ShopResponse shop2 = ShopResponse.builder()
                .id(2L)
                .name("shop2")
                .build();
        ShopResponse shop3 = ShopResponse.builder()
                .id(3L)
                .name("shop3")
                .build();
        when(shopMaintenance.getAllShops()).thenReturn(List.of(shop1, shop2, shop3));
        when(shopMaintenance.getAllUserShops(testConfig.getTestUser())).thenReturn(List.of(shop1, shop2));

        MvcResult result = mvc.perform(get("/api/shops")
                        .header(AUTH_HEADER_NAME, AUTH_USER)
                        .header("only-my", "true"))
                .andReturn();

        ArrayList<ShopResponse> returnedShopResponses = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, ShopResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(
                        returnedShopResponses,
                        containsInAnyOrder(shop1, shop2)
                )
        );
    }

    @Test
    void getShopById_validJwt_http200() throws Exception {
        ShopResponse shop1 = ShopResponse.builder()
                .id(1L)
                .name("shop1")
                .build();
        when(shopMaintenance.getShopById(1L)).thenReturn(shop1);

        MvcResult result = mvc.perform(get("/api/shop/" + shop1.getId())
                        .header(AUTH_HEADER_NAME, AUTH_USER))
                .andReturn();

        ShopResponse returnedShopResponse = mapper.readValue(
                result.getResponse().getContentAsString(),
                ShopResponse.class
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertEquals(shop1, returnedShopResponse)
        );
    }

    @Test
    void getShopById_withoutJwt_http401() throws Exception {
        ShopResponse shop1 = ShopResponse.builder()
                .id(1L)
                .name("shop1")
                .build();
        when(shopMaintenance.getShopById(1L)).thenReturn(shop1);

        MvcResult result = mvc.perform(get("/api/shop/" + shop1.getId()))
                .andReturn();

        assertAll(
                () -> assertEquals(401, result.getResponse().getStatus()),
                () -> verify(shopMaintenance, times(0)).getShopById(shop1.getId())
        );
    }

    @Test
    void getShopById_nonexistentShop_http400() throws Exception {
        when(shopMaintenance.getShopById(anyLong())).thenThrow(new ServerException(ServerErrorCode.SHOP_NOT_FOUND));

        MvcResult result = mvc.perform(get("/api/shop/10")
                        .header(AUTH_HEADER_NAME, AUTH_USER))
                .andReturn();

        assertAll(
                () -> assertEquals(400, result.getResponse().getStatus()),
                () -> verify(shopMaintenance, times(1)).getShopById(anyLong())
        );
    }
}