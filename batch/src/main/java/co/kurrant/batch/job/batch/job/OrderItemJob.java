package co.kurrant.batch.job.batch.job;

import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.system.util.DateUtils;
import co.kurrant.batch.service.OrderItemService;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OrderItemJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final OrderItemService orderItemService;
    private final int CHUNK_SIZE = 500;

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
                .reader(forChangingStatusToDeliveringInOrderItemReader())
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
                .reader(forChangingStatusToDeliveredInOrderItemReader())
                .processor(OrderStatusToDeliveredProcessor())
                .writer(forChangingStatusInOrderItemWriter())
                .build();
    }

    @Bean
    @JobScope
    public JpaPagingItemReader<OrderItem> forChangingStatusToDeliveringInOrderItemReader() {
        List<BigInteger> orderItemIds = orderItemService.matchingOrderStatusByWaitDelivery();
        log.info("[OrderItemDailyFood 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("orderItemIds", orderItemIds);

        String queryString = "SELECT oi " +
                "FROM OrderItem oi " +
                "WHERE oi.id IN :orderItemIds";

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
    public JpaPagingItemReader<OrderItem> forChangingStatusToDeliveredInOrderItemReader() {
        List<BigInteger> orderItemIds = orderItemService.matchingOrderStatusByWaitDelivering();
        log.info("[OrderItemDailyFood 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("orderItemIds", orderItemIds);

        String queryString = "SELECT oi " +
                "FROM OrderItem oi " +
                "WHERE oi.id IN :orderItemIds";

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

