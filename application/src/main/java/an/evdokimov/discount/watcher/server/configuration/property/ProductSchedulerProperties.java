package an.evdokimov.discount.watcher.server.configuration.property;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("application.scheduler.product")
@Component
public class ProductSchedulerProperties {
    /**
     * Cron for an update-product task.
     * Time should be in UTC zone.
     */
    @NotBlank
    private String update;
}
