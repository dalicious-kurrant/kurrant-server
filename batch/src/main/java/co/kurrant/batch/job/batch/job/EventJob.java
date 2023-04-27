package co.kurrant.batch.job.batch.job;

import co.dalicious.client.core.entity.RefreshToken;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.user.entity.PointHistory;
import co.dalicious.domain.user.entity.PointPolicy;
import co.dalicious.system.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.awt.*;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class EventJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManager entityManager;

    @Bean("eventDeleteJob")
    public Job orderStatusToDeliveringJob() {
        return jobBuilderFactory.get("eventDeleteJob")
                .start(eventDeleteJob_step1())
                .next(eventDeleteJob_step2())
                .build();
    }

    @Bean
    @JobScope
    public Step eventDeleteJob_step1() {
        // point history 삭제
        return stepBuilderFactory.get("eventDeleteJob_step1")
                .tasklet(((contribution, chunkContext) -> {
                    log.info("[Point History 삭제 시작] : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));

                    final String pointPolicyQueryString = "SELECT pp.id FROM PointPolicy pp WHERE pp.eventEndDate < :today";
                    final LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
                    final TypedQuery<PointPolicy> pointPolicyQuery = entityManager.createQuery(pointPolicyQueryString, PointPolicy.class);
                    pointPolicyQuery.setParameter("today", today);

                    final List<PointPolicy> pointPolicyList = pointPolicyQuery.getResultList();


                    final String pointHistoryQueryString = "SELECT ph FROM PointHistory ph WHERE ph.pointPolicyId in :pointPolicyIdList and ph.point = 0";
                    final List<BigInteger> pointPolicyIdList = new ArrayList<>();
                    for (PointPolicy pointPolicy : pointPolicyList) {
                        pointPolicyIdList.add(pointPolicy.getId());
                    }
                    final TypedQuery<PointHistory> pointHistoryQuery = entityManager.createQuery(pointHistoryQueryString, PointHistory.class);
                    pointHistoryQuery.setParameter("pointPolicyIdList", pointPolicyIdList);

                    final List<PointHistory> pointHistoryList = pointHistoryQuery.getResultList();

                    for(PointHistory pointHistory : pointHistoryList) {
                        entityManager.remove(pointHistory);
                    }
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }

    @Bean
    @JobScope
    public Step eventDeleteJob_step2() {
        // point policy 삭제
        return stepBuilderFactory.get("eventDeleteJob_step2")
                .tasklet(((contribution, chunkContext) -> {
                    log.info("[Point Policy 삭제 시작] : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));

                    final String pointPolicyQueryString = "SELECT pp.id FROM PointPolicy pp WHERE pp.eventEndDate < :today";
                    final LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
                    final TypedQuery<PointPolicy> pointPolicyQuery = entityManager.createQuery(pointPolicyQueryString, PointPolicy.class);
                    pointPolicyQuery.setParameter("today", today);

                    final List<PointPolicy> pointPolicyList = pointPolicyQuery.getResultList();

                    for(PointPolicy pointPolicy : pointPolicyList) {
                        entityManager.remove(pointPolicy);
                    }
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }

}
