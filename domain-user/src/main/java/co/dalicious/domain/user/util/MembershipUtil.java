package co.dalicious.domain.user.util;

import co.dalicious.domain.user.dto.PeriodDto;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.dalicious.domain.user.repository.UserRepository;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MembershipUtil {
    private final MembershipRepository membershipRepository;

    // 월간 멤버십 회원권 구매시, 멤버십 기간 설정
    public static PeriodDto getStartAndEndDateMonthly(LocalDate paidDate) {
        int day = paidDate.getDayOfMonth();
        if (day <= 28) {
            return PeriodDto.builder()
                    .startDate(paidDate)
                    .endDate(paidDate.plusMonths(1))
                    .build();
        } else { // 가입일이 29~31일이면 자동 결제일이 1~28일이 될 때까지 계속 31일간 가입 상태 보장
            return PeriodDto.builder()
                    .startDate(paidDate)
                    .endDate(paidDate.plusDays(31))
                    .build();
        }
    }

    // 연간 멤버십 회원권 구매시, 멤버십 기간 설정
    public static PeriodDto getStartAndEndDateYearly(LocalDate paidDate) {
        LocalDate endDate = paidDate.plusYears(1);

        return PeriodDto.builder()
                .startDate(paidDate)
                .endDate(endDate)
                .build();
    }

    // 현재까지의 멤버십 이용 기간 출력
    public int getUserPeriodOfUsingMembership(User user) {
        List<Membership> membershipList = membershipRepository.findByUserOrderByEndDateDesc(user);
        List<Membership> autoPaymentMembership = membershipList.stream()
                .takeWhile(Membership::getAutoPayment).toList();

        // 근래 자동결제가 된 적이 없다면 0을 return.
        if (autoPaymentMembership.isEmpty()) {
            return 0;
        }

        Membership recentMembership = autoPaymentMembership.get(autoPaymentMembership.size() - 1);
        if (recentMembership == null) {
            return 0;
        }

        // 멤버십 이용 기간을 계산
        LocalDate firstPaidDate = recentMembership.getStartDate();
        LocalDate now = LocalDate.now();
        Period period = firstPaidDate.until(now);
        return period.getMonths() + 1;
    }

    // 시작날짜와 종료날짜로 멤버십 이용 개월 반환
    public static int getPeriodWithStartAndEndDate(LocalDate startDate, LocalDate endDate) {
        Period period = startDate.until(endDate);
        return period.getYears() * 12 + period.getMonths();
    }

    // 현재 유효한 멤버십인지 확인
    public static Boolean isValidMembership(Membership membership) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = membership.getStartDate();
        LocalDate endDate = membership.getEndDate();
        return (now.isAfter(startDate) || now.equals(startDate)) && (now.isBefore(endDate) || now.equals(endDate));
    }

}
