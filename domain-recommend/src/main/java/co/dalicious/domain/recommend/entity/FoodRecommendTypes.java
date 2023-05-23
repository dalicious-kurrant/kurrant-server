package co.dalicious.domain.recommend.entity;

import co.dalicious.domain.recommend.converter.FoodRecommendTypeConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class FoodRecommendTypes {
    @Comment("식품 타입 번호")
    private Integer order;

    @Convert(converter = FoodRecommendTypeConverter.class)
    @Comment("음식 추천 타입 리스트")
    private List<FoodRecommendType> foodRecommendTypes;

    public FoodRecommendTypes(Integer order, List<FoodRecommendType> foodRecommendTypes) {
        this.order = order;
        this.foodRecommendTypes = foodRecommendTypes;
    }
}
