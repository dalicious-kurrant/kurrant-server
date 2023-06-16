package co.kurrant.batch.job.batch.job;

import co.dalicious.client.alarm.dto.BatchAlarmDto;
import co.dalicious.client.alarm.dto.PushRequestDtoByUser;
import co.dalicious.client.alarm.service.PushService;
import co.dalicious.client.alarm.util.PushUtil;
import co.dalicious.client.core.entity.RefreshToken;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.domain.user.entity.enums.PushCondition;
import co.dalicious.domain.user.util.MembershipUtil;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import co.kurrant.batch.service.ReviewService;
import exception.ApiException;
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
    private final ReviewService reviewService;
    private final PushUtil pushUtil;
    private final EntityManagerFactory entityManagerFactory;
    private final PushService pushService;
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
        return stepBuilderFactory.get("reviewJob1_step1")
                .<User, User>chunk(CHUNK_SIZE)
                .reader(reviewReader())
                .processor(reviewProcessor())
                .writer(reviewWriter())
                .faultTolerant()
                .skip(ApiException.class) // Add the exception classes you want to skip
                .skip(RuntimeException.class)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<User> reviewReader() {
        log.info("[user 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));

        List<BigInteger> userIds = reviewService.findUserIdsByReviewDeadline();

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("userIds", userIds);


        if (userIds.isEmpty()) {
            // Return an empty reader if orderItemIds is empty
            return new JpaPagingItemReaderBuilder<User>()
                    .name("EmptyReviewReader")
                    .build();
        }

        String queryString = "SELECT u FROM User u WHERE u.id in :userIds";

        return new JpaPagingItemReaderBuilder<User>()
                .entityManagerFactory(entityManagerFactory) // Use the injected entityManagerFactory
                .pageSize(100)
                .queryString(queryString)
                .name("JpaPagingItemReader")
                .parameterValues(Collections.singletonMap("userIds", userIds))
                .build();
    }

    @Bean
    @JobScope
    public ItemProcessor<User, User> reviewProcessor() {
        return new ItemProcessor<User, User>() {
            @Override
            public User process(User user) throws Exception {
                log.info("[User 푸시 알림 전송 시작] : {}", user.getId());
                try {
                    PushCondition pushCondition = PushCondition.REVIEW_DEADLINE;

                    PushRequestDtoByUser pushRequestDto = pushUtil.getPushRequest(user, pushCondition, null);
                    BatchAlarmDto batchAlarmDto = pushUtil.getBatchAlarmDto(pushRequestDto, user);
                    pushService.sendToPush(batchAlarmDto, pushCondition);

                    log.info("[푸시알림 전송 성공] : {}", user.getId());
                } catch (Exception ignored) {
                    log.info("[푸시알림 전송 실패] : {}", user.getId());
                }
                return user;
            }
        };
    }

    @Bean
    @JobScope
    public JpaItemWriter<User> reviewWriter() {
        log.info("리뷰 푸시전송 완료 시작 : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));
        return new JpaItemWriterBuilder<User>().entityManagerFactory(entityManagerFactory).build();
    }


}
