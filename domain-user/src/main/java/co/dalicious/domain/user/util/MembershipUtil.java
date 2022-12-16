package co.dalicious.domain.user.util;

import co.dalicious.domain.user.dto.PeriodDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;

@Component
public class MembershipUtil {
    public static PeriodDto getStartAndEndDateMonthly(LocalDate paidDate) {
        int day = paidDate.getDayOfMonth();
        if(day <= 28) {
            return PeriodDto.builder()
                    .startDate(paidDate)
                    .endDate(paidDate.plusMonths(1))
                    .build();
        } else {
            return PeriodDto.builder()
                    .startDate(paidDate)
                    .endDate(paidDate.plusDays(31))
                    .build();
        }
    }

//    public static PeriodDto getStartAndEndDateYearly(LocalDate paidDate) {
//        if(paidDate.isLeapYear() && paidDate.getMonth().equals(Month.FEBRUARY) && paidDate.getDayOfMonth() == 29) {
//            paidDate.plusYears(1);
//        }
//
//    }
}
