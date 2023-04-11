package co.dalicious.domain.user.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum VeganLevel {

    VEGAN("비건", 0),
    LACTO("락토", 1),
    OVO("오보", 2),
    LACTO_OVO("락토 오보", 3),
    PESCO("페스코", 4),
    POLLO("폴로", 5),
    FLEXITARIAN("플렉시테리언", 6);

    private String name;
    private Integer level;

    VeganLevel(String name, Integer level){
        this.name = name;
        this.level = level;
    }


    public static VeganLevel ofCode(Integer code){
        return Arrays.stream(VeganLevel.values())
                .filter(v -> v.getLevel().equals(code))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }

    public static VeganLevel ofValue(String value){
        return Arrays.stream(VeganLevel.values())
                .filter(v -> v.getName().equals(value))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }


}
