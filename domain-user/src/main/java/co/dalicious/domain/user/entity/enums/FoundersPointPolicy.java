package co.dalicious.domain.user.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum FoundersPointPolicy {


    FOUNDERS_POINT_POLICY_1("파운더스 포인트","80", "80", 1)
    ;

    private final String value;
    private final String minPoint;
    private final String maxPoint;
    private final Integer code;

    FoundersPointPolicy(String value, String minPoint, String maxPoint, Integer code) {
        this.value = value;
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
        this.code = code;
    }


    public static FoundersPointPolicy ofCode(Integer code) {
        return Arrays.stream(FoundersPointPolicy.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }
}
