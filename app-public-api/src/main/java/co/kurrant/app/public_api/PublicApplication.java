package co.kurrant.app.public_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"co.dalicious.*", "co.kurrant.app.public_api"})
@EnableJpaRepositories(basePackages = {"co.dalicious.*", "co.kurrant.app.public_api"})
@SpringBootApplication(scanBasePackages = {"co.dalicious.*", "co.kurrant.app.public_api"})
public class PublicApplication {
  public static void main(String[] args) {
    SpringApplication.run(PublicApplication.class, args);
  }
}
