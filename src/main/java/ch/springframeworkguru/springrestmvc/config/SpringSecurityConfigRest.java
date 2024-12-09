package ch.springframeworkguru.springrestmvc.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("!test-disabled-security")
public class SpringSecurityConfigRest {

    /* http basic auth ./
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeRequests -> 
                authorizeRequests.anyRequest().authenticated())
            
            .httpBasic(Customizer.withDefaults())
            
            .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.ignoringRequestMatchers("/api/**"));
        return http.build();
    }*/


    @Bean
    @Order(99)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests -> {
                authorizeRequests
                    .requestMatchers(
                        "/v3/api-docs**", 
                        "/v3/api-docs/**", 
                        "/swagger-ui/**",  
                        "/swagger-ui.html", 
                        "/h2-console/**")
                    .permitAll() // permit all swagger/openapi endpoints
                    //.requestMatchers("/actuator/**").permitAll()
                    .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()  // permit all actuator endpoints
                    .anyRequest().authenticated();
            })
            .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**") // CSRF-Schutz für H2-Console deaktivieren
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable()) // Neue Syntax für Frame-Optionen
            );
        return http.build();
    }
}
