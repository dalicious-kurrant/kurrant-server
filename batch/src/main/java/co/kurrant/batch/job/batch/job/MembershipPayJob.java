package co.kurrant.batch.job.batch.job;

import co.dalicious.domain.order.service.OrderService;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.domain.user.util.MembershipUtil;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import co.kurrant.batch.job.batch.listener.MatchingMembershipIdsListener;
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
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MembershipPayJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;
    private final OrderService orderService;
    private final MatchingMembershipIdsListener matchingMembershipIdsListener;
    private final int CHUNK_SIZE = 100;

    private List<BigInteger> membershipIds = null;

    private List<BigInteger> getMembershipIds() {
        if (membershipIds == null) {
            String queryString = "SELECT m.id FROM Membership m " +
                    "JOIN m.user u " +
                    "WHERE m.endDate <= NOW() " +
                    "AND m.autoPayment = true " +
                    "AND m.createdDateTime = (" +
                    "   SELECT MAX(m2.createdDateTime) FROM Membership m2 WHERE m2.user = u" +
                    ")";

            TypedQuery<BigInteger> query = entityManager.createQuery(queryString, BigInteger.class);

            return query.getResultList();
        }

        return membershipIds;
    }

    @Bean(name = "membershipPayJob1")
    public Job membershipPayJob1() {
        return jobBuilderFactory.get("membershipPayJob1")
                .start(membershipPayJob_step1())
                .start(membershipPayJob_step2())
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

        List<BigInteger> membershipIds = getMembershipIds();

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("membershipIds", membershipIds);


        if (membershipIds.isEmpty()) {
            return null;
        }

        String queryString = "SELECT m FROM OrderItemMembership om\n" +
                "INNER JOIN Order o ON om.order = o\n" +
                "INNER JOIN Membership m ON om.membership = m\n" +
                "WHERE o.orderType = 3 and o.paymentType = 1 AND om.membership.id IN :membershipIds";

        return new JpaPagingItemReaderBuilder<Membership>()
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
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

    @Bean
    public Step membershipPayJob_step2() {
        // 탈퇴하는 유저의 OAuth 아이디를 삭제한다.
        return stepBuilderFactory.get("membershipPayJob_step2")
                .tasklet((contribution, chunkContext) -> {

                    List<BigInteger> membershipIds = getMembershipIds();

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