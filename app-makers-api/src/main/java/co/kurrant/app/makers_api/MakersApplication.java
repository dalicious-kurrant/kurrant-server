package co.kurrant.app.makers_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"co.dalicious.*", "co.kurrant.app.makers_api"})
@EnableJpaRepositories(basePackages = {"co.dalicious.*", "co.kurrant.app.makers_api"})
@SpringBootApplication(scanBasePackages = {"co.dalicious.*", "co.kurrant.app.makers_api"})
public class MakersApplication {

    public static void main(String[] args) {
        SpringApplication.run(MakersApplication.class, args);
    }

}
