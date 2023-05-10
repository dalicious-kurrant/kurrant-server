package co.dalicious.domain.food.dto;

import co.dalicious.domain.food.entity.Makers;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class FoodListDto {
    List<MakersInfo> makersInfoList;
    List<FoodList> foodList;

    @Getter
    @Setter
    @Builder
    public static class MakersInfo {
        private BigInteger makersId;
        private String makersName;

        public static MakersInfo createMakersInfo(Makers makers) {
            return MakersInfo.builder()
                    .makersId(makers.getId())
                    .makersName(makers.getName())
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class FoodList {

        private BigInteger foodId;
        private String makersName;
        private BigInteger makersId;
        private String foodName;
        private String foodImage;
        private String foodStatus;
        private BigDecimal supplyPrice;
        private BigDecimal defaultPrice;
        private Integer membershipDiscount;
        private Integer makersDiscount;
        private Integer eventDiscount;
        private BigDecimal resultPrice;
        private String description;
        private List<String> foodTags;
        private Integer calorie;
        private Integer fat;
        private Integer protein;
        private Integer carbohydrate;

        @Builder
        public FoodList(BigInteger foodId, String makersName, BigInteger makersId, String foodName, String foodImage,
                        String foodStatus, BigDecimal supplyPrice, BigDecimal defaultPrice, Integer membershipDiscount, Integer makersDiscount,
                        Integer eventDiscount, BigDecimal resultPrice, String description, List<String> foodTags, Integer calorie,
                        Integer fat, Integer protein, Integer carbohydrate) {
            this.foodId = foodId;
            this.makersName = makersName;
            this.makersId = makersId;
            this.foodName = foodName;
            this.foodImage = foodImage;
            this.foodStatus = foodStatus;
            this.supplyPrice = supplyPrice;
            this.defaultPrice = defaultPrice;
            this.membershipDiscount = membershipDiscount;
            this.makersDiscount = makersDiscount;
            this.eventDiscount = eventDiscount;
            this.resultPrice = resultPrice;
            this.description = description;
            this.foodTags = foodTags;
            this.calorie = calorie;
            this.fat = fat;
            this.carbohydrate = carbohydrate;
            this.protein = protein;
        }
    }

    public static FoodListDto createFoodListDto(List<Makers> makersList, List<FoodList> foodList) {
        List<MakersInfo> makersInfos = new ArrayList<>();
        for(Makers makers : makersList) {
            MakersInfo makersInfo = MakersInfo.createMakersInfo(makers);
            makersInfos.add(makersInfo);
        }
        return FoodListDto.builder()
                .makersInfoList(makersInfos)
                .foodList(foodList).build();
    }
}
