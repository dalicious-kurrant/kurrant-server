package co.dalicious.domain.user.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PointStatus {
    REWARD("적립", 0),
    USED("사용", 1);

    private final String type;
    private final Integer code;

    PointStatus(String type, Integer code) {
        this.type = type;
        this.code = code;
    }

    public static PointStatus ofCode(Integer dbData) {
        return Arrays.stream(PointStatus.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }
}
