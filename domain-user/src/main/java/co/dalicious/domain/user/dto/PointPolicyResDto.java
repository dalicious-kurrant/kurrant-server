package co.dalicious.domain.user.dto;

import co.dalicious.domain.user.entity.enums.PointCondition;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class PointPolicyResDto {
    private List<PointConditionSelectBox> pointConditionSelectBoxList;
    private List<EventPointPolicy> eventPointPolicyList;

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
        private Integer pointConditionCode;
        private String pointConditionValue;
        private Integer completedConditionCount;
        private Integer accountCompletionLimit;
        private BigDecimal rewardPoint;
        private String eventStartDate;
        private String eventEndDate;
        private BigInteger boardId;
    }

    @Getter
    @Setter
    @Builder
    public static class PointConditionSelectBox {
        private Integer code;
        private String condition;

        public static PointConditionSelectBox create(PointCondition pointCondition) {
            return PointConditionSelectBox.builder()
                    .code(pointCondition.getCode())
                    .condition(pointCondition.getCondition())
                    .build();
        }
    }

    public static PointPolicyResDto create(List<EventPointPolicy>  eventPointPolicyList, List<PointConditionSelectBox> pointConditionSelectBoxList) {
        return PointPolicyResDto.builder()
                .eventPointPolicyList(eventPointPolicyList)
                .pointConditionSelectBoxList(pointConditionSelectBoxList)
                .build();
    }
}
