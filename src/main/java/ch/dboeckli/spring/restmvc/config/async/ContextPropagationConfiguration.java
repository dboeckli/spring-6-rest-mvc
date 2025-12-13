package ch.dboeckli.spring.restmvc.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;

/*
The ContextPropagatingTaskDecorator uses Micrometer's Context Propagation API to make sure that the
trace context is transferred to new threads.
See: https://spring.io/blog/2025/11/18/opentelemetry-with-spring-boot
 */
@Configuration(proxyBeanMethods = false)
public class ContextPropagationConfiguration {

    @Bean
    ContextPropagatingTaskDecorator contextPropagatingTaskDecorator() {
        return new ContextPropagatingTaskDecorator();
    }

}
