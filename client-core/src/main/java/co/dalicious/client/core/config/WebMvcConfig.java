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
    // NIST 기준 125000, 512 bits다
//    Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder("powerpower", 300000, 512);
//    encoder.setAlgorithm(SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA512);
//    return encoder;
    // return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    return new BCryptPasswordEncoder();
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/v1")
            .allowedMethods("GET", "POST", "PATCH", "DELETE")
            .allowCredentials(true);
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    PageableArgumentResolver pageableResolver = new CorrettoPageableHandlerMethodArgumentResolver(
        new CorrettoSortHandlerMethodArgumentResolver());

    argumentResolvers.add(pageableResolver);
  }
}
