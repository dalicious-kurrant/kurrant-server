package co.dalicious.domain.client.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SparkPlusLogType {
    POSTER_COMBINATION("종합", 1),
    POSTER_DELIVERY_FEE("배송비", 2),
    POSTER_MEMBERSHIP_DISCOUNT("멤버십할인", 3),
    POSTER_TIME("시간", 4),
    POSTER_LOCAL_HOT_PLACE("주변 맛집", 5),
    POSTER_HEALTHY_FOOD("건강식", 6),
    POSTER_SUPPORT_PRICE("지원금", 7);

    private final String logType;
    private final Integer code;

    SparkPlusLogType(String logType, Integer code) {
        this.logType = logType;
        this.code = code;
    }

    public static SparkPlusLogType ofCode(Integer dbData) {
        return Arrays.stream(SparkPlusLogType.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElse(null);
    }
}
