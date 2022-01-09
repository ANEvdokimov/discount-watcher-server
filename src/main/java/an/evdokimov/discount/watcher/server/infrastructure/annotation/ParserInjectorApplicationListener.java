package an.evdokimov.discount.watcher.server.infrastructure.annotation;

import an.evdokimov.discount.watcher.server.parser.Parser;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ParserInjectorApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    private final ParserInjectorBeanPostProcessor parserInjectorBeanPostProcessor;
    private final Map<String, Parser> parsers;

    public ParserInjectorApplicationListener(ParserInjectorBeanPostProcessor parserInjectorBeanPostProcessor,
                                             Set<Parser> parsers) {
        this.parserInjectorBeanPostProcessor = parserInjectorBeanPostProcessor;
        this.parsers = parsers.stream().collect(Collectors.toMap(Parser::getSupportedUrl, Function.identity()));
    }

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent contextRefreshedEvent) {
        for (Map.Entry<String, Object> entry : parserInjectorBeanPostProcessor.getObjectsWithAnnotation().entrySet()) {
            Object oldBean = entry.getValue();
            for (Field field : oldBean.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(AutowiredParser.class)) {
                    String domainName = field.getAnnotation(AutowiredParser.class).domainName();
                    Parser parser = parsers.get(domainName.toLowerCase(Locale.ENGLISH));
                    Object newBean = contextRefreshedEvent.getApplicationContext().getBean(entry.getKey());
                    field.setAccessible(true);
                    try {
                        field.set(newBean, parser);
                    } catch (IllegalAccessException e) {
                        //TODO handle exp
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
