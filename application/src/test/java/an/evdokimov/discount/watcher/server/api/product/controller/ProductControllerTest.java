package an.evdokimov.discount.watcher.server.api.product.controller;

import an.evdokimov.discount.watcher.server.api.TestConfig;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.request.NewProductWithCookiesRequest;
import an.evdokimov.discount.watcher.server.api.product.dto.response.LentaProductPriceResponse;
import an.evdokimov.discount.watcher.server.api.product.dto.response.ProductResponse;
import an.evdokimov.discount.watcher.server.configuration.SecurityConfiguration;
import an.evdokimov.discount.watcher.server.service.product.ProductServiceImpl;
import an.evdokimov.securitystarter.security.authentication.jwt.JwtUtils;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductController.class)
@Import({TestConfig.class, SecurityConfiguration.class})
class ProductControllerTest {
    @Value("${application.security.header.authentication}")
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

        NewProductRequest request = NewProductRequest.builder()
                .shopId(1L)
                .url(new URL("https://test_url.com"))
                .monitorAvailability(false)
                .monitorDiscount(true)
                .monitorPriceChanges(false)
                .build();

        MvcResult result = mvc.perform(put("/api/products/add_by_shop_id")
                        .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(request)))
                .andReturn();

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
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

        MvcResult result = mvc.perform(put("/api/products/add_by_shop_id")
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

        MvcResult result = mvc.perform(put("/api/products/add_by_shop_id")
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

        MvcResult result = mvc.perform(put("/api/products/add_by_shop_id")
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
    void addProductByCookie_correctRequest_ProductResponse() throws Exception {
        LentaProductPriceResponse productPriceResponse = LentaProductPriceResponse.builder()
                .price(BigDecimal.valueOf(100))
                .priceWithCard(BigDecimal.valueOf(50))
                .priceWithDiscount(BigDecimal.valueOf(30))
                .build();

        NewProductWithCookiesRequest request = NewProductWithCookiesRequest.builder()
                .cookies("COOKIES!!!")
                .url(new URL("https://test_url.com"))
                .monitorAvailability(false)
                .monitorDiscount(true)
                .monitorPriceChanges(false)
                .build();

        MvcResult result = mvc.perform(put("/api/products/add_by_cookies")
                        .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(request)))
                .andReturn();

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> verify(productService, times(1))
                        .addProduct(eq(testConfig.getTestUser()), eq(request))
        );
    }

    @Test
    void addProductByCookie_nullUrl_http400() throws Exception {
        NewProductWithCookiesRequest request = NewProductWithCookiesRequest.builder()
                .cookies("COOKIES!!!")
                .url(null)
                .build();

        MvcResult result = mvc.perform(put("/api/products/add_by_cookies")
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
    void addProductByCookie_nullShopId_http400() throws Exception {
        NewProductWithCookiesRequest request = NewProductWithCookiesRequest.builder()
                .cookies("COOKIES!!!")
                .url(new URL("https://test_url.com"))
                .build();

        MvcResult result = mvc.perform(put("/api/products/add_by_cookies")
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
    void addProductByCookie_invalidJwt_http401() throws Exception {
        NewProductWithCookiesRequest request = NewProductWithCookiesRequest.builder()
                .cookies("COOKIES!!!")
                .url(new URL("https://test_url.com"))
                .build();

        MvcResult result = mvc.perform(put("/api/products/add_by_cookies")
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
    void getProduct_validJwt_http200() throws Exception {
        ProductResponse expectedProductResponse = ProductResponse.builder().id(1L).build();
        when(productService.getProduct(anyLong())).thenReturn(expectedProductResponse);

        MvcResult result = mvc.perform(get("/api/product/" + expectedProductResponse.getId())
                .header(authHeaderName, "Bearer " + jwtUtils.generateToken("test_user"))
        ).andReturn();

        ProductResponse returnedProductResponse =
                mapper.readValue(result.getResponse().getContentAsString(), ProductResponse.class);

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertEquals(expectedProductResponse, returnedProductResponse),
                () -> verify(productService, times(1))
                        .getProduct(expectedProductResponse.getId())
        );
    }

    @Test
    void getProduct_invalidJwt_http401() throws Exception {
        ProductResponse expectedProductResponse = ProductResponse.builder().id(1L).build();
        when(productService.getProduct(anyLong())).thenReturn(expectedProductResponse);

        MvcResult result = mvc.perform(get("/api/product/" + expectedProductResponse.getId())
                .header(authHeaderName, "invalid jwt")
        ).andReturn();

        assertAll(
                () -> assertEquals(401, result.getResponse().getStatus()),
                () -> verify(productService, times(0)).getProduct(anyLong())
        );
    }
}