import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrderTest {
    @Test
    void test() {
        System.out.println(LocalDate.now().toString().replace("-", ""));
    }

    @Test
    void test_2() {
        BigDecimal bigDecimal = BigDecimal.valueOf(20 / 100.0);
        BigDecimal price = MembershipSubscriptionType.YEAR.getPrice().multiply(BigDecimal.valueOf((100 - MembershipSubscriptionType.YEAR.getDiscountRate()) / 100.0));
        System.out.println("bigDecimal = " + bigDecimal);
        System.out.println("price = " + price);
    }
}
