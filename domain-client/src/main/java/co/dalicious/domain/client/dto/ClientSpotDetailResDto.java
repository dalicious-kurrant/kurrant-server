package co.dalicious.domain.client.dto;

import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.embeddable.ServiceDaysAndSupportPrice;
import co.dalicious.system.enums.Days;
import co.dalicious.system.util.DateUtils;
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
    private Integer ho;
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
        private List<String> deliveryTime;
        private BigDecimal supportPrice;

        @Builder
        public MealTypeInfo(MealInfo mealInfo) {
            StringBuilder serviceDays = new StringBuilder();
            BigDecimal bigDecimal = BigDecimal.ZERO;
            if(mealInfo instanceof CorporationMealInfo corporationMealInfo) {
                List<Days> allServiceDays = corporationMealInfo.getServiceDays();
                serviceDays.append(allServiceDays.stream().map(Days::getDays)).append(", ");
                BigDecimal supportPrice = corporationMealInfo.getServiceDaysAndSupportPrices().stream()
                        .filter(o -> o.getSupportPrice().compareTo(BigDecimal.valueOf(0)) != 0).findAny()
                        .map(ServiceDaysAndSupportPrice::getSupportPrice).orElse(null);
                bigDecimal = bigDecimal.add(supportPrice);
            }
            else if(mealInfo instanceof ApartmentMealInfo apartmentMealInfo) {
                serviceDays.append(apartmentMealInfo.getServiceDays().stream().map(Days::getDays)).append(", ");
            }
            else if (mealInfo instanceof OpenGroupMealInfo openGroupMealInfo) {
                serviceDays.append(openGroupMealInfo.getServiceDays().stream().map(Days::getDays)).append(", ");
            }

            this.diningType = mealInfo.getDiningType().getCode();
            this.serviceDays = String.valueOf(serviceDays);
            this.membershipBenefitTime = DayAndTime.dayAndTimeToString(mealInfo.getMembershipBenefitTime());
            this.lastOrderTime = DayAndTime.dayAndTimeToString(mealInfo.getLastOrderTime());
            this.deliveryTime = mealInfo.getDeliveryTimes().stream()
                    .map(DateUtils::timeToString)
                    .toList();
            this.supportPrice = !(mealInfo instanceof CorporationMealInfo) ? bigDecimal : null;
        }
    }
}
