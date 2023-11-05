package an.evdokimov.discount.watcher.server.configuration;

import an.evdokimov.discount.watcher.server.security.filter.UserLoginExtractorAuthenticationProcessingFilter;
import an.evdokimov.discount.watcher.server.security.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        UserLoginExtractorAuthenticationProcessingFilter filter =
                new UserLoginExtractorAuthenticationProcessingFilter(userService);

        return httpSecurity
                .securityMatcher("/**")
                .authorizeHttpRequests(configurer -> configurer.anyRequest().authenticated())
                .addFilterAt(filter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .build();
    }
}
