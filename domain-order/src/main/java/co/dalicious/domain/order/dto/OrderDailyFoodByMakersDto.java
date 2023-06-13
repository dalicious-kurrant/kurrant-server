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
//        @Schema(description = "고객사별 식사일정")
//        private List<GroupFoodByDateDiningType> groupFoodByDateDiningTypes;
        @Schema(description = "고객사별 식사일정")
        private List<DeliveryGroupsByDate> deliveryGroupsByDates;
        @Schema(description = "메이커스 음식별 개수 및 상세정보")
        private List<Foods> totalFoods;
        @Schema(description = "메이커스 기간별 음식 개수")
        private List<FoodByDateDiningType> foodByDateDiningTypes;
    }

    @Getter
    @Setter
    public static class DeliveryGroupsByDate {
        private String serviceDate;
        private String diningType;
        private Integer spotCount;
        private List<DeliveryGroups> deliveryGroups;

        public Integer getSpotCount() {
            return deliveryGroups.stream()
                    .map(DeliveryGroups::getSpotCount)
                    .reduce(0, Integer::sum);
        }
    }

    @Getter
    @Setter
    public static class DeliveryGroups {
        private String deliveryTime;
        private Integer spotCount;
        private List<Food> foods;
        private Integer foodCount;
        private List<FoodBySpot> foodBySpots;

        public Integer getFoodCount() {
            return this.foods.stream()
                    .map(Food::getFoodCount)
                    .reduce(0, Integer::sum);
        }
        public Integer getSpotCount() {
            return foodBySpots.size();
        }
    }

    @Getter
    @Setter
    public static class FoodBySpot {
        private String deliveryId;
        private Integer spotType;
        private String pickUpTime;
        private String address1;
        private String address2;
        private String spotName;
        private String groupName;
        private String userName;
        private String phone;
        private List<Food> foods;
        private Integer foodCount;

        public Integer getFoodCount() {
            return this.foods.stream()
                    .map(Food::getFoodCount)
                    .reduce(0, Integer::sum);
        }
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
        private String description;
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
    public static class FoodByGroup {
        private BigInteger groupId;
        private String groupName;
        private List<SpotByDateDiningType> spotByDateDiningTypes;
    }

    @Getter
    @Setter
    public static class SpotByDateDiningType {
        private String pickupTime;
        private List<String> deliveryTime;
        private BigInteger spotId;
        private String spotName;
        private List<Food> foods;
    }
}
