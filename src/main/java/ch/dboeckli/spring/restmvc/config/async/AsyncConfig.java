package ch.dboeckli.spring.restmvc.config.async;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // NICHTS weiter!
    // Spring Boot erstellt automatisch den Async Executor
}