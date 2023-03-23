package co.kurrant.batch.job.batch.job;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MealInfo;
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
                .reader(orderItemDailyFoodReader(matchingOrderStatusByWaitDelivery()))
                .processor(OrderStatusToDeliveringProcessor())
                .writer(orderItemDailyFoodWriter())
                .build();
    }

    @Bean
    @JobScope
    public Step orderStatusToDeliveredJob_step() {
        // pickup time 지나면 order status -> OrderStatus.DELIVERED
        return stepBuilderFactory.get("orderStatusToDeliveredJob_step")
                .<OrderItem, OrderItem>chunk(CHUNK_SIZE)
                .reader(orderItemDailyFoodReader(matchingOrderStatusByWaitDelivering()))
                .processor(OrderStatusToDeliveredProcessor())
                .writer(orderItemDailyFoodWriter())
                .build();
    }

    @Bean(name = "matchingDailyFoodIds")
    List<BigInteger> matchingOrderStatusByWaitDelivery() {
        log.info("[OrderItemDailyFood 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));

        // list up order item daily food by order status = delivering
        String queryString = "SELECT oidf.dailyFood " +
                "FROM OrderItemDailyFood oidf " +
                "WHERE oidf.orderStatus = 6";

        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        List<Object[]> results = query.getResultList();

        LocalDate today  = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"));

        List<BigInteger> dailyFoodIds = new ArrayList<>();
        for(Object[] objects : results) {
            DailyFood dailyFood = (DailyFood) objects[0];
            if(dailyFood.getServiceDate().equals(today) && dailyFood.getDailyFoodGroup().getPickupTime().isAfter(now)) {
                dailyFoodIds.add(dailyFood.getId());
            }
        }

        return dailyFoodIds;
    }

    @Bean(name = "matchingDailyFoodIds")
    List<BigInteger> matchingOrderStatusByWaitDelivering() {
        log.info("[OrderItemDailyFood 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));

        // list up order item daily food by order status = delivering
        String queryString = "SELECT oidf.dailyFood, mi.deliveryTime " +
                "FROM OrderItemDailyFood oidf " +
                "JOIN oidf.dailyFood df " +
                "JOIN df.group g " +
                "JOIN MealInfo mi ON mi.group.id = g.id " +
                "WHERE oidf.orderStatus = 9" +
                " AND mi.diningType = df.diningType";

        TypedQuery<Object[]> query = entityManager.createQuery(queryString, Object[].class);
        List<Object[]> results = query.getResultList();

        LocalDate today  = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"));

        List<BigInteger> dailyFoodIds = new ArrayList<>();
        for(Object[] objects : results) {
            DailyFood dailyFood = (DailyFood) objects[0];
            LocalTime deliveryTime = (LocalTime) objects[1];
            if(dailyFood.getServiceDate().equals(today) && deliveryTime.isAfter(now)) {
                dailyFoodIds.add(dailyFood.getId());
            }
        }

        return dailyFoodIds;
    }

    @Bean
    @JobScope
    public JpaPagingItemReader<OrderItem> orderItemDailyFoodReader(@Qualifier("matchingDailyFoodIds") List<BigInteger> dailyFoodIds) {
        log.info("[OrderItemDailyFood 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("dailyFoodIds", dailyFoodIds);

        String queryString = "SELECT oi " +
                "FROM OrderItemDailyFood oidf " +
                "JOIN OrderItem oi ON oidf.id = oi.id " +
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
    public JpaItemWriter<OrderItem> orderItemDailyFoodWriter() {
        log.info("orderItem 상태 저장 시작 : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));
        return new JpaItemWriterBuilder<OrderItem>().entityManagerFactory(entityManagerFactory).build();
    }

}

