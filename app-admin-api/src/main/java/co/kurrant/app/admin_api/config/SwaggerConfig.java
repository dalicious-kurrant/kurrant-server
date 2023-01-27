package co.kurrant.app.admin_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import java.util.List;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {
  @Bean
  public GroupedOpenApi oasApi() {
    return GroupedOpenApi.builder().group("springshop-public").pathsToMatch("/v1/**").build();
  }

  @Bean
  public OpenAPI springShopOpenAPI() {
    SecurityScheme auth = new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer");
    SecurityRequirement m = new SecurityRequirement().addList("bearerAuth");
    return new OpenAPI().components(new Components().addSecuritySchemes("bearerAuth", auth))
            .addSecurityItem(m)
        .info(new Info().title("SpringShop API").description("Spring shop sample application")
            .version("v0.0.1")
            .license(new License().name("Apache 2.0").url("http://springdoc.org")))
        .externalDocs(new ExternalDocumentation().description("SpringShop Wiki Documentation")
            .url("https://springshop.wiki.github.org/docs"));
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    argumentResolvers.add(new PageableHandlerMethodArgumentResolver());
  }
}
