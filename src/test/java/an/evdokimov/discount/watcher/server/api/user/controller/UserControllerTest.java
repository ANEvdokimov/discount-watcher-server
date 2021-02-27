package an.evdokimov.discount.watcher.server.api.user.controller;

import an.evdokimov.discount.watcher.server.api.session.dto.response.LogInDtoResponse;
import an.evdokimov.discount.watcher.server.api.user.dto.request.RegisterDtoRequest;
import an.evdokimov.discount.watcher.server.api.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest
class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Test
    void register_validDto_logInDtoResponse() throws Exception {
        RegisterDtoRequest newUser = RegisterDtoRequest.builder()
                .login("new user")
                .password("pass")
                .name("name")
                .build();

        LogInDtoResponse response = LogInDtoResponse.builder()
                .token("token")
                .build();
        when(userService.register(newUser)).thenReturn(response);

        MvcResult result = mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser))).andReturn();

        assertAll(
                () -> assertEquals(result.getResponse().getStatus(), 200),
                () -> assertEquals(
                        mapper.readValue(result.getResponse().getContentAsString(), LogInDtoResponse.class),
                        response),
                () -> verify(userService, times(1)).register(newUser)
        );
    }

    @Test
    void register_nullLogin_http400() throws Exception {
        RegisterDtoRequest newUser = RegisterDtoRequest.builder()
                .password("pass")
                .name("name")
                .build();

        MvcResult result = mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser))).andReturn();

        assertAll(
                () -> assertEquals(result.getResponse().getStatus(), 400),
                () -> verify(userService, times(0)).register(newUser)
        );
    }

    @Test
    void register_emptyLogin_http400() throws Exception {
        RegisterDtoRequest newUser = RegisterDtoRequest.builder()
                .login("")
                .password("pass")
                .name("name")
                .build();

        MvcResult result = mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser))).andReturn();

        assertAll(
                () -> assertEquals(result.getResponse().getStatus(), 400),
                () -> verify(userService, times(0)).register(newUser)
        );
    }

    @Test
    void register_nullName_http400() throws Exception {
        RegisterDtoRequest newUser = RegisterDtoRequest.builder()
                .login("login")
                .password("pass")
                .build();

        MvcResult result = mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser))).andReturn();

        assertAll(
                () -> assertEquals(result.getResponse().getStatus(), 400),
                () -> verify(userService, times(0)).register(newUser)
        );
    }

    @Test
    void register_emptyName_http400() throws Exception {
        RegisterDtoRequest newUser = RegisterDtoRequest.builder()
                .login("login")
                .password("pass")
                .name("")
                .build();

        MvcResult result = mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser))).andReturn();

        assertAll(
                () -> assertEquals(result.getResponse().getStatus(), 400),
                () -> verify(userService, times(0)).register(newUser)
        );
    }

    @Test
    void register_nullPassword_http400() throws Exception {
        RegisterDtoRequest newUser = RegisterDtoRequest.builder()
                .login("login")
                .name("name")
                .build();

        MvcResult result = mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser))).andReturn();

        assertAll(
                () -> assertEquals(result.getResponse().getStatus(), 400),
                () -> verify(userService, times(0)).register(newUser)
        );
    }

    @Test
    void register_emptyPassword_http400() throws Exception {
        RegisterDtoRequest newUser = RegisterDtoRequest.builder()
                .login("login")
                .password("")
                .name("name")
                .build();

        MvcResult result = mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newUser))).andReturn();

        assertAll(
                () -> assertEquals(result.getResponse().getStatus(), 400),
                () -> verify(userService, times(0)).register(newUser)
        );
    }
}