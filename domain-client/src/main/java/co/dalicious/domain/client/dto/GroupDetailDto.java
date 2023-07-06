package co.dalicious.domain.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class GroupDetailDto {
    private BigInteger id;
    private String name;
    private String address;
    private String phone;
    private Integer userCount;
    private List<Integer> diningTypes;
    private List<MealInfo> mealInfos;
    private List<SpotInfo> spots;

    @Builder
    public GroupDetailDto(BigInteger id, String name, String address, String phone, Integer userCount, List<Integer> diningTypes, List<MealInfo> mealInfos, List<SpotInfo> spots) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.userCount = userCount;
        this.diningTypes = diningTypes;
        this.mealInfos = mealInfos;
        this.spots = spots;
    }

    @Getter
    @Setter
    public static class MealInfo {
        private Integer diningType;
        private String lastOrderTime;
        private String membershipBenefitTime;
        private List<String> deliveryTimes;

        @Builder
        public MealInfo(Integer diningType, String lastOrderTime, String membershipBenefitTime, List<String> deliveryTimes) {
            this.diningType = diningType;
            this.lastOrderTime = lastOrderTime;
            this.membershipBenefitTime = membershipBenefitTime;
            this.deliveryTimes = deliveryTimes;
        }
    }

    @Getter
    @Setter
    public static class SpotInfo {
        private BigInteger spotId;
        private String spotName;
        private Boolean isRestriction;

        @Builder
        public SpotInfo(BigInteger spotId, String spotName, Boolean isRestriction) {
            this.spotId = spotId;
            this.spotName = spotName;
            this.isRestriction = isRestriction;
        }
    }
}
