package kr.autohero.combined;


import co.dalicious.domain.user.dto.PeriodDto;
import co.dalicious.domain.user.dto.ProviderEmailDto;
import co.dalicious.domain.user.entity.Provider;
import co.dalicious.domain.user.util.MembershipUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Arrays;

class DomainUserApplicationTests {
    @Test
    public void Provider_test() {
        String sns = "kakao";
        System.out.println(Arrays.toString(Provider.values()));
        Assertions.assertTrue(Arrays.toString(Provider.values()).contains("KAKAO"));
        Provider provider = Provider.KAKAO;
        Provider provider1 = Provider.valueOf(sns.toUpperCase());
        Assertions.assertEquals(provider1, provider);
    }

    @Test
    public void MembershipUtil_MonthlyPaid_Test() {
        LocalDate test1 = LocalDate.of(2022, 1, 29);
        PeriodDto period = MembershipUtil.getStartAndEndDateMonthly(test1);
        LocalDate result1 = period.getEndDate();

        Assertions.assertEquals(result1, LocalDate.of(2022, 3, 1));
    }

    @Test
    public void MembershipUtil_YearlyPaid_Test() {
        LocalDate test1 = LocalDate.of(2024, 2, 29);
        LocalDate result1 = test1.plusYears(1);

        System.out.println(result1);
    }
}
