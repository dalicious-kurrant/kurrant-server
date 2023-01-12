package co.dalicious.domain.client.dto;

import co.dalicious.domain.client.entity.CorporationMealInfo;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.system.util.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private String clientName;

    @Getter
    @Setter
    public static class MealTypeInfo {
        private Integer diningType;
        private String serviceDays;
        private String lastOrderTime;
        private String deliveryTime;
        private BigDecimal supportPrice;

        @Builder
        public MealTypeInfo(MealInfo mealInfo) {
            this.diningType = mealInfo.getDiningType().getCode();
            this.serviceDays = mealInfo.getServiceDays();
            this.lastOrderTime = DateUtils.timeToString(mealInfo.getLastOrderTime()) ;
            this.deliveryTime =  DateUtils.timeToString(mealInfo.getDeliveryTime());
            this.supportPrice = (mealInfo instanceof CorporationMealInfo) ?
                        ((CorporationMealInfo) mealInfo).getSupportPrice() :
                        null;
        }
    }
}
