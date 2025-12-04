package ch.dboeckli.spring.restmvc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager(@Value("${appconfig.cache.enabled}") boolean cacheEnabled) {
        log.info("Cache config enabled: " + cacheEnabled);
        if (cacheEnabled) {
            log.info("Caching enabled");
            return new ConcurrentMapCacheManager();
        }
        log.info("Caching disabled");
        return new NoOpCacheManager();
    }
}
