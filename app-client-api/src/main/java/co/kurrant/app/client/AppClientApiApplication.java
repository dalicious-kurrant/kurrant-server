package co.kurrant.app.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"co.dalicious.*", "co.kurrant.app.client"})
@EnableJpaRepositories(basePackages = {"co.dalicious.*", "co.kurrant.app.client"})
@SpringBootApplication(scanBasePackages = {"co.dalicious.*", "co.kurrant.app.client"})
public class AppClientApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(AppClientApiApplication.class, args);
  }

}
