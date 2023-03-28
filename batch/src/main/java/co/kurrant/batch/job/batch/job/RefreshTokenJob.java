package co.kurrant.batch.job.batch.job;

import co.dalicious.client.core.entity.RefreshToken;
import co.dalicious.domain.user.entity.ProviderEmail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RefreshTokenJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;
    private final int CHUNK_SIZE = 100;

    @Bean("refreshTokenJob")
    public Job refreshTokenJob() {
        return jobBuilderFactory.get("refreshTokenJob")
                .start(refreshTokenJob_step())
                .build();
    }

    @Bean
    @JobScope
    public Step refreshTokenJob_step() {
//        return stepBuilderFactory.get("refreshTokenJob_step")
//                .tasklet(((contribution, chunkContext) -> {
//                    Map<String, Object> parameterValues = new HashMap<>();
//                    LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7L);
//                    parameterValues.put("sevenDaysAgo", sevenDaysAgo);
//
//                    String queryString = "SELECT rt FROM RefreshToken rt \n" +
//                            "WHERE rt.createdDateTime <= :sevenDaysAgo";
//                    TypedQuery<RefreshToken> query = entityManager.createQuery(queryString, RefreshToken.class);
//                    query.setParameter()
//                }))
        return null;
    }
}
