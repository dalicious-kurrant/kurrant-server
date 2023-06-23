package co.kurrant.app.admin_api.dto.delivery;

import co.dalicious.domain.client.dto.GroupInfo;
import co.dalicious.domain.client.dto.SpotInfo;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.system.util.DateUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
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
        private List<DeliveryMakers> makersList;
    }

    @Getter
    @Setter
    public static class DeliveryMakers {
        private BigInteger makersId;
        private String makersName;
        private String pickupTime;
        private String address;
        private List<DeliveryFood> foods;
    }

    @Getter
    @Setter
    public static class DeliveryFood {
        private BigInteger foodId;
        private String foodName;
        private Integer foodCount;
    }

    @Getter
    public static class DeliveryManifest {
        private String spotType;
        private String serviceDate;
        private Integer diningType;
        private String deliveryTime;
        private String orderNumber;
        private String makersName;
        private String makersAddress;
        private String makersPhone;
        private String foodName;
        private Integer count;
        private String userName;
        private String userAddress;
        private String userPhone;
        private String memo;

        @Builder
        public DeliveryManifest(String spotType, String serviceDate, Integer diningType, String deliveryTime, String orderNumber, String makersName, String makersAddress, String makersPhone, String foodName, Integer count, String userName, String userAddress, String userPhone, String memo) {
            this.spotType = spotType;
            this.serviceDate = serviceDate;
            this.diningType = diningType;
            this.deliveryTime = deliveryTime;
            this.orderNumber = orderNumber;
            this.makersName = makersName;
            this.makersAddress = makersAddress;
            this.makersPhone = makersPhone;
            this.foodName = foodName;
            this.count = count;
            this.userName = userName;
            this.userAddress = userAddress;
            this.userPhone = userPhone;
            this.memo = memo;
        }
    }

    public static DeliveryDto create (List<Group> groupList, List<DeliveryInfo> deliveryInfoList, List<Spot> spotList) {
        List<GroupInfo> groupInfos = groupList.stream().map(group -> GroupInfo.create(group.getId(), group.getName())).toList();
        List<SpotInfo> spotInfos = spotList.stream().map(spot -> SpotInfo.create(spot.getId(), spot.getName())).toList();

        return DeliveryDto.builder()
                .groupInfoList(groupInfos)
                .spotInfoList(spotInfos)
                .deliveryInfoList(deliveryInfoList)
                .build();
    }
}
