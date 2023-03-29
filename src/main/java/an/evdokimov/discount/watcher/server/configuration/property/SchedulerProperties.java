package an.evdokimov.discount.watcher.server.configuration.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.schedule.product-update")
@Getter
@Setter
public class SchedulerProperties {
    /**
     * Minimum delay between updating products in milliseconds.
     */
    private Long minDelay;

    /**
     * Maximum delay between updating products in milliseconds.
     */
    private Long maxDelay;
}
