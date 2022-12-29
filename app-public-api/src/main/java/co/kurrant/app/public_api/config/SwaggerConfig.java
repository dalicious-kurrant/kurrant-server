package co.kurrant.app.public_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class SwaggerConfig {
  @Bean
  public Docket api() {
    return new Docket(DocumentationType.OAS_30)
            .groupName("Kurrant_V1")
            .consumes(getConsumeContentTypes())
            .produces(getProduceContentTypes())
            .apiInfo(new ApiInfo("Kurrant App API","Dalicious Kurrant App Analyzer","1.0","","","",""))
            .select()
            .apis(RequestHandlerSelectors.basePackage("app-public-api"))
            .paths(PathSelectors.any())
            .build();
  }

  private Set<String> getConsumeContentTypes() {
    Set<String> consumes = new HashSet<>();
    consumes.add("application/json;charset=UTF-8");
    consumes.add("application/x-www-form-urlencoded");
    return consumes;
  }

  private Set<String> getProduceContentTypes() {
    Set<String> produces = new HashSet<>();
    produces.add("application/json;charset=UTF-8");
    return produces;
  }

}
