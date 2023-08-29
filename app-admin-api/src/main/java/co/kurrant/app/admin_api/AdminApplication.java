package co.kurrant.app.admin_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EntityScan(basePackages = {"co.dalicious.*", "co.kurrant.app.public_api"})
@EnableJpaRepositories(basePackages = {"co.dalicious.*", "co.kurrant.app.admin_api"})
@SpringBootApplication(scanBasePackages = {"co.dalicious.*", "co.kurrant.app.admin_api"})
@EnableScheduling
@EnableAsync
public class AdminApplication {

  public static void main(String[] args) {
    SpringApplication.run(AdminApplication.class, args);
  }
}
