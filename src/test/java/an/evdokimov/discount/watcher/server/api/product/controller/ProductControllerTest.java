package an.evdokimov.discount.watcher.server.api.product.controller;

import an.evdokimov.discount.watcher.server.api.TestConfig;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.LentaProductPriceResponse;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.security.JwtUtils;
import an.evdokimov.discount.watcher.server.service.product.ProductService;
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
@Import(TestConfig.class)
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
    private ProductService productService;

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

        when(productService.addProduct(any())).thenReturn(productResponse);

        NewProductRequest request = NewProductRequest.builder()
                .shopId(1L)
                .url(new URL("https://test_url.com"))
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
                () -> verify(productService, times(1)).addProduct(eq(request))
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
                () -> verify(productService, times(0)).addProduct(eq(request))
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
                () -> verify(productService, times(0)).addProduct(eq(request))
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
                () -> verify(productService, times(0)).addProduct(eq(request))
        );
    }

    @Test
    void getUserProducts_validJwt_listOfProducts() throws Exception {
        List<ProductResponse> products = List.of(
                ProductResponse.builder().id(0L).build(),
                ProductResponse.builder().id(1L).build(),
                ProductResponse.builder().id(2L).build()
        );

        when(productService.getUserProducts(any(), anyBoolean())).thenReturn(Collections.emptyList());
        when(productService.getUserProducts(eq(testConfig.getTestUser()), anyBoolean())).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products")
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                .header("with_price_history", true)
        ).andReturn();

        ArrayList<ProductResponse> resultProducts = mapper.readValue(
                result.getResponse().getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, ProductResponse.class)
        );

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertThat(resultProducts, containsInAnyOrder(products.toArray())),
                () -> verify(productService, times(1))
                        .getUserProducts(eq(testConfig.getTestUser()), anyBoolean())
        );
    }

    @Test
    void getUserProducts_invalidJwt_http401() throws Exception {
        List<ProductResponse> products = List.of(
                ProductResponse.builder().id(0L).build(),
                ProductResponse.builder().id(1L).build(),
                ProductResponse.builder().id(2L).build()
        );

        when(productService.getUserProducts(any(), anyBoolean())).thenReturn(Collections.emptyList());
        when(productService.getUserProducts(eq(testConfig.getTestUser()), anyBoolean())).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products")
                .header(authHeaderName, "wrong jwt")
                .header("with_price_history", true)
        ).andReturn();

        assertAll(
                () -> assertEquals(401, result.getResponse().getStatus()),
                () -> verify(productService, times(0))
                        .getUserProducts(eq(testConfig.getTestUser()), anyBoolean())
        );
    }

    @Test
    void getUserProducts_withoutWithPriceHistoryHeader_400() throws Exception {
        List<ProductResponse> products = List.of(
                ProductResponse.builder().id(0L).build(),
                ProductResponse.builder().id(1L).build(),
                ProductResponse.builder().id(2L).build()
        );

        when(productService.getUserProducts(any(), anyBoolean())).thenReturn(Collections.emptyList());
        when(productService.getUserProducts(eq(testConfig.getTestUser()), anyBoolean())).thenReturn(products);

        MvcResult result = mvc.perform(get("/api/products")
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
        ).andReturn();

        assertAll(
                () -> assertEquals(400, result.getResponse().getStatus()),
                () -> verify(productService, times(0))
                        .getUserProducts(eq(testConfig.getTestUser()), anyBoolean())
        );
    }

    @Test
    void getProduct_validJwt_http200() throws Exception {
        ProductResponse expectedProductResponse = ProductResponse.builder().id(1L).build();
        when(productService.getProduct(anyLong(), anyBoolean())).thenReturn(expectedProductResponse);

        MvcResult result = mvc.perform(get("/api/product/" + expectedProductResponse.getId())
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                .header("with_price_history", true)
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
                .header("with_price_history", true)
        ).andReturn();

        assertAll(
                () -> assertEquals(401, result.getResponse().getStatus()),
                () -> verify(productService, times(0)).getProduct(anyLong(), anyBoolean())
        );
    }

    @Test
    void getProduct_withoutWithPriceHistoryHeader_http400() throws Exception {
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