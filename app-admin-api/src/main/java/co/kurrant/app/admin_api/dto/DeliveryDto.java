package co.kurrant.app.admin_api.dto;

import co.dalicious.domain.client.dto.GroupInfo;
import co.dalicious.domain.client.dto.SpotInfo;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.N;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class DeliveryDto {
    private List<GroupInfo> groupInfoList;
    private List<SpotInfo> spotInfoList;
    private List<DeliveryInfo> deliveryInfoList;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DeliveryInfo {
        private String serviceDate;
        private List<DeliveryGroup> group;

        @Builder
        public DeliveryInfo(String serviceDate, List<DeliveryGroup> group) {
            this.serviceDate = serviceDate;
            this.group = group;
        }
    }

    @Getter
    @Setter
    public static class DeliveryGroup {
        private BigInteger groupId;
        private String groupName;
        private String spotName;
        private BigInteger spotId;
        private String address;
        private String deliveryTime;
        private Integer diningType;
        private List<DeliveryMakers> makers;

        @Builder
        public DeliveryGroup(BigInteger groupId, String groupName, String spotName, BigInteger spotId, String address, LocalTime deliveryTime, Integer diningType, List<DeliveryMakers> makers) {
            this.groupId = groupId;
            this.groupName = groupName;
            this.spotName = spotName;
            this.spotId = spotId;
            this.address = address;
            this.deliveryTime = (deliveryTime == null) ? null : DateUtils.timeToString(deliveryTime);
            this.diningType = diningType;
            this.makers = makers;
        }
    }

    @Getter
    @Setter
    public static class DeliveryMakers {
        private BigInteger makersId;
        private String makersName;
        private String pickupTime;
        private String address;
        private List<DeliveryFood> foods;

        @Builder
        public DeliveryMakers(BigInteger makersId, String makersName, LocalTime pickupTime, List<DeliveryFood> foods, String address) {
            this.makersId = makersId;
            this.makersName = makersName;
            this.pickupTime = (pickupTime == null) ? null : DateUtils.timeToString(pickupTime);
            this.address = address;
            this.foods = foods;
        }
    }

    @Getter
    @Setter
    public static class DeliveryFood {
        private BigInteger foodId;
        private String foodName;
        private Integer foodCount;

        @Builder
        public DeliveryFood(BigInteger foodId, String foodName, Integer foodCount) {
            this.foodId = foodId;
            this.foodName = foodName;
            this.foodCount = foodCount;
        }
    }

}
