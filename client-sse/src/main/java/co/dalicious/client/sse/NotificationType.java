package co.dalicious.client.sse;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum NotificationType {
    ALL_NOTY("전체공지", 1),
    SPOT_NOTY("스팟공지", 2),
    REVIEW_NOTY("구매후기", 3);

    private String notificationType;
    private Integer code;

    NotificationType(String notificationType, Integer code) {
        this.notificationType = notificationType;
        this.code = code;
    }

    public static NotificationType ofCode(Integer code) {
        return Arrays.stream(NotificationType.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow();

    }
}
