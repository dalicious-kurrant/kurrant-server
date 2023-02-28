package co.kurrant.app.admin_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class FoodDto {
    public static class DailyFood {
        private BigInteger dailyFoodId;
        private String deliveryTime;
        private Integer diningType;
        private Integer foodCapacity;
        private Integer	foodCount;
        private String  foodName;
        private Integer foodStatus;
        private Integer  groupCapacity;
        private String groupName;
        private Integer makersCapacity;
        private String makersName;
        private String  makersPickupTime;
        private String serviceDate;
    }
}
