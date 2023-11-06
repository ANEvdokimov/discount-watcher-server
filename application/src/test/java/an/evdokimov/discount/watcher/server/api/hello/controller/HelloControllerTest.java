package an.evdokimov.discount.watcher.server.api.hello.controller;

import an.evdokimov.discount.watcher.server.api.TestConfig;
import an.evdokimov.discount.watcher.server.configuration.SecurityConfiguration;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(HelloController.class)
@Import({TestConfig.class, SecurityConfiguration.class})
class HelloControllerTest {
    private static final String AUTH_HEADER_NAME = "Authenticated-User";
    private static final String AUTH_USER = "test_user";

    @Autowired
    private MockMvc mvc;

    @SneakyThrows
    @Test
    @DisplayName("Hello-request with authenticated user")
    public void hello_authenticatedUser_http200() {
        mvc.perform(get("/api/hello")
                        .header(AUTH_HEADER_NAME, AUTH_USER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"hello\": \"hello!\"}"));
    }

    @SneakyThrows
    @Test
    @DisplayName("Hello-request without user")
    public void hello_notAuthenticatedUser_http200() {
        mvc.perform(get("/api/hello"))
                .andExpect(status().isUnauthorized());
    }
}