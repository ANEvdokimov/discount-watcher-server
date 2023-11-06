package an.evdokimov.discount.watcher.server.api;


import an.evdokimov.discount.watcher.server.security.user.model.User;
import an.evdokimov.discount.watcher.server.security.user.model.UserRole;
import an.evdokimov.discount.watcher.server.security.user.service.UserService;
import lombok.Getter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestConfig {
    private final UserService userService = mock(UserService.class);
    @Getter
    private final User testUser;

    public TestConfig() {
        testUser = User.builder()
                .login("test_user")
                .role(UserRole.ROLE_USER)
                .build();

        when(userService.getOrCreate("test_user")).thenReturn(testUser);
    }

    @Bean
    public UserService userService() {
        return userService;
    }
}
