package ch.springframeworkguru.springrestmvc.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
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
public class OpenApiConfiguration {

    public final static String SECURITY_SCHEME_NAME = "Bearer Authentication";

}
