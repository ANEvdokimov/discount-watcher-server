package an.evdokimov.discount.watcher.server.api.product.controller;

import an.evdokimov.discount.watcher.server.api.TestConfig;
import an.evdokimov.discount.watcher.server.api.error.ServerErrorCode;
import an.evdokimov.discount.watcher.server.api.product.dto.request.UserProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.UserProductResponse;
import an.evdokimov.discount.watcher.server.api.product.maintenance.UserProductMaintenance;
import an.evdokimov.discount.watcher.server.configuration.SecurityConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserProductController.class)
@Import({TestConfig.class, SecurityConfiguration.class})
public class UserProductControllerTest {
    private static final String AUTH_HEADER_NAME = "Authenticated-User";
    private static final String AUTH_USER = "test_user";

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserProductMaintenance service;

    @Autowired
    private MockMvc mvc;

    @Test
    void getUserProducts_availability_listOfProducts() throws Exception {
        List<UserProductResponse> products = List.of(
                UserProductResponse.builder().id(0L).build(),
                UserProductResponse.builder().id(1L).build(),
                UserProductResponse.builder().id(2L).build()
        );

        when(service.getAll(
                any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(Collections.emptyList());
        when(service.getAll(
                eq(testConfig.getTestUser()), anyBoolean(), eq(true), isNull(), isNull())
        ).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products/by_user")
                .header(AUTH_HEADER_NAME, AUTH_USER)
                .header("only-active", true)
                .header("monitor-availability", true)
        ).andReturn();

        ArrayList<UserProductResponse> resultProducts = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, UserProductResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(resultProducts, containsInAnyOrder(products.toArray())),
                () -> verify(service, times(1))
                        .getAll(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                anyBoolean(),
                                isNull(),
                                isNull()
                        ),
                () -> verify(service, times(0))
                        .getAll(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyLong()
                        ),
                () -> verify(service, times(0))
                        .getAll(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                anyBoolean(),
                                isNull(),
                                isNull(),
                                anyLong()
                        )
        );
    }

    @Test
    void getUserProducts_discountWithFalseOtherParameters_listOfProducts() throws Exception {
        List<UserProductResponse> products = List.of(
                UserProductResponse.builder().id(0L).build(),
                UserProductResponse.builder().id(1L).build(),
                UserProductResponse.builder().id(2L).build()
        );

        when(service.getAll(
                any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(Collections.emptyList());
        when(service.getAll(
                eq(testConfig.getTestUser()), anyBoolean(), eq(false), eq(true), eq(false))
        ).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products/by_user")
                .header(AUTH_HEADER_NAME, AUTH_USER)
                .header("only-active", true)
                .header("monitor-availability", false)
                .header("monitor-discount", true)
                .header("monitor-price-changes", false)
        ).andReturn();

        ArrayList<UserProductResponse> resultProducts = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, UserProductResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(resultProducts, containsInAnyOrder(products.toArray())),
                () -> verify(service, times(1))
                        .getAll(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean()
                        ),
                () -> verify(service, times(0))
                        .getAll(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyLong()
                        )
        );
    }

    @Test
    void getUserProducts_priceChages_listOfProducts() throws Exception {
        List<UserProductResponse> products = List.of(
                UserProductResponse.builder().id(0L).build(),
                UserProductResponse.builder().id(1L).build(),
                UserProductResponse.builder().id(2L).build()
        );

        when(service.getAll(
                any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(Collections.emptyList());
        when(service.getAll(
                eq(testConfig.getTestUser()), anyBoolean(), isNull(), isNull(), eq(true))
        ).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products/by_user")
                .header(AUTH_HEADER_NAME, AUTH_USER)
                .header("only-active", true)
                .header("monitor-price-changes", true)
        ).andReturn();

        ArrayList<UserProductResponse> resultProducts = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, UserProductResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(resultProducts, containsInAnyOrder(products.toArray())),
                () -> verify(service, times(1))
                        .getAll(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                isNull(),
                                isNull(),
                                anyBoolean()
                        ),
                () -> verify(service, times(0))
                        .getAll(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyLong()
                        ),
                () -> verify(service, times(0))
                        .getAll(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                isNull(),
                                isNull(),
                                anyBoolean(),
                                anyLong()
                        )
        );
    }

    @Test
    void getUserProducts_availabilityAndDiscount_listOfProducts() throws Exception {
        List<UserProductResponse> products = List.of(
                UserProductResponse.builder().id(0L).build(),
                UserProductResponse.builder().id(1L).build(),
                UserProductResponse.builder().id(2L).build()
        );

        when(service.getAll(
                any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(Collections.emptyList());
        when(service.getAll(
                eq(testConfig.getTestUser()), anyBoolean(), eq(true), eq(true), isNull())
        ).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products/by_user")
                .header(AUTH_HEADER_NAME, AUTH_USER)
                .header("only-active", true)
                .header("monitor-availability", true)
                .header("monitor-discount", true)
        ).andReturn();

        ArrayList<UserProductResponse> resultProducts = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, UserProductResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(resultProducts, containsInAnyOrder(products.toArray())),
                () -> verify(service, times(1))
                        .getAll(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                isNull()
                        ),
                () -> verify(service, times(0))
                        .getAll(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyLong()
                        ),
                () -> verify(service, times(0))
                        .getAll(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                isNull(),
                                anyLong()
                        )
        );
    }

    @Test
    void getUserProducts_availabilityAndShopId_listOfProducts() throws Exception {
        List<UserProductResponse> products = List.of(
                UserProductResponse.builder().id(0L).build(),
                UserProductResponse.builder().id(1L).build(),
                UserProductResponse.builder().id(2L).build()
        );

        when(service.getAll(
                any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyLong())
        ).thenReturn(Collections.emptyList());
        when(service.getAll(
                eq(testConfig.getTestUser()), anyBoolean(),
                eq(true), isNull(), isNull(), eq(1L))
        ).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products/by_user")
                .header(AUTH_HEADER_NAME, AUTH_USER)
                .header("only-active", true)
                .header("monitor-availability", true)
                .header("shop-id", 1)
        ).andReturn();

        ArrayList<UserProductResponse> resultProducts = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, UserProductResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(resultProducts, containsInAnyOrder(products.toArray())),
                () -> verify(service, times(1))
                        .getAll(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                anyBoolean(),
                                isNull(),
                                isNull(),
                                anyLong()
                        )
        );
    }

    @Test
    void getUserProducts_discountAndShopId_listOfProducts() throws Exception {
        List<UserProductResponse> products = List.of(
                UserProductResponse.builder().id(0L).build(),
                UserProductResponse.builder().id(1L).build(),
                UserProductResponse.builder().id(2L).build()
        );

        when(service.getAll(
                any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyLong())
        ).thenReturn(Collections.emptyList());
        when(service.getAll(
                eq(testConfig.getTestUser()), anyBoolean(),
                isNull(), eq(true), isNull(), eq(1L))
        ).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products/by_user")
                .header(AUTH_HEADER_NAME, AUTH_USER)
                .header("only-active", true)
                .header("monitor-discount", true)
                .header("shop-id", 1)
        ).andReturn();

        ArrayList<UserProductResponse> resultProducts = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, UserProductResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(resultProducts, containsInAnyOrder(products.toArray())),
                () -> verify(service, times(1))
                        .getAll(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                isNull(),
                                anyBoolean(),
                                isNull(),
                                anyLong()
                        )
        );
    }

    @Test
    void getUserProducts_priceChangesAndShopId_listOfProducts() throws Exception {
        List<UserProductResponse> products = List.of(
                UserProductResponse.builder().id(0L).build(),
                UserProductResponse.builder().id(1L).build(),
                UserProductResponse.builder().id(2L).build()
        );

        when(service.getAll(
                any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyLong())
        ).thenReturn(Collections.emptyList());
        when(service.getAll(
                eq(testConfig.getTestUser()), anyBoolean(),
                isNull(), isNull(), eq(true), eq(1L))
        ).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products/by_user")
                .header(AUTH_HEADER_NAME, AUTH_USER)
                .header("only-active", true)
                .header("monitor-price-changes", true)
                .header("shop-id", 1)
        ).andReturn();

        ArrayList<UserProductResponse> resultProducts = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, UserProductResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(resultProducts, containsInAnyOrder(products.toArray())),
                () -> verify(service, times(1))
                        .getAll(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                isNull(),
                                isNull(),
                                anyBoolean(),
                                anyLong()
                        )
        );
    }

    @Test
    void getUserProducts_discountAndpriceChangesAndShopId_listOfProducts() throws Exception {
        List<UserProductResponse> products = List.of(
                UserProductResponse.builder().id(0L).build(),
                UserProductResponse.builder().id(1L).build(),
                UserProductResponse.builder().id(2L).build()
        );

        when(service.getAll(
                any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyLong())
        ).thenReturn(Collections.emptyList());
        when(service.getAll(
                eq(testConfig.getTestUser()), anyBoolean(),
                isNull(), eq(true), eq(true), eq(1L))
        ).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products/by_user")
                .header(AUTH_HEADER_NAME, AUTH_USER)
                .header("only-active", true)
                .header("monitor-discount", true)
                .header("monitor-price-changes", true)
                .header("shop-id", 1)
        ).andReturn();

        ArrayList<UserProductResponse> resultProducts = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, UserProductResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(resultProducts, containsInAnyOrder(products.toArray())),
                () -> verify(service, times(1))
                        .getAll(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                isNull(),
                                anyBoolean(),
                                anyBoolean(),
                                anyLong()
                        )
        );
    }

    @Test
    void getUserProducts_withoutPriceHistoryHeader_400() throws Exception {
        List<UserProductResponse> products = List.of(
                UserProductResponse.builder().id(0L).build(),
                UserProductResponse.builder().id(1L).build(),
                UserProductResponse.builder().id(2L).build()
        );

        MvcResult result = mvc.perform(get("/api/products/by_user")
                .header(AUTH_HEADER_NAME, AUTH_USER)
        ).andReturn();

        assertAll(
                () -> assertEquals(400, result.getResponse().getStatus()),
                () -> verify(service, times(0))
                        .getAll(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean()
                        ),
                () -> verify(service, times(0))
                        .getAll(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyLong()
                        )
        );
    }

    @Test
    void update_userProduct_http200() throws Exception {
        UserProductRequest userProduct = UserProductRequest.builder()
                .id(666L)
                .productId(555L)
                .monitorDiscount(true)
                .monitorAvailability(false)
                .monitorPriceChanges(true)
                .build();

        MvcResult result = mvc.perform(post("/api/products/by_user")
                        .header(AUTH_HEADER_NAME, AUTH_USER)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(userProduct)))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void update_noId_http400() throws Exception {
        UserProductRequest userProduct = UserProductRequest.builder()
                .productId(555L)
                .monitorDiscount(true)
                .monitorAvailability(false)
                .monitorPriceChanges(true)
                .build();

        MvcResult result = mvc.perform(post("/api/products/by_user")
                        .header(AUTH_HEADER_NAME, AUTH_USER)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(userProduct)))
                .andReturn();

        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    void update_nonexistentUserProduct_http400() throws Exception {
        UserProductRequest userProduct = UserProductRequest.builder()
                .id(666L)
                .productId(555L)
                .monitorDiscount(true)
                .monitorAvailability(false)
                .monitorPriceChanges(true)
                .build();

        doThrow(ServerErrorCode.USER_PRODUCT_NOT_FOUND.getException())
                .when(service).update(any(), eq(userProduct));

        MvcResult result = mvc.perform(post("/api/products/by_user")
                        .header(AUTH_HEADER_NAME, AUTH_USER)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(userProduct)))
                .andReturn();

        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    void delete_userProduct_http200() throws Exception {
        MvcResult result = mvc.perform(delete("/api/products/by_user/666")
                        .header(AUTH_HEADER_NAME, AUTH_USER))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    void delete_noId_http404() throws Exception {
        MvcResult result = mvc.perform(delete("/api/products/by_user/")
                        .header(AUTH_HEADER_NAME, AUTH_USER))
                .andReturn();

        assertEquals(404, result.getResponse().getStatus());
    }

    @Test
    void delete_nonexistentUserProduct_http400() throws Exception {
        doThrow(ServerErrorCode.USER_PRODUCT_NOT_FOUND.getException())
                .when(service).delete(any(), eq(666L));

        MvcResult result = mvc.perform(delete("/api/products/by_user/666")
                        .header(AUTH_HEADER_NAME, AUTH_USER))
                .andReturn();

        assertEquals(400, result.getResponse().getStatus());
    }
}
