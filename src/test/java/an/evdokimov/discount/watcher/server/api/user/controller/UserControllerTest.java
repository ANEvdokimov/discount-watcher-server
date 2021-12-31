package an.evdokimov.discount.watcher.server.api.user.controller;

import an.evdokimov.discount.watcher.server.api.TestConfig;
import an.evdokimov.discount.watcher.server.api.user.dto.request.LoginRequest;
import an.evdokimov.discount.watcher.server.api.user.dto.request.RegisterRequest;
import an.evdokimov.discount.watcher.server.api.user.dto.response.LoginResponse;
import an.evdokimov.discount.watcher.server.security.JwtUtils;
import an.evdokimov.discount.watcher.server.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@Import(TestConfig.class)
class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void register_validDto_logInDtoResponse() throws Exception {
        RegisterRequest newUser = RegisterRequest.builder()
                .login("new user")
                .password("pass")
                .name("name")
                .build();

        LoginResponse response = LoginResponse.builder()
                .token("token")
                .build();
        when(userService.register(newUser)).thenReturn(response);

        MvcResult result = mvc.perform(post("/api/users/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser))).andReturn();

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertEquals(
                        mapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class),
                        response),
                () -> verify(userService, times(1)).register(newUser)
        );
    }

    @Test
    void register_nullLogin_http400() throws Exception {
        RegisterRequest newUser = RegisterRequest.builder()
                .password("pass")
                .name("name")
                .build();

        MvcResult result = mvc.perform(post("/api/users/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser))).andReturn();

        assertAll(
                () -> assertEquals(400, result.getResponse().getStatus()),
                () -> verify(userService, times(0)).register(newUser)
        );
    }

    @Test
    void register_emptyLogin_http400() throws Exception {
        RegisterRequest newUser = RegisterRequest.builder()
                .login("")
                .password("pass")
                .name("name")
                .build();

        MvcResult result = mvc.perform(post("/api/users/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser))).andReturn();

        assertAll(
                () -> assertEquals(400, result.getResponse().getStatus()),
                () -> verify(userService, times(0)).register(newUser)
        );
    }

    @Test
    void register_nullName_http400() throws Exception {
        RegisterRequest newUser = RegisterRequest.builder()
                .login("login")
                .password("pass")
                .build();

        MvcResult result = mvc.perform(post("/api/users/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser))).andReturn();

        assertAll(
                () -> assertEquals(400, result.getResponse().getStatus()),
                () -> verify(userService, times(0)).register(newUser)
        );
    }

    @Test
    void register_emptyName_http400() throws Exception {
        RegisterRequest newUser = RegisterRequest.builder()
                .login("login")
                .password("pass")
                .name("")
                .build();

        MvcResult result = mvc.perform(post("/api/users/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser))).andReturn();

        assertAll(
                () -> assertEquals(400, result.getResponse().getStatus()),
                () -> verify(userService, times(0)).register(newUser)
        );
    }

    @Test
    void register_nullPassword_http400() throws Exception {
        RegisterRequest newUser = RegisterRequest.builder()
                .login("login")
                .name("name")
                .build();

        MvcResult result = mvc.perform(post("/api/users/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser))).andReturn();

        assertAll(
                () -> assertEquals(400, result.getResponse().getStatus()),
                () -> verify(userService, times(0)).register(newUser)
        );
    }

    @Test
    void register_emptyPassword_http400() throws Exception {
        RegisterRequest newUser = RegisterRequest.builder()
                .login("login")
                .password("")
                .name("name")
                .build();

        MvcResult result = mvc.perform(post("/api/users/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser))).andReturn();

        assertAll(
                () -> assertEquals(400, result.getResponse().getStatus()),
                () -> verify(userService, times(0)).register(newUser)
        );
    }

    @Test
    void login_validRequest_http200() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .login("user")
                .password("pass")
                .build();

        LoginResponse response = new LoginResponse("token");

        when(userService.login(request)).thenReturn(response);

        MvcResult result = mvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andReturn();

        assertAll(
                () -> assertEquals(200, result.getResponse().getStatus()),
                () -> assertEquals(
                        response.getToken(),
                        mapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class).getToken()
                ),
                () -> verify(userService, times(1)).login(request)
        );
    }

    @Test
    void login_nullPassword_http400() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .login("user")
                .build();

        MvcResult result = mvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andReturn();

        assertAll(
                () -> assertEquals(400, result.getResponse().getStatus()),
                () -> verify(userService, times(0)).login(request)
        );
    }

    @Test
    void login_blankPassword_http400() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .login("user")
                .password("")
                .build();

        MvcResult result = mvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andReturn();

        assertAll(
                () -> assertEquals(400, result.getResponse().getStatus()),
                () -> verify(userService, times(0)).login(request)
        );
    }

    @Test
    void login_nullLogin_http400() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .password("pass")
                .build();

        MvcResult result = mvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andReturn();

        assertAll(
                () -> assertEquals(400, result.getResponse().getStatus()),
                () -> verify(userService, times(0)).login(request)
        );
    }

    @Test
    void login_blankLogin_http400() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .login("")
                .password("pass")
                .build();

        MvcResult result = mvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andReturn();

        assertAll(
                () -> assertEquals(400, result.getResponse().getStatus()),
                () -> verify(userService, times(0)).login(request)
        );
    }
}