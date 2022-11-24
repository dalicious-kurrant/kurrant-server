package co.kurrant.app.public_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = {"co.kurrant.*", "co.dalicious.*"})
@SpringBootApplication(scanBasePackages = {"co.kurrant.*", "co.dalicious.*"})
public class PublicApplication {
  public static void main(String[] args) {
    SpringApplication.run(PublicApplication.class, args);
  }
}
