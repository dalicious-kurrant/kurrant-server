package co.dalicious.domain.makers.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ServiceType {
    KOREAN("한식", 0),
    CHINESE("중식", 1),
    JAPANESE("일식", 2),
    WESTERN("양식", 3),
    SCHOOL_FOOD("분식", 4),
    ASIAN("아시안", 5),
    ETC("기타", 6);

    private final String serviceType;
    private final Integer code;

    ServiceType(String serviceType, Integer code) {
        this.serviceType = serviceType;
        this.code = code;
    }

    public static ServiceType ofCode(Integer dbData) {
        return Arrays.stream(ServiceType.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }
}
