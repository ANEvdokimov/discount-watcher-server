package an.evdokimov.discount.watcher.server.api;

import an.evdokimov.discount.watcher.server.database.user.model.User;
import an.evdokimov.discount.watcher.server.database.user.model.UserRole;
import an.evdokimov.discount.watcher.server.database.user.repository.UserRepository;
import an.evdokimov.discount.watcher.server.security.JwtAuthenticationProvider;
import an.evdokimov.discount.watcher.server.security.JwtUtils;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestConfig {
    private final UserRepository userRepository;

    public TestConfig() {
        userRepository = mock(UserRepository.class);

        User testUser = User.builder()
                .login("test_user")
                .password("pass")
                .name("test_user")
                .role(UserRole.ROLE_USER)
                .build();
        when(userRepository.findByLogin("test_user")).thenReturn(Optional.of(testUser));
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(userRepository);
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwtUtils());
    }
}
