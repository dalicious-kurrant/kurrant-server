package co.kurrant.app.public_api.dto.client;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Getter
@NoArgsConstructor
public class ClientSpotDetailResDto {
    private String clientType;
    private BigInteger spotId;
    private String spotName;
    private String address;
    private List<MealTypeInfo> mealTypeInfoList;
    private String clientName;

    @Builder
    public ClientSpotDetailResDto(String clientType, BigInteger spotId, String spotName, String address, List<MealTypeInfo> mealTypeInfoList, String clientName) {
        this.clientType = clientType;
        this.spotId = spotId;
        this.spotName = spotName;
        this.address = address;
        this.mealTypeInfoList = mealTypeInfoList;
        this.clientName = clientName;
    }

    @Getter
    @NoArgsConstructor
    public static class MealTypeInfo {
        private String diningType;
        private String lastOrderTime;
        private String deliveryTime;
        private String supportPrice;

        @Builder
        public MealTypeInfo(String diningType, String lastOrderTime, String deliveryTime, String supportPrice) {
            this.diningType = diningType;
            this.lastOrderTime = lastOrderTime;
            this.deliveryTime = deliveryTime;
            this.supportPrice = supportPrice;
        }
    }
}
