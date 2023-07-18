package co.dalicious.domain.food.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class FoodDto {
    @Getter
    @Setter
    public static class DailyFood {
        private BigInteger dailyFoodId;
        private List<String> deliveryTime;
        private Integer diningType;
        private Integer foodCapacity;
        private Integer foodCount;
        private String foodName;
        private Integer foodStatus;
        private Integer groupCapacity;
        private String groupName;
        private Integer makersCapacity;
        private String makersName;
        private List<String> makersPickupTime;
        private String serviceDate;
    }
}
