package co.dalicious.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "메이커스 별 주문 수량 조회 DTO")
public class OrderDailyFoodByMakersDto {
    @Getter
    @Setter
    public static class ByPeriod {
        private List<GroupFoodByDateDiningType> groupFoodByDateDiningTypes;
        private List<Foods> totalFoods;
        private List<FoodByDateDiningType> foodByDateDiningTypes;
    }
    @Getter
    @Setter
    public static class FoodByDateDiningType {
        private String serviceDate;
        private String diningType;
        private Integer totalCount;
        private List<Food> foods;
    }

    @Getter
    @Setter
    public static class Food {
        private BigInteger foodId;
        private Integer foodCount;
        private String foodName;
    }

    @Getter
    @Setter
    public static class Foods {
        private BigInteger foodId;
        private String FoodName;
        private Integer totalFoodCount;
    }

    @Getter
    @Setter
    public static class GroupFoodByDateDiningType {
        private String serviceDate;
        private String diningType;
        List<FoodByGroup> foodByGroups;
    }

    @Getter
    @Setter
    public static class FoodByGroup{
        private BigInteger groupId;
        private String groupName;
        private List<SpotByDateDiningType> spotByDateDiningTypes;
    }

    @Getter
    @Setter
    public static class SpotByDateDiningType {
        private String deliveryTime;
        private BigInteger spotId;
        private String spotName;
        private List<Food> foods;
    }
}
