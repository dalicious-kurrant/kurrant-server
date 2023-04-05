package co.dalicious.domain.user.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BirthPlace {

    SEOUL("서울", 1),
    INCHEON("인천",2),
    DAEJEON("대전", 3),
    DAEGU("대구", 4),
    ULSAN("울산", 5),
    GWANGJU("광주", 6),
    SEJONG("세종", 7),
    GYEONGGI("경기", 8),
    GANGWON("강원", 9),
    CHUNG_CHEONG_BUKDO("충북", 10),
    CHUNG_CHEONG_NAMDO("충남", 11),
    GYEONGSANG_NAMDO("경남", 12),
    GYEONGSANG_BUKDO("경북", 13),
    JEOLLA_BUKDO("전북", 14),
    JEOLLA_NAMDO("전남", 15),
    JEJU("제주", 16),
    OVERSEAS("해외", 17);

    private final String place;
    private final Integer code;

    BirthPlace(String place, Integer code){
        this.place = place;
        this.code = code;
    }

    public static BirthPlace ofCode(Integer code){
        return Arrays.stream(BirthPlace.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }

    public static BirthPlace ofValue(String value){
        return  Arrays.stream(BirthPlace.values())
                .filter(v -> v.getPlace().equals(value))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }
}
