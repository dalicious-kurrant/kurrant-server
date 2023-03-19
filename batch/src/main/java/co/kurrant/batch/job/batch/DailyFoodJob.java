package co.kurrant.batch.job.batch;

import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DailyFoodJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;
    private final int CHUNK_SIZE = 100;

    @Bean(name = "dailyFoodJob1")
    public Job dailyFoodJob1() {
        return jobBuilderFactory.get("dailyFoodJob1")
                .start(dailyFoodJob_step1())
                .next(dailyFoodJob_step2())
                .build();
    }

    @Bean
    @JobScope
    public Step dailyFoodJob_step1() {
        // 식사 정보를 통해 주문 마감 시간 가져오기
        return stepBuilderFactory.get("dailyFoodJob_step1")
                .<DailyFood, DailyFood>chunk(CHUNK_SIZE)
                .reader(dailyFoodReader(matchingDailyFoodIds()))
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
                .reader(orderItemDailyFoodReader(matchingDailyFoodIds()))
                .processor(orderItemDailyFoodProcessor())
                .writer(orderItemDailyFoodWriter())
                .build();
    }

    @Bean(name = "matchingDailyFoodIds")
    public List<BigInteger> matchingDailyFoodIds() {
        log.info("[DailyFood 읽기 시작] : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));

        String queryString = "SELECT df.id, mi.lastOrderTime, df.serviceDate " +
                "FROM DailyFood df " +
                "JOIN df.group g " +
                "JOIN MealInfo mi ON mi.group.id = g.id " +
                "WHERE mi.diningType = df.diningType " +
                "  AND df.dailyFoodStatus = 1";

        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        List<Object[]> results = query.getResultList();

        List<BigInteger> dailyFoodIds = new ArrayList<>();
        for (Object[] result : results) {
            BigInteger dailyFoodId = (BigInteger) result[0];
            DayAndTime lastOrderDayAndTime = (DayAndTime) result[1];
            LocalDate serviceDate = (LocalDate) result[2]; // Fetch the serviceDate from the DailyFood entity
            LocalDate lastOrderDate = serviceDate.minusDays(lastOrderDayAndTime.getDay());
            LocalDateTime lastOrderDateTime = lastOrderDate.atTime(lastOrderDayAndTime.getTime());

            if (LocalDateTime.now().isAfter(lastOrderDateTime) || LocalDateTime.now().isEqual(lastOrderDateTime)) {
                dailyFoodIds.add(dailyFoodId);
            }
        }

        return dailyFoodIds;
    }


    @Bean
    @StepScope
    public JpaPagingItemReader<DailyFood> dailyFoodReader(@Qualifier("matchingDailyFoodIds") List<BigInteger> dailyFoodIds) {
        log.info("[DailyFood 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("dailyFoodIds", dailyFoodIds);

        String queryString = "SELECT df " +
                "FROM DailyFood df " +
                "WHERE df.id IN :dailyFoodIds";

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
    public JpaPagingItemReader<OrderItemDailyFood> orderItemDailyFoodReader(@Qualifier("matchingDailyFoodIds") List<BigInteger> dailyFoodIds) {
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
}
