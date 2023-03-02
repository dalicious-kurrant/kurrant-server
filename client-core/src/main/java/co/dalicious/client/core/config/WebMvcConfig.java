package co.dalicious.client.core.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import co.dalicious.client.core.resolver.CorrettoPageableHandlerMethodArgumentResolver;
import co.dalicious.client.core.resolver.CorrettoSortHandlerMethodArgumentResolver;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
  @Bean
  BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

//  @Override
//  public void addCorsMappings(CorsRegistry registry) {
//    registry.addMapping("/v1/**")
//            .allowedMethods("GET", "POST", "PATCH", "DELETE")
//            .allowedOrigins("http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost:3003", "http://kurrant.co");
//  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    PageableArgumentResolver pageableResolver = new CorrettoPageableHandlerMethodArgumentResolver(
        new CorrettoSortHandlerMethodArgumentResolver());

    argumentResolvers.add(pageableResolver);
  }
}
