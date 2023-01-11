package co.dalicious.domain.client.repository;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "co.dalicious.domain.client.repository")
@EntityScan(basePackages = "co.dalicious.domain.client")
public class TestConfiguration {
}