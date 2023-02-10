package co.dalicious.domain.user.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum UserStatus {
    INACTIVE("탈퇴", 0),
    ACTIVE("가입된 유저", 1),
    REQUEST_WITHDRAWAL("탈퇴 요청", 2);

    private final String userStatus;
    private final Integer code;

    UserStatus(String userStatus, Integer code) {
        this.userStatus = userStatus;
        this.code = code;
    }

    public static UserStatus ofCode(Integer dbData) {
        return Arrays.stream(UserStatus.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }
}
