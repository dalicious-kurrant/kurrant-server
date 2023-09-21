package co.dalicious.domain.client.entity.enums;

import co.dalicious.domain.client.entity.*;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum GroupDataType {
    CORPORATION("기업", 0),
    MY_SPOT("마이스팟", 1),
    OPEN_GROUP("오픈스팟", 2),
    EAT_IN("매장스팟", 3);

    private final String type;
    private final Integer code;

    GroupDataType(String type, Integer code) {
        this.type = type;
        this.code = code;
    }

    public static GroupDataType ofCode(Integer dbData) {
        return Arrays.stream(GroupDataType.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹 상태입니다."));
     }
    public static GroupDataType ofString(String groupDataType) {
        return Arrays.stream(GroupDataType.values())
                .filter(v -> v.getType().equals(groupDataType))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹 상태입니다."));
    }

    public static GroupDataType ofClass(Class<? extends Spot> spotClass) {
        if(spotClass.equals(CorporationSpot.class)) return CORPORATION;
        if(spotClass.equals(OpenGroupSpot.class)) return OPEN_GROUP;
        if(spotClass.equals(MySpot.class)) return MY_SPOT;
        if(spotClass.equals(EatInSpot.class)) return EAT_IN;
        return null;
    }
}
