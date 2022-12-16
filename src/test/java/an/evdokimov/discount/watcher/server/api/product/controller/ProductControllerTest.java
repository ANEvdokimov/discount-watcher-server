package an.evdokimov.discount.watcher.server.api.product.controller;

import an.evdokimov.discount.watcher.server.api.TestConfig;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.LentaProductPriceResponse;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.configuration.SecurityConfiguration;
import an.evdokimov.discount.watcher.server.security.JwtUtils;
import an.evdokimov.discount.watcher.server.service.product.ProductServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductController.class)
@Import({TestConfig.class, SecurityConfiguration.class})
class ProductControllerTest {
    @Value("${header.authentication}")
    private String authHeaderName;

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ProductServiceImpl productService;

    @Autowired
    private MockMvc mvc;

    @Test
    void addProduct_correctRequest_ProductResponse() throws Exception {
        LentaProductPriceResponse productPriceResponse = LentaProductPriceResponse.builder()
                .price(BigDecimal.valueOf(100))
                .priceWithCard(BigDecimal.valueOf(50))
                .priceWithDiscount(BigDecimal.valueOf(30))
                .build();
        ProductResponse productResponse = ProductResponse.builder()
                .id(2312312L)
                .prices(List.of(productPriceResponse))
                .build();

        when(productService.addProduct(any(), any())).thenReturn(productResponse);

        NewProductRequest request = NewProductRequest.builder()
                .shopId(1L)
                .url(new URL("https://test_url.com"))
                .monitorAvailability(false)
                .monitorDiscount(true)
                .monitorPriceChanges(false)
                .build();

        MvcResult result = mvc.perform(put("/api/products")
                        .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(request)))
                .andReturn();

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertEquals(
                        mapper.writeValueAsString(productResponse),
                        result.getResponse().getContentAsString()),
                () -> verify(productService, times(1))
                        .addProduct(eq(testConfig.getTestUser()), eq(request))
        );
    }

    @Test
    void addProduct_nullUrl_http400() throws Exception {
        NewProductRequest request = NewProductRequest.builder()
                .shopId(1L)
                .url(null)
                .build();

        MvcResult result = mvc.perform(put("/api/products")
                        .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(request)))
                .andReturn();

        assertAll(
                () -> assertEquals(400, result.getResponse().getStatus()),
                () -> verify(productService, times(0))
                        .addProduct(eq(testConfig.getTestUser()), eq(request))
        );
    }

    @Test
    void addProduct_nullShopId_http400() throws Exception {
        NewProductRequest request = NewProductRequest.builder()
                .shopId(null)
                .url(new URL("https://test_url.com"))
                .build();

        MvcResult result = mvc.perform(put("/api/products")
                        .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(request)))
                .andReturn();

        assertAll(
                () -> assertEquals(400, result.getResponse().getStatus()),
                () -> verify(productService, times(0))
                        .addProduct(eq(testConfig.getTestUser()), eq(request))
        );
    }

    @Test
    void addProduct_invalidJwt_http401() throws Exception {
        NewProductRequest request = NewProductRequest.builder()
                .shopId(1L)
                .url(new URL("https://test_url.com"))
                .build();

        MvcResult result = mvc.perform(put("/api/products")
                        .header(authHeaderName, "Bearer wrong_token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(request)))
                .andReturn();

        assertAll(
                () -> assertEquals(401, result.getResponse().getStatus()),
                () -> verify(productService, times(0))
                        .addProduct(eq(testConfig.getTestUser()), eq(request))
        );
    }

    @Test
    void getUserProducts_availability_listOfProducts() throws Exception {
        List<ProductResponse> products = List.of(
                ProductResponse.builder().id(0L).build(),
                ProductResponse.builder().id(1L).build(),
                ProductResponse.builder().id(2L).build()
        );

        when(productService.getUserProducts(
                any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(Collections.emptyList());
        when(productService.getUserProducts(
                eq(testConfig.getTestUser()), anyBoolean(), anyBoolean(), eq(true), isNull(), isNull())
        ).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products")
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                .header("with-price-history", true)
                .header("only-active", true)
                .header("monitor-availability", true)
        ).andReturn();

        ArrayList<ProductResponse> resultProducts = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, ProductResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(resultProducts, containsInAnyOrder(products.toArray())),
                () -> verify(productService, times(1))
                        .getUserProducts(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                isNull(),
                                isNull()
                        ),
                () -> verify(productService, times(0))
                        .getUserProductsInShop(
                                eq(testConfig.getTestUser()),
                                anyLong(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean()
                        ),
                () -> verify(productService, times(0))
                        .getUserProductsInShop(
                                eq(testConfig.getTestUser()),
                                anyLong(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                isNull(),
                                isNull()
                        )
        );
    }

    @Test
    void getUserProducts_discountWithFalseOtherParameters_listOfProducts() throws Exception {
        List<ProductResponse> products = List.of(
                ProductResponse.builder().id(0L).build(),
                ProductResponse.builder().id(1L).build(),
                ProductResponse.builder().id(2L).build()
        );

        when(productService.getUserProducts(
                any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(Collections.emptyList());
        when(productService.getUserProducts(
                eq(testConfig.getTestUser()), anyBoolean(), anyBoolean(), eq(false), eq(true), eq(false))
        ).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products")
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                .header("with-price-history", true)
                .header("only-active", true)
                .header("monitor-availability", false)
                .header("monitor-discount", true)
                .header("monitor-price-changes", false)
        ).andReturn();

        ArrayList<ProductResponse> resultProducts = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, ProductResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(resultProducts, containsInAnyOrder(products.toArray())),
                () -> verify(productService, times(1))
                        .getUserProducts(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean()
                        ),
                () -> verify(productService, times(0))
                        .getUserProductsInShop(
                                eq(testConfig.getTestUser()),
                                anyLong(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean()
                        )
        );
    }

    @Test
    void getUserProducts_priceChages_listOfProducts() throws Exception {
        List<ProductResponse> products = List.of(
                ProductResponse.builder().id(0L).build(),
                ProductResponse.builder().id(1L).build(),
                ProductResponse.builder().id(2L).build()
        );

        when(productService.getUserProducts(
                any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(Collections.emptyList());
        when(productService.getUserProducts(
                eq(testConfig.getTestUser()), anyBoolean(), anyBoolean(), isNull(), isNull(), eq(true))
        ).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products")
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                .header("with-price-history", true)
                .header("only-active", true)
                .header("monitor-price-changes", true)
        ).andReturn();

        ArrayList<ProductResponse> resultProducts = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, ProductResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(resultProducts, containsInAnyOrder(products.toArray())),
                () -> verify(productService, times(1))
                        .getUserProducts(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                anyBoolean(),
                                isNull(),
                                isNull(),
                                anyBoolean()
                        ),
                () -> verify(productService, times(0))
                        .getUserProductsInShop(
                                eq(testConfig.getTestUser()),
                                anyLong(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean()
                        ),
                () -> verify(productService, times(0))
                        .getUserProductsInShop(
                                eq(testConfig.getTestUser()),
                                anyLong(),
                                anyBoolean(),
                                anyBoolean(),
                                isNull(),
                                isNull(),
                                anyBoolean()
                        )
        );
    }

    @Test
    void getUserProducts_availabilityAndDiscount_listOfProducts() throws Exception {
        List<ProductResponse> products = List.of(
                ProductResponse.builder().id(0L).build(),
                ProductResponse.builder().id(1L).build(),
                ProductResponse.builder().id(2L).build()
        );

        when(productService.getUserProducts(
                any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(Collections.emptyList());
        when(productService.getUserProducts(
                eq(testConfig.getTestUser()), anyBoolean(), anyBoolean(), eq(true), eq(true), isNull())
        ).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products")
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                .header("with-price-history", true)
                .header("only-active", true)
                .header("monitor-availability", true)
                .header("monitor-discount", true)
        ).andReturn();

        ArrayList<ProductResponse> resultProducts = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, ProductResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(resultProducts, containsInAnyOrder(products.toArray())),
                () -> verify(productService, times(1))
                        .getUserProducts(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                isNull()
                        ),
                () -> verify(productService, times(0))
                        .getUserProductsInShop(
                                eq(testConfig.getTestUser()),
                                anyLong(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean()
                        ),
                () -> verify(productService, times(0))
                        .getUserProductsInShop(
                                eq(testConfig.getTestUser()),
                                anyLong(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                isNull()
                        )
        );
    }

    @Test
    void getUserProducts_availabilityAndShopId_listOfProducts() throws Exception {
        List<ProductResponse> products = List.of(
                ProductResponse.builder().id(0L).build(),
                ProductResponse.builder().id(1L).build(),
                ProductResponse.builder().id(2L).build()
        );

        when(productService.getUserProductsInShop(
                any(), anyLong(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(Collections.emptyList());
        when(productService.getUserProductsInShop(
                eq(testConfig.getTestUser()), eq(1L), anyBoolean(), anyBoolean(),
                eq(true), isNull(), isNull())
        ).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products")
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                .header("with-price-history", true)
                .header("only-active", true)
                .header("monitor-availability", true)
                .header("shop-id", 1)
        ).andReturn();

        ArrayList<ProductResponse> resultProducts = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, ProductResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(resultProducts, containsInAnyOrder(products.toArray())),
                () -> verify(productService, times(1))
                        .getUserProductsInShop(
                                eq(testConfig.getTestUser()),
                                anyLong(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                isNull(),
                                isNull()
                        )
        );
    }

    @Test
    void getUserProducts_discountAndShopId_listOfProducts() throws Exception {
        List<ProductResponse> products = List.of(
                ProductResponse.builder().id(0L).build(),
                ProductResponse.builder().id(1L).build(),
                ProductResponse.builder().id(2L).build()
        );

        when(productService.getUserProductsInShop(
                any(), anyLong(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(Collections.emptyList());
        when(productService.getUserProductsInShop(
                eq(testConfig.getTestUser()), eq(1L), anyBoolean(), anyBoolean(),
                isNull(), eq(true), isNull())
        ).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products")
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                .header("with-price-history", true)
                .header("only-active", true)
                .header("monitor-discount", true)
                .header("shop-id", 1)
        ).andReturn();

        ArrayList<ProductResponse> resultProducts = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, ProductResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(resultProducts, containsInAnyOrder(products.toArray())),
                () -> verify(productService, times(1))
                        .getUserProductsInShop(
                                eq(testConfig.getTestUser()),
                                anyLong(),
                                anyBoolean(),
                                anyBoolean(),
                                isNull(),
                                anyBoolean(),
                                isNull()
                        )
        );
    }

    @Test
    void getUserProducts_priceChangesAndShopId_listOfProducts() throws Exception {
        List<ProductResponse> products = List.of(
                ProductResponse.builder().id(0L).build(),
                ProductResponse.builder().id(1L).build(),
                ProductResponse.builder().id(2L).build()
        );

        when(productService.getUserProductsInShop(
                any(), anyLong(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(Collections.emptyList());
        when(productService.getUserProductsInShop(
                eq(testConfig.getTestUser()), eq(1L), anyBoolean(), anyBoolean(),
                isNull(), isNull(), eq(true))
        ).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products")
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                .header("with-price-history", true)
                .header("only-active", true)
                .header("monitor-price-changes", true)
                .header("shop-id", 1)
        ).andReturn();

        ArrayList<ProductResponse> resultProducts = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, ProductResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(resultProducts, containsInAnyOrder(products.toArray())),
                () -> verify(productService, times(1))
                        .getUserProductsInShop(
                                eq(testConfig.getTestUser()),
                                anyLong(),
                                anyBoolean(),
                                anyBoolean(),
                                isNull(),
                                isNull(),
                                anyBoolean()
                        )
        );
    }

    @Test
    void getUserProducts_discountAndpriceChangesAndShopId_listOfProducts() throws Exception {
        List<ProductResponse> products = List.of(
                ProductResponse.builder().id(0L).build(),
                ProductResponse.builder().id(1L).build(),
                ProductResponse.builder().id(2L).build()
        );

        when(productService.getUserProductsInShop(
                any(), anyLong(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(Collections.emptyList());
        when(productService.getUserProductsInShop(
                eq(testConfig.getTestUser()), eq(1L), anyBoolean(), anyBoolean(),
                isNull(), eq(true), eq(true))
        ).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products")
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                .header("with-price-history", true)
                .header("only-active", true)
                .header("monitor-discount", true)
                .header("monitor-price-changes", true)
                .header("shop-id", 1)
        ).andReturn();

        ArrayList<ProductResponse> resultProducts = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, ProductResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(resultProducts, containsInAnyOrder(products.toArray())),
                () -> verify(productService, times(1))
                        .getUserProductsInShop(
                                eq(testConfig.getTestUser()),
                                anyLong(),
                                anyBoolean(),
                                anyBoolean(),
                                isNull(),
                                anyBoolean(),
                                anyBoolean()
                        )
        );
    }

    @Test
    void getUserProducts_invalidJwt_http401() throws Exception {
        List<ProductResponse> products = List.of(
                ProductResponse.builder().id(0L).build(),
                ProductResponse.builder().id(1L).build(),
                ProductResponse.builder().id(2L).build()
        );

        when(productService.getUserProducts(
                any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(Collections.emptyList());
        when(productService.getUserProducts(
                eq(testConfig.getTestUser()), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())
        ).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products")
                .header(authHeaderName, "wrong jwt")
                .header("with-price-history", true)
        ).andReturn();

        assertAll(
                () -> assertEquals(401, result.getResponse().getStatus()),
                () -> verify(productService, times(0))
                        .getUserProducts(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean()
                        )
        );
    }

    @Test
    void getUserProducts_withoutPriceHistoryHeader_400() throws Exception {
        List<ProductResponse> products = List.of(
                ProductResponse.builder().id(0L).build(),
                ProductResponse.builder().id(1L).build(),
                ProductResponse.builder().id(2L).build()
        );

        MvcResult result = mvc.perform(get("/api/products")
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
        ).andReturn();

        assertAll(
                () -> assertEquals(400, result.getResponse().getStatus()),
                () -> verify(productService, times(0))
                        .getUserProducts(
                                eq(testConfig.getTestUser()),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean()
                        ),
                () -> verify(productService, times(0))
                        .getUserProductsInShop(
                                eq(testConfig.getTestUser()),
                                anyLong(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean(),
                                anyBoolean()
                        )
        );
    }

    @Test
    void getProduct_validJwt_http200() throws Exception {
        ProductResponse expectedProductResponse = ProductResponse.builder().id(1L).build();
        when(productService.getProduct(anyLong(), anyBoolean())).thenReturn(expectedProductResponse);

        MvcResult result = mvc.perform(get("/api/product/" + expectedProductResponse.getId())
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                .header("with-price-history", true)
        ).andReturn();

        ProductResponse returnedProductResponse =
                mapper.readValue(result.getResponse().getContentAsString(), ProductResponse.class);

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertEquals(expectedProductResponse, returnedProductResponse),
                () -> verify(productService, times(1))
                        .getProduct(expectedProductResponse.getId(), true)
        );
    }

    @Test
    void getProduct_invalidJwt_http401() throws Exception {
        ProductResponse expectedProductResponse = ProductResponse.builder().id(1L).build();
        when(productService.getProduct(anyLong(), anyBoolean())).thenReturn(expectedProductResponse);

        MvcResult result = mvc.perform(get("/api/product/" + expectedProductResponse.getId())
                .header(authHeaderName, "invalid jwt")
                .header("with-price-history", true)
        ).andReturn();

        assertAll(
                () -> assertEquals(401, result.getResponse().getStatus()),
                () -> verify(productService, times(0)).getProduct(anyLong(), anyBoolean())
        );
    }

    @Test
    void getProduct_withoutPriceHistoryHeader_http400() throws Exception {
        ProductResponse expectedProductResponse = ProductResponse.builder().id(1L).build();
        when(productService.getProduct(anyLong(), anyBoolean())).thenReturn(expectedProductResponse);

        MvcResult result = mvc.perform(get("/api/product/" + expectedProductResponse.getId())
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
        ).andReturn();

        assertAll(
                () -> assertEquals(400, result.getResponse().getStatus()),
                () -> verify(productService, times(0)).getProduct(anyLong(), anyBoolean())
        );
    }
}