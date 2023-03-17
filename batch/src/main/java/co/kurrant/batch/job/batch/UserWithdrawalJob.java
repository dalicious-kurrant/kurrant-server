package co.kurrant.batch.job.batch;

import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.UserStatus;
import co.dalicious.system.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UserWithdrawalJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;
    private final int CHUNK_SIZE = 100;

    @Bean(name = "userWithdrawalJob1")
    public Job userWithdrawalJob1membershipPayJob1() {
        return jobBuilderFactory.get("userWithdrawalJob1")
                .start(userWithdrawalJob_step1())
                .build();
    }

    @Bean
    @JobScope
    public Step userWithdrawalJob_step1() {
        // 식사 정보를 통해 주문 마감 시간 가져오기
        return stepBuilderFactory.get("userWithdrawalJob_step1")
                .<User, User>chunk(CHUNK_SIZE)
                .reader(userReader())
//                .processor()
//                .writer()
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<User> userReader() {
        log.info("[User 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));

        String queryString = "SELECT u FROM User u\n" +
                "WHERE u.userStatus = 2 AND FUNCTION('TIMESTAMPDIFF', DAY, u.updatedDateTime, CURRENT_DATE) >= 7";

        return new JpaPagingItemReaderBuilder<User>()
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
                .queryString(queryString)
                .name("JpaPagingItemReader")
                .build();
    }

//    @Bean
//    @JobScope
//    public ItemProcessor<User, User> userProcessor() {
//        return new ItemProcessor<User, User>() {
//            @Override
//            public User process(User user) throws Exception {
//                log.info("[User 탈퇴 시작] : {} ", user.getId());
//
//                user.withdrawUser();
//
//            }
//        }
//    }
}
