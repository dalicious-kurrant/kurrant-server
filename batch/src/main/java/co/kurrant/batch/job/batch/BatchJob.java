package co.kurrant.batch.job.batch;

import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.system.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final int CHUNK_SIZE = 10;

    @Bean(name = "batchJob1")
    public Job batchJob1() {
        return jobBuilderFactory.get("BatchJob1")
                .start(batchJob1_step1())
                .build();
    }

    @Bean
    @JobScope
    public Step batchJob1_step1() {
        // 식사 정보를 통해 주문 마감 시간 가져오기
        return stepBuilderFactory.get("batchJob1_step1")
                .<MealInfo, MealInfo>chunk(CHUNK_SIZE)
                .reader(reader(null))
                .processor(processor)
                .tasklet()
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<DailyFood> reader(@Value("#{jobParameters[requestDate]}") String requestDate) {
        log.info("[MealInfo 읽기 시작] : {} ", requestDate);
        Map<String, Object> parameterValues = new HashMap<>();
        String time = DateUtils.timeToString(LocalTime.now());
        parameterValues.put("time", time);



        return new JpaPagingItemReaderBuilder<DailyFood>()
                .pageSize(10)
                .parameterValues(parameterValues)
                .queryString("")
                .name("JpaPagingItemReader")
                .build();
    }
}
