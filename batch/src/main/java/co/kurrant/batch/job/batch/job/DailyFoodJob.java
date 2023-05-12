package co.kurrant.batch.job.batch.job;

import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.system.util.DateUtils;
import co.kurrant.batch.service.DailyFoodService;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DailyFoodJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final DailyFoodService dailyFoodService;
    private final int CHUNK_SIZE = 500;

    @Bean(name = "dailyFoodJob1")
    public Job dailyFoodJob1() {
        return jobBuilderFactory.get("dailyFoodJob1")
                .start(dailyFoodJob_step1())
                .next(dailyFoodJob_step2())
                .build();
    }

    @Bean(name = "dailyFoodJob2")
    public Job dailyFoodJob2() {
        return jobBuilderFactory.get("dailyFoodJob2")
                .start(dailyFoodJob2_step1())
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
    @JobScope
    public Step dailyFoodJob_step2() {
        // 식사 정보를 통해 주문 마감 시간 가져오기
        return stepBuilderFactory.get("dailyFoodJob_step2")
                .<OrderItemDailyFood, OrderItemDailyFood>chunk(CHUNK_SIZE)
                .reader(orderItemDailyFoodReader())
                .processor(orderItemDailyFoodProcessor())
                .writer(orderItemDailyFoodWriter())
                .build();
    }

    @Bean
    @JobScope
    public Step dailyFoodJob2_step1() {
        // 식사 정보를 통해 주문 마감 시간 가져오기
        return stepBuilderFactory.get("dailyFoodJob2_step1")
                .<DailyFood, DailyFood>chunk(CHUNK_SIZE)
                .reader(makersLastOrderTimeDailyFoodReader())
                .processor(dailyFoodProcessor())
                .writer(dailyFoodWriter())
                .build();
    }


    @Bean
    @StepScope
    public JpaPagingItemReader<DailyFood> dailyFoodReader() {
        List<BigInteger> dailyFoodIds = dailyFoodService.matchingDailyFoodIds();
        log.info("[DailyFood 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("dailyFoodIds", dailyFoodIds);

        String queryString = "SELECT df " +
                "FROM DailyFood df " +
                "WHERE df.id IN :dailyFoodIds";

        return new JpaPagingItemReaderBuilder<DailyFood>()
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
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
                dailyFood.updateFoodStatus(DailyFoodStatus.PASS_LAST_ORDER_TIME);
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

    @Bean
    @StepScope
    public JpaPagingItemReader<OrderItemDailyFood> orderItemDailyFoodReader() {
        List<BigInteger> dailyFoodIds = dailyFoodService.overLastOrderTimeDailyFoodIds();
        log.info("[OrderItemDailyFood 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("dailyFoodIds", dailyFoodIds);

        String queryString = "SELECT od from OrderItemDailyFood od\n" +
                "WHERE od.orderStatus = 5L\n" +
                "AND od.dailyFood.id IN :dailyFoodIds";

        return new JpaPagingItemReaderBuilder<OrderItemDailyFood>()
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
                .parameterValues(parameterValues)
                .queryString(queryString)
                .name("JpaPagingItemReader")
                .build();
    }

    @Bean
    @JobScope
    public ItemProcessor <OrderItemDailyFood, OrderItemDailyFood> orderItemDailyFoodProcessor() {
        return new ItemProcessor<OrderItemDailyFood, OrderItemDailyFood>() {
            @Override
            public OrderItemDailyFood process(OrderItemDailyFood orderItemDailyFood) throws Exception {
                log.info("[OrderItemDailyFood 상태 업데이트 시작] : {}", orderItemDailyFood.getId());
                orderItemDailyFood.updateOrderStatus(OrderStatus.WAIT_DELIVERY);
                return orderItemDailyFood;
            }
        };
    }

    @Bean
    @JobScope
    public JpaItemWriter<OrderItemDailyFood> orderItemDailyFoodWriter() {
        log.info("DailyFood 상태 저장 시작 : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));
        return new JpaItemWriterBuilder<OrderItemDailyFood>().entityManagerFactory(entityManagerFactory).build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<DailyFood> makersLastOrderTimeDailyFoodReader() {
        Set<BigInteger> dailyFoodIds = dailyFoodService.overLastOverTimeDailyFood();
        log.info("[DailyFood 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("dailyFoodIds", dailyFoodIds);

        String queryString = "SELECT df " +
                "FROM DailyFood df " +
                "WHERE df.id IN :dailyFoodIds";

        return new JpaPagingItemReaderBuilder<DailyFood>()
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .parameterValues(parameterValues)
                .queryString(queryString)
                .name("JpaPagingItemReader")
                .build();
    }
}
