package co.dalicious.data.flyway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;

@EntityScan(basePackages = {"co.dalicious.*"})
@SpringBootApplication
public class FlywayApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(FlywayApplication.class, args);
        ctx.close();
    }
}
