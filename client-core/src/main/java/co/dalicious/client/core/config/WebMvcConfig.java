package co.dalicious.client.core.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableArgumentResolver;
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

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PATCH", "DELETE","OPTIONS")
            .allowCredentials(true)
            .maxAge(60);
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    PageableArgumentResolver pageableResolver = new CorrettoPageableHandlerMethodArgumentResolver(
        new CorrettoSortHandlerMethodArgumentResolver());

    argumentResolvers.add(pageableResolver);
  }
}
