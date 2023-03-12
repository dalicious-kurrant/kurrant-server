package co.kurrant.batch.job.batch;

import co.dalicious.domain.order.service.OrderService;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.domain.user.util.MembershipUtil;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MembershipPayJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final OrderService orderService;
    private final int CHUNK_SIZE = 100;

    @Bean(name = "membershipPayJob1")
    public Job membershipPayJob1() {
        return jobBuilderFactory.get("membershipPayJob1")
                .start(membershipPayJob_step1())
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
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Membership> membershipReader() {
        log.info("[Membership 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));
        Map<String, Object> parameterValues = new HashMap<>();
        LocalDate now = LocalDate.now();
        parameterValues.put("now", now);
        String queryString = "SELECT m FROM Membership m JOIN FETCH m.user WHERE m.endDate <= :now AND m.autoPayment = true";

        return new JpaPagingItemReaderBuilder<Membership>()
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
                .parameterValues(parameterValues)
                .queryString(queryString)
                .name("JpaPagingItemReader")
                .build();
    }

    @Bean
    @JobScope
    public ItemProcessor<Membership, Membership> membershipProcessor() {
        return new ItemProcessor<Membership, Membership>() {
            @Override
            public Membership process(Membership membership) throws Exception {
                log.info("[Membership 상태 업데이트 시작] : {}", membership.getId());
                try {
                    // TODO: 결제 수단이 추가 될 시 수정
                    PeriodDto periodDto = (membership.getMembershipSubscriptionType().equals(MembershipSubscriptionType.MONTH)) ?
                            MembershipUtil.getStartAndEndDateMonthly(membership.getEndDate()) :
                            MembershipUtil.getStartAndEndDateYearly(membership.getEndDate().plusMonths(1));
                    orderService.payMembership(membership.getUser(), membership.getMembershipSubscriptionType(), periodDto, PaymentType.CREDIT_CARD);
                    log.info("[Membership 결제 성공] : {}", membership.getId());
                } catch (Exception ignored) {
                    membership.changeAutoPaymentStatus(false);
                    membership.getUser().changeMembershipStatus(false);
                    log.info("[Membership 결제 실패] : {}", membership.getId());
                }
                return membership;
            }
        };
    }

    @Bean
    @JobScope
    public JpaItemWriter<Membership> membershipWriter() {
        log.info("Membership 상태 저장 시작 : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));
        return new JpaItemWriterBuilder<Membership>().entityManagerFactory(entityManagerFactory).build();
    }
}
