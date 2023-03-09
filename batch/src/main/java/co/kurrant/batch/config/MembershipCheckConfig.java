package co.kurrant.batch.config;

import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.kurrant.batch.tasklet.SimpleJobTasklet;
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
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class MembershipCheckConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SimpleJobTasklet simpleJobTasklet;
    private final EntityManagerFactory entityManagerFactory;
    private final int CHUNK_SIZE = 10;

    @Bean
    public Job membershipCheckJob() {
        return jobBuilderFactory.get("membershipCheckJob")
//                .incrementer(new RunIdIncrementer())
//                .start(membershipCheckStep(null))
                .start(membershipCheckStep(null))
                .build();
    }

    @Bean
    @JobScope
    public Step membershipCheckStep(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("membershipCheckStep")
                .<Membership, User>chunk(CHUNK_SIZE)
                .reader(membershipReader())
//                .processor(userProcessor())
//                .writer(userWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Membership> membershipReader() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("now", Timestamp.valueOf(LocalDateTime.of(LocalDate.now().minusDays(1L), LocalTime.MIN)));

        return new JpaPagingItemReaderBuilder<Membership>()
                .name("membershipReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("SELECT m FROM Membership m WHERE endDate > :now AND user.isMembership = true")
                .parameterValues(parameters)
                .build();
    }

    @Bean
    public ItemProcessor<Membership, User> membershipUserProcessor() {
        return membership -> {
            membership.changeAutoPaymentStatus(false);
            return membership.getUser();
        };
    }

    @Bean
    public ItemProcessor<User, User> membershipUpdateProcessor() {
        return user -> {
            user.updateIsMembership(false);; // or false, depending on your business logic
            return user;
        };
    }

    @Bean
    public CompositeItemProcessor<Membership, User> membershipProcessor() {
        CompositeItemProcessor<Membership, User> processor = new CompositeItemProcessor<>();
        processor.setDelegates(Arrays.asList(
                membershipUserProcessor(),
                membershipUpdateProcessor()
        ));
        return processor;
    }

    @Bean
    public Step membershipUpdateStep() {
        return stepBuilderFactory.get("membershipUpdateStep")
                .<Membership, User>chunk(CHUNK_SIZE)
                .reader(membershipReader())
                .processor(membershipProcessor())
                .writer(userJpaItemWriter())
                .build();
    }

    @Bean
    public JpaItemWriter<User> userJpaItemWriter() {
        JpaItemWriter<User> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
}
