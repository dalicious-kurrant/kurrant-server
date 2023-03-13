package co.dalicious.domain.client.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum GroupDataType {
    CORPORATION("기업", 0),
    APARTMENT("아파트", 1),
    OPEN_GROUP("오픈 스팟", 2);

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

}
