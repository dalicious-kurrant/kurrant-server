package co.dalicious.domain.client.dto;

import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.embeddable.ServiceDaysAndSupportPrice;
import co.dalicious.domain.client.entity.MySpotZoneMealInfo;
import co.dalicious.system.enums.Days;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DaysUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "스팟 상세정보 응답 DTO")
public class ClientSpotDetailResDto {
    private BigInteger spotId;
    private String spotName;
    private String address;
    private List<MealTypeInfo> mealTypeInfoList;
    private BigInteger clientId;
    private String clientName;

    @Getter
    @Setter
    public static class MealTypeInfo {
        private Integer diningType;
        private String serviceDays;
        private String membershipBenefitTime;
        private String lastOrderTime;
        private String deliveryTime;
        private BigDecimal supportPrice;

        @Builder
        public MealTypeInfo(MealInfo mealInfo) {
            BigDecimal bigDecimal = BigDecimal.ZERO;
            if(mealInfo instanceof CorporationMealInfo corporationMealInfo) {
                BigDecimal supportPrice = corporationMealInfo.getServiceDaysAndSupportPrices().stream()
                        .filter(o -> o.getSupportPrice().compareTo(BigDecimal.valueOf(0)) != 0).findAny()
                        .map(ServiceDaysAndSupportPrice::getSupportPrice).orElse(null);
                if(supportPrice != null) bigDecimal = bigDecimal.add(supportPrice);
            }

            this.diningType = mealInfo.getDiningType().getCode();
            this.serviceDays = DaysUtil.stringToDaysStringList(mealInfo.getServiceDays());
            this.membershipBenefitTime = DayAndTime.dayAndTimeToString(mealInfo.getMembershipBenefitTime());
            this.lastOrderTime = DayAndTime.dayAndTimeToString(mealInfo.getLastOrderTime());
            this.deliveryTime = DateUtils.timesToString(mealInfo.getDeliveryTimes());
            this.supportPrice = !(mealInfo instanceof CorporationMealInfo) ? bigDecimal : null;
        }
    }
}
