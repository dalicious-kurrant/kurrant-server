package co.dalicious.domain.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.GroupSequence;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class ExtraOrderDto {

    @Getter
    @Setter
    public static class Request {
        private String serviceDate;
        private String diningType;
        private BigInteger foodId;
        private BigInteger groupId;
        private BigInteger spotId;
        private BigDecimal price;
        private BigDecimal totalPrice;
        private String usage;
        private Integer count;
    }

    @Getter
    @Setter
    public static class Response {
        private String serviceDate;
        private String diningType;
        private String createdDateTime;
        private String usage;
        private BigInteger spotId;
        private String spotName;
        private BigInteger groupId;
        private String groupName;
        private BigDecimal price;
        private Integer count;
        private BigDecimal totalPrice;
        private String dailyFoodStatus;
        private String orderStatus;

        @Builder
        public Response(String serviceDate, String diningType, String createdDateTime, String usage, BigInteger spotId, String spotName, BigInteger groupId, String groupName, BigDecimal price, Integer count, BigDecimal totalPrice, String dailyFoodStatus, String orderStatus) {
            this.serviceDate = serviceDate;
            this.diningType = diningType;
            this.createdDateTime = createdDateTime;
            this.usage = usage;
            this.spotId = spotId;
            this.spotName = spotName;
            this.groupId = groupId;
            this.groupName = groupName;
            this.price = price;
            this.count = count;
            this.totalPrice = totalPrice;
            this.dailyFoodStatus = dailyFoodStatus;
            this.orderStatus = orderStatus;
        }
    }

    @Getter
    @Setter
    public static class DailyFoodList {
        private String serviceDate;
        private String diningType;
        private List<DailyFood> dailyFoods;
    }

    @Getter
    @Setter
    public static class DailyFood {
        private BigInteger foodId;
        private String foodName;
        private BigDecimal price;
        private String dailyFoodStatus;
        private Integer foodCapacity;
        private List<Group> groupList;

    }

    @Getter
    @Setter
    public static class Group {
        private BigInteger groupId;
        private String groupName;
    }

}
