package co.dalicious.system.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum FoodTag {
    STYLE_KOREAN("요리스타일", "한식", 1),
    STYLE_SCHOOL_FOOD("요리스타일", "분식", 2),
    STYLE_CHINESE("요리스타일", "중식", 3),
    STYLE_JAPANESE("요리스타일", "일식", 4),
    STYLE_WESTERN("요리스타일", "양식", 5),
    STYLE_ASIAN("요리스타일", "동남아", 6),
    STYLE_INDIAN("요리스타일", "인도", 7),


    COUNTRY_KOREA("국가", "한국", 1001),
    COUNTRY_CHINA("국가", "중국", 1002),
    COUNTRY_HONG_KONG("국가", "홍콩", 1003),
    COUNTRY_TAIWAN("국가", "대만", 1004),
    COUNTRY_JAPAN("국가", "일본", 1005),
    COUNTRY_AMERICA("국가", "미국", 1006),
    COUNTRY_MEXICO("국가", "멕시코", 1007),
    COUNTRY_SPAIN("국가", "스페인", 1008),
    COUNTRY_MEDITERRANEAN("국가", "지중해", 1009),
    COUNTRY_ITALY("국가", "이탈리아", 1010),
    COUNTRY_FRANCE("국가", "프랑스", 1011),
    COUNTRY_VIETNAM("국가", "베트남", 1012),
    COUNTRY_THAILAND("국가", "태국", 1013),
    COUNTRY_TURKIYE("국가", "튀르키예", 1014),
    COUNTRY_INDIA("국가", "인도", 1015),
    COUNTRY_GERMANY("국가", "독일", 1016),
    COUNTRY_INDONESIA("국가", "인도네시아", 1017),
    COUNTRY_GREECE("국가", "그리스", 1018),
    COUNTRY_EGYPT("국가", "이집트", 1019),
    COUNTRY_BRAZIL("국가", "브라질", 1020),
    COUNTRY_RUSIA("국가", "러시아", 1021),
    COUNTRY_PHILIPPINES("국가", "필리핀", 1022),
    COUNTRY_MONGOLIA("국가", "몽골", 1023),
    COUNTRY_UNITED_KINGDOM("국가", "영국", 1024),

    FOOD_TYPE_RICE_CAKE("식품유형", "떡류", 2001),
    FOOD_TYPE_BREAD("식품유형", "빵류", 2002),
    FOOD_TYPE_NOODLE("식품유형", "면류", 2003),
    FOOD_TYPE_DIMPLING("식품유형", "만두류", 2004),
    FOOD_TYPE_KIMCHI("식품유형", "김치류", 2005),
    FOOD_TYPE_ROW_FISH("식품유형", "회류", 2006),
    FOOD_TYPE_FERMENTED_SEAFOOD("식품유형", "젓갈류", 2007),
    FOOD_TYPE_PICKLES("식품유형", "장아찌류", 2008),
    FOOD_TYPE_SEASONING("식품유형", "양념류", 2009),
    FOOD_TYPE_DAIRY("식품유형", "유제품류", 2010),
    FOOD_TYPE_BEVERAGE("식품유형", "음료", 2011),
    FOOD_TYPE_ALCOHOL("식품유형", "주류", 2012),
    FOOD_TYPE_TEA("식품유형", "차류", 2013),
    FOOD_TYPE_FRUIT("식품유형", "과일류", 2014),
    FOOD_TYPE_SINGLE_PRODUCT("식품유형", "단일 식품", 2015),
    FOOD_TYPE_FROZEN_PRODUCT("식품유형", "냉동 식품", 2016),
    FOOD_TYPE_CONGEE("식품유형", "죽류", 2017),
    FOOD_TYPE_SOUP("식품유형", "국/탕류", 2018),
    FOOD_TYPE_STEW("식품유형", "찌개류", 2019),
    FOOD_TYPE_STEAM("식품유형", "찜류", 2020),
    FOOD_TYPE_GRILL("식품유형", "구이류", 2021),
    FOOD_TYPE_PANCAKE("식품유형", "전/부침류", 2022),
    FOOD_TYPE_SALAD("식품유형", "샐러드류", 2023),
    FOOD_TYPE_RICE("식품유형", "밥류", 2024),


    INGREDIENT_BEEF("주재료", "소고기", 3001),
    INGREDIENT_PORK("주재료", "돼지고기", 3002),
    INGREDIENT_CHICKEN("주재료", "닭고기", 3003),
    INGREDIENT_DUCK("주재료", "오리고기", 3004),
    INGREDIENT_LAMB("주재료", "양고기", 3005),
    INGREDIENT_FISH("주재료", "생선류", 3006),
    INGREDIENT_CLAM("주재료", "조개류", 3007),
    INGREDIENT_CRAB("주재료", "갑각류", 3008),
    INGREDIENT_OCTOPUS("주재료", "연체류", 3009),
    INGREDIENT_VEGETABLE("주재료", "채소류", 3010),
    INGREDIENT_GRAIN("주재료", "곡류", 3011),
    INGREDIENT_NAN("주재료", "난류", 3012),
    INGREDIENT_MUSHROOM("주재료", "버섯류", 3013),

    WAY_STIR_FRY("조리법", "볶다", 4001),
    WAY_STEAM("조리법", "찌다", 4002),
    WAY_DEEP_FRY("조리법", "튀기다", 4003),
    WAY_GRILL("조리법", "굽다", 4004),
    WAY_BOIL("조리법", "삶다", 4005),
    WAY_STRONG_BOIL("조리법", "끓이다", 4006),
    WAY_BAKE("조리법", "베이킹", 4007),
    WAY_SIMMER("조리법", "졸이다", 4008),
    WAY_PICKLE("조리법", "절이다", 4009),


    TEMPERATURE_NORMAL("온도", "상온", 5001),
    TEMPERATURE_COOL("온도", "시원함", 5002),
    TEMPERATURE_COLD("온도", "차가움", 5003),
    TEMPERATURE_WARM("온도", "따뜻함", 5004),
    TEMPERATURE_HOT("온도", "뜨거움", 5005),

    TASTE_SWEET("맛", "단맛", 6001),
    TASTE_SPICY("맛", "매운맛", 6002),
    TASTE_SALTY("맛", "짠맛", 6003),
    TASTE_BITTER("맛", "쓴맛", 6004),
    TASTE_SOUR("맛", "신맛", 6005),
    TASTE_UMAMI("맛", "감칠맛", 6006),

    FEATURE_HEARTY("메뉴성격", "든든한", 7001),
    FEATURE_LIGHT("메뉴성격", "가벼운", 7002),
    FEATURE_HEAVY("메뉴성격", "헤비한", 7003),
    FEATURE_EASY("메뉴성격", "간편한", 7004),
    FEATURE_HEALTHY("메뉴성격", "건강한", 7005),
    FEATURE_OILY("메뉴성격", "기름진", 7006),
    FEATURE_TINGLY("메뉴성격", "얼얼한", 7007),
    FEATURE_SWEET_SALTY("메뉴성격", "단짠단짠", 7008),
    FEATURE_SWEET_SOUR("메뉴성격", "새콤달콤", 7009),
    FEATURE_WATERY("메뉴성격", "싱거운", 7010),
    FEATURE_SMOKY("메뉴성격", "불맛", 7011),
    FEATURE_GREASY("메뉴성격", "느끼한", 7012),
    FEATURE_CHEWY("메뉴성격", "쫄깃한", 7013),
    FEATURE_CRUNCHY("메뉴성격", "아삭한", 7014),
    FEATURE_FRESH("메뉴성격", "신선한", 7015),
    FEATURE_MILD("메뉴성격", "순한", 7016),
    FEATURE_MOIST("메뉴성격", "촉촉한", 7017),
    FEATURE_TANGY("메뉴성격", "톡쏘는", 7018),
    FEATURE_SAVORY("메뉴성격", "구수한", 7019),
    FEATURE_BUTTERY("메뉴성격", "버터향이 나는", 7020),


    ALLERGY_MILK("알레르기 체크", "우유", 8001),
    ALLERGY_BUCK_WHEAT("알레르기 체크", "메밀", 8002),
    ALLERGY_PEANUT("알레르기 체크", "땅콩", 8003),
    ALLERGY_SOYBEAN("알레르기 체크", "대두", 8004),
    ALLERGY_WHEAT("알레르기 체크", "밀", 8005),
    ALLERGY_MACKEREL("알레르기 체크", "고등어", 8006),
    ALLERGY_CRAB("알레르기 체크", "게", 8007),
    ALLERGY_SHRIMP("알레르기 체크", "새우", 8008),
    ALLERGY_PORK("알레르기 체크", "돼지고기", 8009),
    ALLERGY_SULFITE("알레르기 체크", "아황산류", 8010),
    ALLERGY_PEACH("알레르기 체크", "복숭아", 8011),
    ALLERGY_TOMATO("알레르기 체크", "토마토", 8012),
    ALLERGY_WALNUT("알레르기 체크", "호두", 8013),
    ALLERGY_CHICKEN("알레르기 체크", "닭고기", 8014),
    ALLERGY_EGG("알레르기 체크", "알류", 8015),
    ALLERGY_BEEF("알레르기 체크", "쇠고기", 8016),
    ALLERGY_SQUID("알레르기 체크", "오징어", 8017),
    ALLERGY_CLAM("알레르기 체크", "조개류", 8018),
    ALLERGY_PINE_NUT("알레르기 체크", "잣", 8019),
    ALLERGY_ABALONE("알레르기 체크", "전복", 8020),
    ALLERGY_OYSTER("알레르기 체크", "굴", 8021),
    ALLERGY_MUSSEL("알레르기 체크", "홍합", 8022),


    APPETITE_VEGETARIAN("특이식성", "채식", 9001),
    APPETITE_GLUTEN_FREE("특이식성", "글루텐프리", 9002),
    APPETITE_HALAL_FOOD("특이식성", "할랄푸드", 9003),
    APPETITE_KITO("특이식성", "키토제닉", 9004),
    APPETITE_DIET("특이식성", "다이어트", 9005),
    APPETITE_LOW_SALT("특이식성", "저염식", 9006),


    SPICY_LEVEL_0("맵기", "맵지 않음", 10001),
    SPICY_LEVEL_1("맵기", "약간 매움", 10002),
    SPICY_LEVEL_2("맵기", "신라면 맵기", 10003),
    SPICY_LEVEL_3("맵기", "불닭볶음면 맵기", 10004);



    private final String category;
    private final String tag;
    private final Integer code;

    FoodTag(String category, String tag, Integer code) {
        this.category = category;
        this.tag = tag;
        this.code = code;
    }

    public static FoodTag ofCode(Integer dbData) {
        return Arrays.stream(FoodTag.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }

    public static FoodTag ofString(String tag) {
        return Arrays.stream(FoodTag.values())
                .filter(v -> v.getTag().equals(tag))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }

    public static List<FoodTag> ofCodes(List<Integer> dbData) {
        List<FoodTag> foodTags = new ArrayList<>();
        for (Integer code : dbData) {
            FoodTag foodTag = FoodTag.ofCode(code);
            if (foodTag != null) {
                foodTags.add(foodTag);
            }
        }
        if (foodTags.isEmpty()) {
            throw new ApiException(ExceptionEnum.ENUM_NOT_FOUND);
        }
        return foodTags;
    }

    public static List<FoodTag> ofCategory(FoodCategory foodCategory) {
        return Arrays.stream(FoodTag.values())
                .filter(v -> v.getCategory().equals(foodCategory.getFoodCategory()))
                .toList();
    }
}
