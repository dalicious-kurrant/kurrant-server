package co.kurrant.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"co.dalicious.*", "co.kurrant.batch"})
@EnableJpaRepositories(basePackages = {"co.dalicious.*", "co.kurrant.batch"})
@SpringBootApplication(scanBasePackages = {"co.dalicious.*", "co.kurrant.batch"})
@EnableBatchProcessing
public class BatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }
}
