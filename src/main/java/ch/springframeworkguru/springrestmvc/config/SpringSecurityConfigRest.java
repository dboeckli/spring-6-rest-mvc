package ch.springframeworkguru.springrestmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfigRest {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(httpSecurityCsrfConfigurer -> {
            httpSecurityCsrfConfigurer.ignoringRequestMatchers("/api/**");
        });
        // deprecated: http.csrf().ignoringRequestMatchers("/api/**");
        return http.build();
    }
}
