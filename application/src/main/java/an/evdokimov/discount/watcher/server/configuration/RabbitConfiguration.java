package an.evdokimov.discount.watcher.server.configuration;

import an.evdokimov.discount.watcher.server.configuration.property.RabbitProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitConfiguration {
    private final RabbitProperties properties;

    @Bean
    public Queue inputQueue() {
        return new Queue(properties.getInputQueueName());
    }

    @Bean
    public Queue outputQueue() {
        return new Queue(properties.getOutputQueueName());
    }

    @Bean
    public Queue outputErrorQueue() {
        return new Queue(properties.getOutputErrorQueueName());
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
