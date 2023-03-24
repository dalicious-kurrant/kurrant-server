package co.kurrant.batch.job.batch.job;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.system.util.DateUtils;
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
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OrderItemJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;
    private final int CHUNK_SIZE = 100;

    @Bean("orderStatusToDeliveringJob")
    public Job orderStatusToDeliveringJob() {
        return jobBuilderFactory.get("orderStatusToDeliveringJob")
                .start(orderStatusToDeliveringJob_step())
                .build();
    }

    @Bean("orderStatusToDeliveredJob")
    public Job orderStatusToDeliveredJob() {
        return jobBuilderFactory.get("orderStatusToDeliveredJob")
                .start(orderStatusToDeliveredJob_step())
                .build();
    }

    @Bean
    @JobScope
    public Step orderStatusToDeliveringJob_step() {
        // pickup time 지나면 order status -> OrderStatus.DELIVERING
        return stepBuilderFactory.get("orderStatusToDeliveringJob_step")
                .<OrderItem, OrderItem>chunk(CHUNK_SIZE)
                .reader(forChangingStatusInOrderItemReader(findMatchedDailyFoodIdsForDelivering(), null))
                .processor(OrderStatusToDeliveringProcessor())
                .writer(forChangingStatusInOrderItemWriter())
                .build();
    }

    @Bean
    @JobScope
    public Step orderStatusToDeliveredJob_step() {
        // pickup time 지나면 order status -> OrderStatus.DELIVERED
        return stepBuilderFactory.get("orderStatusToDeliveredJob_step")
                .<OrderItem, OrderItem>chunk(CHUNK_SIZE)
                .reader(forChangingStatusInOrderItemReader(null, findMatchedDailyFoodIdsForDelivered()))
                .processor(OrderStatusToDeliveredProcessor())
                .writer(forChangingStatusInOrderItemWriter())
                .build();
    }

    @Bean(name = "findMatchedDailyFoodIdsForDelivering")
    List<BigInteger> findMatchedDailyFoodIdsForDelivering() {
        log.info("[OrderItemDailyFood 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));

        // list up order item daily food by order status = delivering
        String queryString = "SELECT oidf.dailyFood, dfg.pickupTime " +
                "FROM OrderItem oi " +
                "JOIN OrderItemDailyFood oidf ON oi.id = oidf.id " +
                "JOIN oidf.dailyFood df " +
                "JOIN df.dailyFoodGroup dfg " +
                "WHERE oi.orderStatus = 6L";

        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        List<Object[]> results = query.getResultList();

        LocalDate today  = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"));

        List<BigInteger> dailyFoodIds = new ArrayList<>();
        for(Object[] objects : results) {
            DailyFood dailyFood = (DailyFood) objects[0];
            LocalTime pickupTime = (LocalTime) objects[1];
            if(dailyFood.getServiceDate().equals(today) && pickupTime.isBefore(now)) {
                dailyFoodIds.add(dailyFood.getId());
            }
        }

        return dailyFoodIds;
    }

    @Bean(name = "findMatchedDailyFoodIdsForDelivered")
    List<BigInteger> findMatchedDailyFoodIdsForDelivered() {
        log.info("[OrderItemDailyFood 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));

        // list up order item daily food by order status = delivering
        String queryString = "SELECT df, mi.deliveryTime " +
                "FROM OrderItem oi " +
                "JOIN OrderItemDailyFood oidf ON oi.id = oidf.id " +
                "JOIN oidf.dailyFood df " +
                "JOIN df.group g " +
                "JOIN MealInfo mi ON g.id = mi.group.id AND df.diningType = mi.diningType " +
                "WHERE oi.orderStatus = 9L";

        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        List<Object[]> results = query.getResultList();

        LocalDate today  = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"));

        List<BigInteger> dailyFoodIds = new ArrayList<>();
        for(Object[] objects : results) {
            DailyFood dailyFood = (DailyFood) objects[0];
            LocalTime deliveryTime = (LocalTime) objects[1];
            if(dailyFood.getServiceDate().equals(today) && now.isAfter(deliveryTime)) {
                dailyFoodIds.add(dailyFood.getId());
            }
        }

        return dailyFoodIds;
    }

    @Bean
    @JobScope
    public JpaPagingItemReader<OrderItem> forChangingStatusInOrderItemReader(@Qualifier("findMatchedDailyFoodIdsForDelivering") List<BigInteger> forDeliveringIds,
                                                                             @Qualifier("findMatchedDailyFoodIdsForDelivered") List<BigInteger> forDeliveredIds) {
        log.info("[OrderItemDailyFood 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));
        Map<String, Object> parameterValues = new HashMap<>();
        if(forDeliveredIds != null && !forDeliveredIds.isEmpty()) {
            parameterValues.put("dailyFoodIds", forDeliveredIds);
        }
        if(forDeliveringIds != null && !forDeliveringIds.isEmpty()) {
            parameterValues.put("dailyFoodIds", forDeliveringIds);
        }

        String queryString = "SELECT oi " +
                "FROM OrderItem oi " +
                "JOIN OrderItemDailyFood oidf ON oi.id = oidf.id " +
                "WHERE oidf.dailyFood.id IN :dailyFoodIds";

        return new JpaPagingItemReaderBuilder<OrderItem>()
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
                .parameterValues(parameterValues)
                .queryString(queryString)
                .name("JpaPagingItemReader")
                .build();
    }

    @Bean
    @JobScope
    public ItemProcessor<OrderItem, OrderItem> OrderStatusToDeliveringProcessor() {
        return new ItemProcessor<OrderItem, OrderItem>() {
            @Override
            public OrderItem process(OrderItem item) throws Exception {
                log.info("[OrderItem 상태 업데이트 시작] : {}", item.getId());
                item.updateOrderStatus(OrderStatus.DELIVERING);
                return item;
            }
        };
    }

    @Bean
    @JobScope
    public ItemProcessor<OrderItem, OrderItem> OrderStatusToDeliveredProcessor() {
        return new ItemProcessor<OrderItem, OrderItem>() {
            @Override
            public OrderItem process(OrderItem item) throws Exception {
                log.info("[OrderItem 상태 업데이트 시작] : {}", item.getId());
                item.updateOrderStatus(OrderStatus.DELIVERED);
                return item;
            }
        };
    }

    @Bean
    @JobScope
    public JpaItemWriter<OrderItem> forChangingStatusInOrderItemWriter() {
        log.info("orderItem 상태 저장 시작 : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));
        return new JpaItemWriterBuilder<OrderItem>().entityManagerFactory(entityManagerFactory).build();
    }

}

