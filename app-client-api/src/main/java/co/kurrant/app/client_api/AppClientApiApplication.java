package co.kurrant.app.client_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"co.dalicious.*", "co.kurrant.app.client_api"})
@EnableJpaRepositories(basePackages = {"co.dalicious.*", "co.kurrant.app.client_api"})
@SpringBootApplication(scanBasePackages = {"co.dalicious.*", "co.kurrant.app.client_api"})
public class AppClientApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(AppClientApiApplication.class, args);
  }

}
