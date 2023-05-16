package co.dalicious.client.core.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ControllerType {
    DAILY_FOOD("DailyFoodController", "식단", 1),
    BANNER("BannerController", "배너", 2),
    AUTH("AuthController","인증", 3),
    DELIVERY("DeliveryController", "배송", 4),
    FILE("FileController", "파일", 5),
    FOOD("FoodController", "음식", 6),
    GROUP("GroupController", "기업", 7),
    MAKERS("MakersController", "메이커스", 8),
    ORDER_DAILY_FOOD("OrderDailyFoodController", "주문", 8),
    PAYCHECK("PaycheckController", "정산", 9),
    POINT("PointController", "포인트", 10),
    PUBLIC("PublicController", "공용", 11),
    PUSH_ALARM("PushAlarmController", "푸시 알림", 12),
    REVIEWS("ReviewsController", "리뷰", 13),
    SCHEDULE("ScheduleController", "메이커스 식단 일정", 14),
    SPOT("SpotController", "스팟", 15),
    USER("UserController", "유저", 16);
    private final String controller;
    private final String type;
    private final Integer code;

    ControllerType(String controller, String type, Integer code) {
        this.controller = controller;
        this.type = type;
        this.code = code;
    }

    public static ControllerType ofCode(Integer dbData) {
        return Arrays.stream(ControllerType.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElse(null);
    }

    public static ControllerType ofController(String controller) {
        return Arrays.stream(ControllerType.values())
                .filter(v -> v.getController().equals(controller))
                .findAny()
                .orElse(null);
    }
}
