package co.dalicious.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PointPolicyResDto {

    @Getter
    @Setter
    public static class ReviewPointPolicy {
        private Integer minPrice;
        private Integer maxPrice;
        private BigDecimal contentPoint;
        private BigDecimal imagePoint;
    }

    @Getter
    @Setter
    public static class EventPointPolicy {
        private BigInteger pointPolicyId;
        private Integer pointCondition;
        private Integer completedConditionCount;
        private Integer accountCompletionLimit;
        private BigDecimal rewardPoint;
        private String eventStartDate;
        private String eventEndDate;
    }


}
