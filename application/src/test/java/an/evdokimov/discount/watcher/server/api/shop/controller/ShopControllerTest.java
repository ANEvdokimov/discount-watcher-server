package an.evdokimov.discount.watcher.server.api.shop.controller;

import an.evdokimov.discount.watcher.server.api.TestConfig;
import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.error.ServerException;
import an.evdokimov.discount.watcher.server.api.shop.dto.response.ShopResponse;
import an.evdokimov.discount.watcher.server.service.shop.ShopServiceImpl;
import an.evdokimov.securitystarter.security.authentication.jwt.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    @Value("${application.security.header.authentication}")
    private String authHeaderName;

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ShopServiceImpl shopService;

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
        when(shopService.getAllShops()).thenReturn(List.of(shop1, shop2, shop3));
        when(shopService.getAllUserShops(testConfig.getTestUser())).thenReturn(List.of(shop1, shop2));

        MvcResult result = mvc.perform(get("/api/shops")
                        .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user")))
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
        when(shopService.getAllShops()).thenReturn(List.of(shop1, shop2, shop3));
        when(shopService.getAllUserShops(testConfig.getTestUser())).thenReturn(List.of(shop1, shop2));

        MvcResult result = mvc.perform(get("/api/shops")
                        .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
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
        when(shopService.getAllShops()).thenReturn(List.of(shop1, shop2, shop3));
        when(shopService.getAllUserShops(testConfig.getTestUser())).thenReturn(List.of(shop1, shop2));

        MvcResult result = mvc.perform(get("/api/shops")
                        .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
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
    void getAllShops_invalidJwt_http401() throws Exception {
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
        when(shopService.getAllShops()).thenReturn(List.of(shop1, shop2, shop3));

        MvcResult result = mvc.perform(get("/api/shops")
                        .header(authHeaderName, "invalid JWT"))
                .andReturn();

        assertAll(
                () -> assertEquals(401, result.getResponse().getStatus()),
                () -> verify(shopService, times(0)).getAllShops()
        );
    }

    @Test
    void getShopById_validJwt_http200() throws Exception {
        ShopResponse shop1 = ShopResponse.builder()
                .id(1L)
                .name("shop1")
                .build();
        when(shopService.getShopById(1L)).thenReturn(shop1);

        MvcResult result = mvc.perform(get("/api/shop/" + shop1.getId())
                        .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user")))
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
    void getShopById_invalidJwt_http401() throws Exception {
        ShopResponse shop1 = ShopResponse.builder()
                .id(1L)
                .name("shop1")
                .build();
        when(shopService.getShopById(1L)).thenReturn(shop1);

        MvcResult result = mvc.perform(get("/api/shop/" + shop1.getId())
                        .header(authHeaderName, "invalid jwt"))
                .andReturn();

        assertAll(
                () -> assertEquals(401, result.getResponse().getStatus()),
                () -> verify(shopService, times(0)).getShopById(shop1.getId())
        );
    }

    @Test
    void getShopById_withoutJwt_http401() throws Exception {
        ShopResponse shop1 = ShopResponse.builder()
                .id(1L)
                .name("shop1")
                .build();
        when(shopService.getShopById(1L)).thenReturn(shop1);

        MvcResult result = mvc.perform(get("/api/shop/" + shop1.getId()))
                .andReturn();

        assertAll(
                () -> assertEquals(401, result.getResponse().getStatus()),
                () -> verify(shopService, times(0)).getShopById(shop1.getId())
        );
    }

    @Test
    void getShopById_nonexistentShop_http400() throws Exception {
        when(shopService.getShopById(anyLong())).thenThrow(new ServerException(ServerErrorCode.SHOP_NOT_FOUND));

        MvcResult result = mvc.perform(get("/api/shop/10")
                        .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user")))
                .andReturn();

        assertAll(
                () -> assertEquals(400, result.getResponse().getStatus()),
                () -> verify(shopService, times(1)).getShopById(anyLong())
        );
    }
}