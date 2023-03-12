package co.kurrant.batch.job.batch;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
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
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DailyFoodJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final int CHUNK_SIZE = 100;

    @Bean(name = "dailyFoodJob1")
    public Job dailyFoodJob1() {
        return jobBuilderFactory.get("dailyFoodJob1")
                .start(dailyFoodJob_step1())
                .build();
    }

    @Bean
    @JobScope
    public Step dailyFoodJob_step1() {
        // 식사 정보를 통해 주문 마감 시간 가져오기
        return stepBuilderFactory.get("dailyFoodJob_step1")
                .<DailyFood, DailyFood>chunk(CHUNK_SIZE)
                .reader(dailyFoodReader())
                .processor(dailyFoodProcessor())
                .writer(dailyFoodWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<DailyFood> dailyFoodReader() {
        log.info("[DailyFood 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));
        Map<String, Object> parameterValues = new HashMap<>();

        String queryString = "SELECT df FROM DailyFood df\n" +
                "LEFT JOIN df.group g\n" +
                "LEFT JOIN MealInfo mi on mi.group.id = g.id\n" +
                "WHERE mi.diningType = df.diningType\n" +
                "AND df.dailyFoodStatus = 1\n" +
                "AND NOW() >= CONCAT(DATE_FORMAT(df.serviceDate, '%Y-%m-%d '), SUBSTRING_INDEX(mi.lastOrderTime, '(', -1))";

        return new JpaPagingItemReaderBuilder<DailyFood>()
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
                .parameterValues(parameterValues)
                .queryString(queryString)
                .name("JpaPagingItemReader")
                .build();
    }

    @Bean
    @JobScope
    public ItemProcessor <DailyFood, DailyFood> dailyFoodProcessor() {
        return new ItemProcessor<DailyFood, DailyFood>() {
            @Override
            public DailyFood process(DailyFood dailyFood) throws Exception {
                log.info("[DailyFood 상태 업데이트 시작] : {}", dailyFood.getId());
                dailyFood.updateFoodStatus(DailyFoodStatus.SOLD_OUT);
                return dailyFood;
            }
        };
    }

    @Bean
    @JobScope
    public JpaItemWriter<DailyFood> dailyFoodWriter() {
        log.info("DailyFood 상태 저장 시작 : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));
        return new JpaItemWriterBuilder<DailyFood>().entityManagerFactory(entityManagerFactory).build();
    }
}
