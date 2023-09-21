package co.dalicious.client.core.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

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
    ORDER_DAILY_FOOD("OrderDailyFoodController", "주문", 9),
    PAYCHECK("PaycheckController", "정산", 10),
    POINT("PointController", "포인트", 11),
    PUBLIC("PublicController", "공용", 12),
    PUSH_ALARM("PushAlarmController", "푸시 알림", 13),
    REVIEWS("ReviewsController", "리뷰", 14),
    SCHEDULE("ScheduleController", "메이커스 식단 일정", 15),
    SPOT("SpotController", "스팟", 16),
    USER("UserController", "유저", 17),
    LOGS("LogController", "로그", 18),
    APPLICATION_FORM("ApplicationFormController", "스팟 신청", 19),
    DRIVER("DriverController", "배송 기사", 19),
    BOARD("BoardController", "공지사항", 20),
    HOME("HomeController", "홈", 21),
    QR("QrController", "QR(넥스트 페이먼츠)", 22),
    ;
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

    public static List<ControllerType> ofCodes(List<Integer> integers) {
        if(integers == null) return null;
        return integers.stream()
                .map(ControllerType::ofCode)
                .toList();
    }
}
