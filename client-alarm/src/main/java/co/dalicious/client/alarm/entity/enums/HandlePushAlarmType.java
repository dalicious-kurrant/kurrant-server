package co.dalicious.client.alarm.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum HandlePushAlarmType {
    ALL("전체", 0),
    GROUP("스팟", 1),
    SPOT("상세 스팟", 2),
    USER("유저", 3);

    private final String type;
    private final Integer code;

    HandlePushAlarmType(String type, Integer code) {
        this.type = type;
        this.code = code;
    }

    public static HandlePushAlarmType ofCode(Integer code) {
        return Arrays.stream(HandlePushAlarmType.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }
}
