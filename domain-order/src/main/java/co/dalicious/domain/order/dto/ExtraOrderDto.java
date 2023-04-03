package co.dalicious.domain.order.dto;

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
