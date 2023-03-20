package co.kurrant.batch.job.batch.job;

import co.dalicious.domain.user.entity.ProviderEmail;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.UserSpot;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.system.util.DateUtils;
import co.kurrant.batch.job.batch.listener.UpdatedUserIdsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UserWithdrawalJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;
    private final UpdatedUserIdsListener listener;
    private final int CHUNK_SIZE = 100;

    @Bean(name = "userWithdrawalJob1")
    public Job userWithdrawalJob1membershipPayJob1() {
        return jobBuilderFactory.get("userWithdrawalJob1")
                .start(userWithdrawalJob_step1())
                .next(userWithdrawalJob_step2())
                .build();
    }

    @Bean
    @JobScope
    public Step userWithdrawalJob_step1() {
        return stepBuilderFactory.get("userWithdrawalJob_step1")
                .<User, User>chunk(CHUNK_SIZE)
                .reader(userReader())
                .processor(createUserProcessor(listener))
                .writer(userWriter())
                .listener((StepExecutionListener) listener)
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

    public ItemProcessor<User, User> createUserProcessor(UpdatedUserIdsListener listener) {
        return user -> {
            log.info("[User 탈퇴 시작] : {} ", user.getId());
            user.withdrawUser();
            listener.addUser(user);
            return user;
        };
    }

    @Bean
    @JobScope
    public JpaItemWriter<User> userWriter() {
        log.info("User 상태 저장 시작 : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));
        return new JpaItemWriterBuilder<User>().entityManagerFactory(entityManagerFactory).build();
    }


    @Bean
    public Step userWithdrawalJob_step2() {
        // 탈퇴하는 유저의 OAuth 아이디를 삭제한다.
        return stepBuilderFactory.get("userWithdrawalJob_step2")
                .tasklet((contribution, chunkContext) -> {
                    ExecutionContext jobExecutionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
                    List<BigInteger> updatedUserIds = (List<BigInteger>) jobExecutionContext.get("updatedUserIds");

                    Map<String, Object> parameterValues = new HashMap<>();
                    parameterValues.put("updatedUserIds", updatedUserIds);

                    // Create JPQL query to select ProviderEmail entities
                    String queryString = "SELECT pe FROM ProviderEmail pe WHERE pe.user.id IN :updatedUserIds";
                    TypedQuery<ProviderEmail> query = entityManager.createQuery(queryString, ProviderEmail.class);
                    query.setParameter("updatedUserIds", updatedUserIds);

                    String queryString2 = "SELECT ug from UserGroup ug\n" +
                            "WHERE ug.user.id IN :updatedUserIds";
                    TypedQuery<UserGroup> query2 = entityManager.createQuery(queryString2, UserGroup.class);
                    query2.setParameter("updatedUserIds", updatedUserIds);

                    String queryString3 = "SELECT us from UserSpot us\n" +
                            "WHERE us.user.id IN :updatedUserIds";
                    TypedQuery<UserSpot> query3 = entityManager.createQuery(queryString3, UserSpot.class);
                    query3.setParameter("updatedUserIds", updatedUserIds);

                    // Retrieve matching ProviderEmail entities
                    List<ProviderEmail> result = query.getResultList();
                    List<UserGroup> result2 = query2.getResultList();
                    List<UserSpot> result3 = query3.getResultList();

                    // Delete matching ProviderEmail entities
                    for (ProviderEmail pe : result) {
                        entityManager.remove(pe);
                    }

                    for (UserGroup ug : result2) {
                        ug.updateStatus(ClientStatus.WITHDRAWAL);
                    }

                    for (UserSpot us : result3) {
                        entityManager.remove(us);
                    }

                    return RepeatStatus.FINISHED;
                }).build();
    }
}
