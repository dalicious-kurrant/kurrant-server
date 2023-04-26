package co.kurrant.batch.job.batch.job;

import co.dalicious.client.alarm.util.PushUtil;
import co.dalicious.client.core.entity.RefreshToken;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PushCondition;
import co.dalicious.system.util.DateUtils;
import co.kurrant.batch.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
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
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ReviewJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManager entityManager;
    private final ReviewService reviewService;
    private final PushUtil pushUtil;
    private final int CHUNK_SIZE = 100;

    @Bean(name = "reviewJob1")
    public Job reviewJob1() {
        return jobBuilderFactory.get("reviewJob1")
                .start(reviewJob1_step())
                .build();
    }

    @Bean
    @JobScope
    public Step reviewJob1_step() {
        // 식사 정보를 통해 주문 마감 시간 가져오기
        return stepBuilderFactory.get("reviewJob1_step")
                .tasklet((contribution, chunkContext) -> {
                    log.info("[review 마감 시간 알림 전송] : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));

                    final String queryString = "SELECT u FROM OrderItem oi JOIN Order o ON oi.order = o JOIN User u ON o.user = u WHERE oi.id in :orderItemIds";
                    List<BigInteger> orderItemIds = reviewService.findOrderItemByReviewDeadline();
                    final TypedQuery<User> query = entityManager.createQuery(queryString, User.class);
                    query.setParameter("orderItemIds", orderItemIds);

                    final List<User> userList = query.getResultList();

                    for(User user : userList) {
                        Map<String, Set<BigInteger>> map = Collections.singletonMap("userIds", new HashSet<>(Collections.singletonList(user.getId())));
                        pushUtil.sendToType(map, PushCondition.REVIEW_DEADLINE, null, null, null);
                    }
                    return RepeatStatus.FINISHED;
                        }
                ).build();
    }

}
