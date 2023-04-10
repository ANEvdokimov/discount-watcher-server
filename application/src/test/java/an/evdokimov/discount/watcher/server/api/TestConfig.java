package an.evdokimov.discount.watcher.server.api;

import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.database.user.model.UserRole;
import an.evdokimov.discount.watcher.server.service.user.UserService;
import an.evdokimov.securitystarter.security.authentication.jwt.JwtAuthenticationProvider;
import an.evdokimov.securitystarter.security.authentication.jwt.JwtUtils;
import lombok.Getter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestConfig {
    private final UserService userService;
    @Getter
    private final User testUser;

    public TestConfig() {
        userService = mock(UserService.class);

        testUser = User.builder()
                .login("test_user")
                .password("pass")
                .name("test_user")
                .role(UserRole.ROLE_USER)
                .build();
        when(userService.loadUserByUsername("test_user")).thenReturn(testUser);
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils();
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwtUtils(), userService);
    }
}
