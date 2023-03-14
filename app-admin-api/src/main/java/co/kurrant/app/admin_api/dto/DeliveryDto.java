package co.kurrant.app.admin_api.dto;

import co.dalicious.domain.client.dto.GroupInfo;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Makers;
import co.kurrant.app.admin_api.dto.schedules.ExcelPresetDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.geolatte.geom.M;

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

    @Getter
    @Setter
    @Builder
    public static class MakersGrouping {
        private LocalDate serviceDate;
        private Group group;
        private Makers makers;

        public static MakersGrouping create(DailyFood dailyFood) {
            return MakersGrouping.builder()
                    .serviceDate(dailyFood.getServiceDate())
                    .group(dailyFood.getGroup())
                    .makers(dailyFood.getFood().getMakers())
                    .build();
        }

        public boolean equals(Object obj) {
            if(obj instanceof MakersGrouping tmp) {
                return serviceDate.equals(tmp.serviceDate) && group.equals(tmp.group) && makers.equals(tmp.makers);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(serviceDate, group, makers);
        }
    }
}
