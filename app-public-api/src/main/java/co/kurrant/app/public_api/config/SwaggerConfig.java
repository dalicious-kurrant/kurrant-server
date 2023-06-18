package co.kurrant.app.public_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI kurrantAppApi(){

        String jwtSchemeName = "AUTH-TOKEN";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        Components components = new Components().addSecuritySchemes(jwtSchemeName,
                new SecurityScheme()
                        .name(jwtSchemeName)
                        .in(SecurityScheme.In.HEADER)
                        .type(SecurityScheme.Type.APIKEY));

        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info().title("커런트 앱 Api 명세서")
                        .description("커런트 앱 api 명세서입니다.")
                        .version("v1")
                        .license(new License().name("SpringDoc 공식문서").url("http://springdoc.org")))
                .addSecurityItem(securityRequirement)
                .components(components);
    }

}
