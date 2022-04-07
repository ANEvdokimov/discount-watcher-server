package an.evdokimov.discount.watcher.server.configuration;

import an.evdokimov.discount.watcher.server.security.JwtAuthenticationFilter;
import an.evdokimov.discount.watcher.server.security.JwtAuthenticationProvider;
import an.evdokimov.discount.watcher.server.security.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final JwtUtils jwtUtils;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    @Value("${springdoc.api-docs.path}")
    private String docsUrl;
    @Value("${springdoc.swagger-ui.path}")
    private String swaggerUiUrl;

    public SecurityConfiguration(JwtUtils jwtUtils, JwtAuthenticationProvider jwtAuthenticationProvider) {
        this.jwtUtils = jwtUtils;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/users/registration").permitAll()
                .antMatchers("/api/users/login").permitAll()
                .antMatchers(docsUrl + "/**").permitAll()
                .antMatchers(swaggerUiUrl + "/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(tokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(jwtAuthenticationProvider);
    }

    @Bean
    public JwtAuthenticationFilter tokenFilter() {
        return new JwtAuthenticationFilter(
                "/**",
                List.of("/api/users/registration", "/api/users/login", docsUrl + "/**", swaggerUiUrl + "/**"),
                jwtUtils);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
