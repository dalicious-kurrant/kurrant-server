package co.kurrant.batch.job.batch.job;

import co.dalicious.client.core.entity.RefreshToken;
import co.dalicious.system.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RefreshTokenJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManager entityManager;

    @Bean("refreshTokenJob1")
    public Job refreshTokenJob1() {
        return jobBuilderFactory.get("refreshTokenJob1")
                .start(refreshTokenJob_step())
                .build();
    }

    @Bean
    @JobScope
    public Step refreshTokenJob_step() {
        return stepBuilderFactory.get("refreshTokenJob_step")
                .tasklet(((contribution, chunkContext) -> {
                    log.info("[Refresh Token 삭제 시작] : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));

                    final String queryString = "SELECT rt FROM RefreshToken rt WHERE rt.createdDateTime <= :sevenDaysAgo";
                    final Date sevenDaysAgo = new Date(System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L));
                    final TypedQuery<RefreshToken> query = entityManager.createQuery(queryString, RefreshToken.class);
                    query.setParameter("sevenDaysAgo", sevenDaysAgo);

                    final List<RefreshToken> refreshTokens = query.getResultList();

                    for (RefreshToken refreshToken : refreshTokens) {
                        entityManager.remove(refreshToken);
                    };
                    return RepeatStatus.FINISHED;

                })).build();
    }
}
