package co.kurrant.app.admin_api.dto;

import co.dalicious.domain.client.dto.GroupInfo;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

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
        private String deliveryTime;
        private List<DeliveryMakers> makers;

        @Builder
        public DeliveryGroup(BigInteger groupId, String groupName, LocalTime deliveryTime, List<DeliveryMakers> makers) {
            this.groupId = groupId;
            this.groupName = groupName;
            this.deliveryTime = (deliveryTime == null) ? null : DateUtils.timeToString(deliveryTime);
            this.makers = makers;
        }
    }

    @Getter
    @Setter
    public static class DeliveryMakers {
        private BigInteger makersId;
        private String makersName;
        private String pickupTime;
        private List<DeliveryFood> foods;

        @Builder
        public DeliveryMakers(BigInteger makersId, String makersName, LocalTime pickupTime, List<DeliveryFood> foods) {
            this.makersId = makersId;
            this.makersName = makersName;
            this.pickupTime = (pickupTime == null) ? null : DateUtils.timeToString(pickupTime);
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

    @Getter
    @Setter
    @Builder
    public static class MakersGrouping {
        private LocalDate serviceDate;
        private DiningType diningType;
        private Group group;
        private Makers makers;

        public static MakersGrouping create(DailyFood dailyFood) {
            return MakersGrouping.builder()
                    .serviceDate(dailyFood.getServiceDate())
                    .group(dailyFood.getGroup())
                    .makers(dailyFood.getFood().getMakers())
                    .diningType(dailyFood.getDiningType())
                    .build();
        }

        public boolean equals(Object obj) {
            if(obj instanceof MakersGrouping tmp) {
                return serviceDate.equals(tmp.serviceDate) && group.equals(tmp.group) && makers.equals(tmp.makers) && diningType.equals(tmp.diningType);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(serviceDate, group, makers, diningType);
        }
    }

    @Getter
    @Setter
    @Builder
    public static class GroupGrouping {
        private LocalDate serviceDate;
        private DiningType diningType;
        private Group group;

        public static GroupGrouping create(MakersGrouping makersGrouping) {
            return GroupGrouping.builder()
                    .serviceDate(makersGrouping.getServiceDate())
                    .group(makersGrouping.getGroup())
                    .diningType(makersGrouping.diningType)
                    .build();
        }

        public boolean equals(Object obj) {
            if(obj instanceof GroupGrouping tmp) {
                return serviceDate.equals(tmp.serviceDate) && group.equals(tmp.group) && diningType.equals(tmp.diningType);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(serviceDate, group, diningType);
        }
    }
}
