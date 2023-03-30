package co.dalicious.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PointPolicyReqDto {

    @Getter
    @Setter
    public static class EventPointPolicy {
        private Integer pointCondition;
        private Integer completedConditionCount;
        private Integer accountCompletionLimit;
        private Integer rewardPoint;
        private String eventStartDate;
        private String eventEndDate;
    }
}