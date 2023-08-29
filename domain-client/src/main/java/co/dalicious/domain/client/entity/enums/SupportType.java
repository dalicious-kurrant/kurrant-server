package co.dalicious.domain.client.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SupportType {
    NONE(0, "미지원"),
    FIXED(1, "전액지원"),
    PARTIAL(2, "일부지원");

    private final Integer code;
    private final String membershipType;

    SupportType(Integer code, String membershipType) {
        this.code = code;
        this.membershipType = membershipType;
    }

    public static SupportType ofCode(Integer dbData) {
        return Arrays.stream(SupportType.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }
}
