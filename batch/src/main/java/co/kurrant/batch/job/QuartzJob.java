package co.kurrant.batch.job;

import co.dalicious.domain.order.service.OrderService;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.payment.repository.QCreditCardInfoRepository;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.dalicious.domain.user.repository.QMembershipRepository;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.domain.user.util.MembershipUtil;
import co.dalicious.system.util.PeriodDto;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class QuartzJob implements Job {
    /*
     * Quartz instantiates jobs using a no-argument constructor,
     * and it does not have access to the Spring container to perform dependency injection.
     * so, cannot use constructor dependency injection with Quartz jobs.
     * */
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QMembershipRepository qMembershipRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private QCreditCardInfoRepository qCreditCardInfoRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Quartz Job 실행");

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        log.info("dataMap date : {}", dataMap.get("date"));
        log.info("dataMap executeCount : {}", dataMap.get("executeCount"));

        // JobDataMap을 통해 Job의 실행 횟수를 받아서 +1을 한다.
        int count = (int) dataMap.get("executeCount");
        dataMap.put("executeCount", ++count);

        // 로직 수행
        List<Membership> memberships = qMembershipRepository.findAllByEndDate();
        List<User> users = new ArrayList<>();

        for (Membership membership : memberships) {
            try {
                // TODO: 결제 수단이 추가 될 시 수정
                PeriodDto periodDto = (membership.getMembershipSubscriptionType().equals(MembershipSubscriptionType.MONTH)) ?
                        MembershipUtil.getStartAndEndDateMonthly(membership.getEndDate()) :
                        MembershipUtil.getStartAndEndDateYearly(membership.getEndDate().plusMonths(1));
                orderService.payMembership(membership.getUser(), membership.getMembershipSubscriptionType(), periodDto, PaymentType.CREDIT_CARD);
            } catch (Exception ignored) {

            }
        }

    }
}
