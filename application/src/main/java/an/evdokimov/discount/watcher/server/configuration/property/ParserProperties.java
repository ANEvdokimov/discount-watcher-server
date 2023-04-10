package an.evdokimov.discount.watcher.server.configuration.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application.parser")
@Getter
@Setter
public class ParserProperties {
    /**
     * User-Agent header in requests to lenta.com
     */
    private String lentaUserAgentHeader;

    /**
     * Referer header in request to lenta.com
     */
    private String lentaRefererHeader;
}
