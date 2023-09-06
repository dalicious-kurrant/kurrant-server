package co.kurrant.batch.job.batch.job;

import co.dalicious.domain.order.service.OrderService;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.domain.user.util.MembershipUtil;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import co.kurrant.batch.job.batch.listener.MatchingMembershipIdsListener;
import co.kurrant.batch.service.MembershipService;
import exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MembershipPayJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;
    private final OrderService orderService;
    private final MembershipService membershipService;
    private final int CHUNK_SIZE = 100;

    @Bean(name = "membershipPayJob1")
    public Job membershipPayJob1() {
        return jobBuilderFactory.get("membershipPayJob1")
                .start(membershipPayJob_step1())
                .next(membershipPayJob_step2())
                .build();
    }

    @Bean
    @JobScope
    public Step membershipPayJob_step1() {
        // 식사 정보를 통해 주문 마감 시간 가져오기
        return stepBuilderFactory.get("membershipPayJob_step1")
                .<Membership, Membership>chunk(CHUNK_SIZE)
                .reader(membershipReader())
                .processor(membershipProcessor())
                .writer(membershipWriter())
                .faultTolerant()
                .skip(ApiException.class) // Add the exception classes you want to skip
                .skip(RuntimeException.class)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Membership> membershipReader() {
        log.info("[Membership 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));

        List<BigInteger> membershipIds = membershipService.getMembershipIds();

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("membershipIds", membershipIds);


        if (membershipIds.isEmpty()) {
            // Return an empty reader if membershipIds is empty
            return new JpaPagingItemReaderBuilder<Membership>()
                    .name("EmptyMembershipReader")
                    .build();
        }

        String queryString = "SELECT m FROM OrderItemMembership om " +
                "INNER JOIN Order o ON om.order = o " +
                "INNER JOIN Membership m ON om.membership = m " +
                "JOIN FETCH m.user u " + // Add JOIN FETCH here for the User entity
                "WHERE o.orderType = 3 AND o.paymentType = 1 AND u.userStatus = 1 AND om.membership.id IN :membershipIds";


        return new JpaPagingItemReaderBuilder<Membership>()
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
                .queryString(queryString)
                .name("JpaPagingItemReader")
                .parameterValues(Collections.singletonMap("membershipIds", membershipIds))
                .build();
    }

    @Bean
    @JobScope
    public ItemProcessor<Membership, Membership> membershipProcessor() {
        return new ItemProcessor<Membership, Membership>() {
            @Override
            public Membership process(Membership membership) throws IOException, ParseException {
                log.info("[Membership 상태 업데이트 시작] : {}", membership.getId());
                // TODO: 결제 수단이 추가 될 시 수정
                try {
                    orderService.payMembership(membership, PaymentType.CREDIT_CARD);
                    return membership;
                } catch (ApiException e) {
                    // Handle ApiException gracefully
                    log.error("ApiException encountered while processing membership {}: {}", membership.getId(), e.getMessage());
                    return null; // Return null to skip this item
                } catch (IOException | ParseException e) {
                    // Handle other exceptions
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Bean
    @JobScope
    public ItemWriter<Membership> membershipWriter() {
        log.info("Membership 상태 저장 시작 : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));

        JpaItemWriter<Membership> jpaItemWriter = new JpaItemWriterBuilder<Membership>()
                .entityManagerFactory(entityManagerFactory)
                .build();

        return new ItemWriter<Membership>() {
            @Override
            public void write(List<? extends Membership> items) throws Exception {
                List<Membership> nonNullItems = items.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                jpaItemWriter.write(nonNullItems);
            }
        };
    }

    @Bean
    public Step membershipPayJob_step2() {
        return stepBuilderFactory.get("membershipPayJob_step2")
                .tasklet((contribution, chunkContext) -> {

                    List<BigInteger> membershipIds = membershipService.getMembershipIds();

                    Map<String, Object> parameterValues = new HashMap<>();
                    parameterValues.put("membershipIds", membershipIds);

                    if (membershipIds.isEmpty()) {
                        return null;
                    }

                    // Create JPQL query to select ProviderEmail entities
                    String queryString = "SELECT m FROM OrderItemMembership om " +
                            "INNER JOIN Order o ON om.order = o " +
                            "INNER JOIN Membership m ON om.membership = m " +
                            "WHERE o.orderType = 3 and o.paymentType = 2 AND om.membership.id IN :membershipIds";
                    TypedQuery<Membership> query = entityManager.createQuery(queryString, Membership.class);
                    query.setParameter("membershipIds", membershipIds);

                    List<Membership> memberships = query.getResultList();

                    for (Membership membership : memberships) {
                        membership.getUser().changeMembershipStatus(false);
                    }
                    return RepeatStatus.FINISHED;
                }).build();
    }
}
