package an.evdokimov.discount.watcher.server.infrastructure.annotation;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Component
public class ParserInjectorBeanPostProcessor implements BeanPostProcessor {
    @Getter
    private final Map<String, Object> objectsWithAnnotation;

    public ParserInjectorBeanPostProcessor() {
        objectsWithAnnotation = new HashMap<>();
    }

    @Override
    public Object postProcessBeforeInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(AutowiredParser.class)) {
                objectsWithAnnotation.put(beanName, bean);
                break;
            }
        }
        return bean;
    }
}
