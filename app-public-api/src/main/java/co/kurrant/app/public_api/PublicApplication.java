package co.kurrant.app.public_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EntityScan(basePackages = {"co.dalicious.*", "co.kurrant.app.public_api"})
@EnableJpaRepositories(basePackages = {"co.dalicious.*", "co.kurrant.app.public_api"})
@SpringBootApplication(scanBasePackages = {"co.dalicious.*", "co.kurrant.app.public_api"})
@EnableAsync
public class PublicApplication {
  public static void main(String[] args) {
    SpringApplication.run(PublicApplication.class, args);
  }
}
