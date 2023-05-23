package co.kurrant.batch.job.batch.job;

import co.dalicious.client.alarm.util.PushUtil;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PushCondition;
import co.dalicious.system.util.DateUtils;
import co.kurrant.batch.service.PushAlarmService;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PushAlarmJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final PushAlarmService pushAlarmService;
    private final PushUtil pushUtil;
    private final int CHUNK_SIZE = 500;

    @Bean(name = "pushAlarmJob1")
    public Job pushAlarmJob1() {
        return jobBuilderFactory.get("pushAlarmJob1")
                .start(pushAlarmJob_step())
                .build();
    }

    @Bean
    @JobScope
    public Step pushAlarmJob_step() {
        return stepBuilderFactory.get("pushAlarmJob_step1")
                .<User, User>chunk(CHUNK_SIZE)
                .reader(lastOrderTimePushAlarmReader())
                .processor(lastOrderTimePushAlarmProcessor())
                .writer(lastOrderTimePushAlarmWriter())
                .faultTolerant()
                .skip(ApiException.class) // Add the exception classes you want to skip
                .skip(RuntimeException.class)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<User> lastOrderTimePushAlarmReader() {
        log.info("[user 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));

        List<BigInteger> groupIds = pushAlarmService.getGroupsForOneHourLeftLastOrderTime();

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("groupIds", groupIds);


        if (groupIds.isEmpty()) {
            // Return an empty reader if orderItemIds is empty
            return new JpaPagingItemReaderBuilder<User>()
                    .name("EmptyReviewReader")
                    .build();
        }

        String queryString = "SELECT u FROM UserGroup ug LEFT JOIN User u ON u = ug.user WHERE ug.id in :groupIds";

        return new JpaPagingItemReaderBuilder<User>()
                .entityManagerFactory(entityManagerFactory) // Use the injected entityManagerFactory
                .pageSize(100)
                .queryString(queryString)
                .name("JpaPagingItemReader")
                .parameterValues(Collections.singletonMap("groupIds", groupIds))
                .build();
    }

    @Bean
    @JobScope
    public ItemProcessor<User, User> lastOrderTimePushAlarmProcessor() {
        return new ItemProcessor<User, User>() {
            @Override
            public User process(User user) throws Exception {
                log.info("[User 푸시 알림 전송 시작] : {}", user.getId());
                try {
                    // TODO: 결제 수단이 추가 될 시 수정
                    pushUtil.getBatchAlarmDto(user, PushCondition.LAST_ORDER_BY_DAILYFOOD);
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
    public JpaItemWriter<User> lastOrderTimePushAlarmWriter() {
        log.info("리뷰 푸시전송 완료 시작 : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));
        return new JpaItemWriterBuilder<User>().entityManagerFactory(entityManagerFactory).build();
    }
}

