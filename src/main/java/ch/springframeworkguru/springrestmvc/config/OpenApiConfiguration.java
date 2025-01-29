package ch.springframeworkguru.springrestmvc.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static ch.springframeworkguru.springrestmvc.config.OpenApiConfiguration.SECURITY_SCHEME_NAME;

@OpenAPIDefinition(
    info = @Info(
        title = "Spring Rest MVC API",
        description = "Some long and useful description",
        version = "TODO",
        license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
    )
)
@SecurityScheme(
    name = SECURITY_SCHEME_NAME,
    type = SecuritySchemeType.OAUTH2,
    bearerFormat = "JWT",
    scheme = "bearer",
    flows = @OAuthFlows(
        clientCredentials = @OAuthFlow(
            authorizationUrl = "http://localhost:9000/oauth2/auth",
            tokenUrl = "http://localhost:9000/oauth2/token",
            refreshUrl = "http://localhost:9000/oauth2/refresh-token",
            scopes = {
                @OAuthScope(name = "message.read"),
                @OAuthScope(name = "message.write")
            })
    )
)
@Configuration
@RequiredArgsConstructor
public class OpenApiConfiguration {

    private final BuildProperties buildProperties;

    @Value("${security.authorization-url:http://localhost:9000/oauth2/auth}")
    private String authorizationUrl;
    @Value("${security.token-url:http://localhost:9000/oauth2/token}")
    private String tokenUrl;
    @Value("${security.refresh-url:http://localhost:9000/oauth2/refresh-token}")
    private String refreshUrl;

    public final static String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenApiCustomizer customerGlobalHeaderOpenApiCustomizer() {
        return openApi -> {
            io.swagger.v3.oas.models.info.Info info = openApi.getInfo();
            info.setTitle(buildProperties.getName());
            info.setVersion(buildProperties.getVersion());

            // Update OAuth URLs
            openApi.getComponents().getSecuritySchemes().values().stream()
                .filter(scheme -> scheme.getType() == io.swagger.v3.oas.models.security.SecurityScheme.Type.OAUTH2)
                .forEach(scheme -> {
                    io.swagger.v3.oas.models.security.OAuthFlows flows = scheme.getFlows();
                    if (flows.getClientCredentials() != null) {
                        flows.getClientCredentials()
                            .authorizationUrl(authorizationUrl)
                            .tokenUrl(tokenUrl)
                            .refreshUrl(refreshUrl);
                    }
                });
        };
    }
}
