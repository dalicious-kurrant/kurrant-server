package co.kurrant.batch.job.batch.job;

import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.paycheck.dto.PaycheckDto;
import co.dalicious.domain.paycheck.service.PaycheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PaycheckJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final int CHUNK_SIZE = 100;
    private final PaycheckService paycheckService;

    @Bean("makersPaycheckJob")
    public Job makersPaycheckJob() throws Exception {
        return jobBuilderFactory.get("makersPaycheckJob")
                .start(makersPaycheckJob_step())
                .build();
    }

    @Bean
    @JobScope
    public Step makersPaycheckJob_step() throws Exception {
        // pickup time 지나면 order status -> OrderStatus.DELIVERING
        return stepBuilderFactory.get("makersPaycheckJob_step")
                .<PaycheckDto.PaycheckDailyFood, PaycheckDto.PaycheckDailyFood>chunk(CHUNK_SIZE)
                .reader(makersPaycheckReader())
                .processor(makersPaycheckProcessor())
                .writer(makersPaycheckWriter())
                .build();
    }

    @Bean
    @JobScope
    public JpaPagingItemReader<PaycheckDto.PaycheckDailyFood> makersPaycheckReader() throws Exception {

        String jpqlQuery = "SELECT NEW co.dalicious.domain.paycheck.dto.PaycheckDto$PaycheckDailyFood(" +
                "makers, dailyFood.serviceDate, dailyFood.diningType, food, food.name, " +
                "COALESCE(dailyFood.supplyPrice, food.supplyPrice), COUNT(orderItemDailyFood)) " +
                "FROM Food food " +
                "LEFT JOIN DailyFood dailyFood ON food = dailyFood.food " +
                "LEFT JOIN OrderItemDailyFood orderItemDailyFood ON orderItemDailyFood.dailyFood = dailyFood " +
                "LEFT JOIN OrderItem orderItem ON orderItem.id = orderItemDailyFood.id " +
                "LEFT JOIN Makers makers ON food.makers = makers " +
                "WHERE dailyFood.serviceDate BETWEEN :startOfMonth AND :endOfMonth " +
                "AND orderItem.orderStatus IN :completePaymentStatus " +
                "GROUP BY makers.id, dailyFood.serviceDate, dailyFood.diningType, food.id " +
                "HAVING COUNT(orderItemDailyFood) > 0 " +
                "ORDER BY makers.id ASC, dailyFood.serviceDate ASC, dailyFood.diningType ASC, food.id ASC";

        LocalDate startOfMonth = YearMonth.now().atDay(1);
        LocalDate endOfMonth = YearMonth.now().atEndOfMonth();

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("startOfMonth", startOfMonth);
        parameterValues.put("endOfMonth", endOfMonth);
        parameterValues.put("completePaymentStatus", OrderStatus.completePayment());

        JpaPagingItemReader<PaycheckDto.PaycheckDailyFood> reader = new JpaPagingItemReaderBuilder<PaycheckDto.PaycheckDailyFood>()
                .name("makersPaycheckReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString(jpqlQuery)
                .parameterValues(parameterValues)
                .pageSize(10)
                .transacted(false)
                .build();

        reader.afterPropertiesSet();

        return reader;
    }

    @Bean
    @JobScope
    public ItemProcessor<PaycheckDto.PaycheckDailyFood, PaycheckDto.PaycheckDailyFood> makersPaycheckProcessor() {
        return new ItemProcessor<PaycheckDto.PaycheckDailyFood, PaycheckDto.PaycheckDailyFood>() {
            @Override
            public PaycheckDto.PaycheckDailyFood process(PaycheckDto.PaycheckDailyFood item) throws Exception {
                // Do any processing on the single item if needed, otherwise, return the item directly.
                return item;
            }
        };
    }

    @Bean
    @JobScope
    public ItemWriter<PaycheckDto.PaycheckDailyFood> makersPaycheckWriter() {
        return new ItemWriter<PaycheckDto.PaycheckDailyFood>() {
            @Override
            public void write(List<? extends PaycheckDto.PaycheckDailyFood> items) throws Exception {
                paycheckService.generateAllMakersPaycheck(items);
            }
        };
    }

}
