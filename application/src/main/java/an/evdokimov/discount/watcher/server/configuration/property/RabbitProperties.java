package an.evdokimov.discount.watcher.server.configuration.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("application.mq")
public class RabbitProperties {
    /**
     * A queue in which are product for parsing.
     */
    private String inputQueueName;

    /**
     * A queue for filled product information after parsing.
     */
    private String outputQueueName;

    /**
     * A queue for errors during parsing product.
     */
    private String outputErrorQueueName;
}
