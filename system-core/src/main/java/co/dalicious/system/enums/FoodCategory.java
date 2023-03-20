package co.dalicious.system.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum FoodCategory {
    CATEGORY_STYLE("요리스타일", 1),
    CATEGORY_COUNTRY("국가", 2),
    CATEGORY_FOOD_TYPE("식품유형", 3),
    CATEGORY_INGREDIENT("주재료", 4),
    CATEGORY_WAY("조리법", 5),
    CATEGORY_TASTE("맛", 6),
    CATEGORY_FEATURE("메뉴성격", 7),
    CATEGORY_ALLERGY("알레르기 체크", 8),
    CATEGORY_APPETITE("특이식성", 9),
    CATEGORY_SPICY("맵기", 10);

    private final String foodCategory;
    private final Integer code;

    FoodCategory(String foodCategory, Integer code) {
        this.foodCategory = foodCategory;
        this.code = code;
    }

    public static FoodCategory ofCode(Integer dbData) {
        return Arrays.stream(FoodCategory.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElse(null);
    }
}
