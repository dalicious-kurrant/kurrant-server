import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = { "co.kurrant.batch", "co.dalicious.*"})
@EntityScan(basePackages = "co.kurrant.batch")
public class JobApplication {
}
