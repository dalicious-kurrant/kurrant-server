package co.kurrant.app.admin_api.dto;

import co.dalicious.domain.client.dto.GroupInfo;
import co.dalicious.domain.client.entity.Group;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
public class DeliveryDto {
    private List<GroupInfo> groupInfoList;
    private List<DeliveryInfo> deliveryInfoList;

    @Getter
    @Setter
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
        private LocalTime deliveryTime;
        private List<DeliveryMakers> makers;

        @Builder
        public DeliveryGroup(BigInteger groupId, String groupName, LocalTime deliveryTime, List<DeliveryMakers> makers) {
            this.groupId = groupId;
            this.groupName = groupName;
            this.deliveryTime = deliveryTime;
            this.makers = makers;
        }
    }

    @Getter
    @Setter
    public static class DeliveryMakers {
        private BigInteger makersId;
        private String makersName;
        private LocalTime pickupTime;
        private List<DeliveryFood> foods;

        @Builder
        public DeliveryMakers(BigInteger makersId, String makersName, LocalTime pickupTime, List<DeliveryFood> foods) {
            this.makersId = makersId;
            this.makersName = makersName;
            this.pickupTime = pickupTime;
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

    public static DeliveryDto create (List<Group> groupList, List<DeliveryInfo> deliveryInfoList) {
        List<GroupInfo> groupInfos = groupList.stream().map(group -> GroupInfo.create(group.getId(), group.getName())).toList();

        return DeliveryDto.builder()
                .groupInfoList(groupInfos)
                .deliveryInfoList(deliveryInfoList)
                .build();
    }
}
