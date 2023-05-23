package co.dalicious.domain.recommend.entity;

import co.dalicious.system.enums.FoodTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class FoodRecommendType {
    private FoodTag foodTag;
    private Integer importance;

    public FoodRecommendType(FoodTag foodTag, Integer importance) {
        this.foodTag = foodTag;
        this.importance = importance;
    }
}
