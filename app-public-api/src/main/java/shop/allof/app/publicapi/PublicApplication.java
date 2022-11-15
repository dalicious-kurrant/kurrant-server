package shop.allof.app.publicapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = {"io.corretto.*", "shop.allof.*"})
@SpringBootApplication(scanBasePackages = {"io.corretto.*", "shop.allof.*"})
public class PublicApplication {
  public static void main(String[] args) {
    SpringApplication.run(PublicApplication.class, args);
  }
}
