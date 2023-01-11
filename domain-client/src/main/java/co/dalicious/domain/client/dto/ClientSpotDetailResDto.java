package co.dalicious.domain.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@NoArgsConstructor
public class ClientSpotDetailResDto {
    private BigInteger spotId;
    private String spotName;
    private String address;
    private List<MealTypeInfo> mealTypeInfoList;
    private String clientName;

    @Builder
    public ClientSpotDetailResDto( BigInteger spotId, String spotName, String address, List<MealTypeInfo> mealTypeInfoList, String clientName) {
        this.spotId = spotId;
        this.spotName = spotName;
        this.address = address;
        this.mealTypeInfoList = mealTypeInfoList;
        this.clientName = clientName;
    }

    @Getter
    @NoArgsConstructor
    public static class MealTypeInfo {
        private Integer diningType;
        private String serviceDays;
        private String lastOrderTime;
        private String deliveryTime;
        private BigDecimal supportPrice;

        @Builder
        public MealTypeInfo(Integer diningType, String serviceDays, String lastOrderTime, String deliveryTime, BigDecimal supportPrice) {
            this.diningType = diningType;
            this.serviceDays = serviceDays;
            this.lastOrderTime = lastOrderTime;
            this.deliveryTime = deliveryTime;
            this.supportPrice = supportPrice;
        }
    }
}
