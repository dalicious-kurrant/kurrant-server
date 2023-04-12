package co.dalicious.client.alarm.entity.enums;

import co.dalicious.client.alarm.converter.PushStatusConverter;
import co.dalicious.system.enums.DiningType;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PushStatus {
    ACTIVE("활성", 0),
    INACTIVE("비활성", 1);

    private final String status;
    private final Integer code;

    PushStatus(String status, Integer code) {
        this.status = status;
        this.code = code;
    }

    public static PushStatus ofCode(Integer code) {
        return Arrays.stream(PushStatus.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }
}
