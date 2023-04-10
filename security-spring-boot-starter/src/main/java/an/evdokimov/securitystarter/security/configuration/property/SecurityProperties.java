package an.evdokimov.securitystarter.security.configuration.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application.security")
@Getter
@Setter
public class SecurityProperties {
    @ConfigurationProperties("jwt")
    @Getter
    @Setter
    public static class JwtProperties {
        /**
         * A secret key for encryption an JWT.
         */
        private String secret;
        /**
         * An JWT lifetime.
         */
        private Long expirationTime;
    }

    @ConfigurationProperties("http.header")
    @Getter
    @Setter
    public static class HttpHeaderProperties {
        /**
         * A name of the header a JWT is in.
         */
        private String authentication;
    }
}
