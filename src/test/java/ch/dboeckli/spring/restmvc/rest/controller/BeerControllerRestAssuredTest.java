package ch.dboeckli.spring.restmvc.rest.controller;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.atlassian.oai.validator.whitelist.ValidationErrorsWhitelist;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import static com.atlassian.oai.validator.whitelist.rule.WhitelistRules.messageHasKey;
import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(BeerControllerRestAssuredTest.DisabledSecurityConfig.class)
@ComponentScan(basePackages = "ch.springframeworkguru.springrestmvc")
@ActiveProfiles("test-disabled-security")
@Disabled("rest-assured not supported in spring boot 4")
class BeerControllerRestAssuredTest {

    @LocalServerPort
    Integer localPort;

    OpenApiValidationFilter validationFilter = new OpenApiValidationFilter(OpenApiInteractionValidator
        .createForSpecificationUrl("openapi.json")
        .withWhitelist(ValidationErrorsWhitelist.create()
            // we get an validation error for date format, so we ignore it
            // TODO: instead of ignoring it, consider fixing the date format in the API specification
            .withRule("Ignore date format", messageHasKey("validation.response.body.schema.format.date-time"))
            .withRule("Ignore Instance type (null)", messageHasKey("validation.response.body.schema.type"))
        )
        .build());

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = localPort;
    }

    @Test
    void testValidateListBeers() {
        given().contentType(ContentType.JSON)
            .when()
            .filter(validationFilter)
            .get("/api/v1/beer/listBeers")
            .then()
            .assertThat().statusCode(200);
    }

    @Configuration
    public static class DisabledSecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) {
            http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
            return http.build();
        }
    }
}
