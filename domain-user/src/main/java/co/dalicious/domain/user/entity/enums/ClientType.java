package co.dalicious.domain.user.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

@Getter
public enum  ClientType {
    APARTMENT("아파트", 0),
    CORPORATION("기업", 1),
    OPEN_GROUP("오픈 그룹", 2),;

    private final String client;
    private final Integer code;

    ClientType(String client, Integer code) {
        this.client = client;
        this.code = code;
    }

    public static ClientType ofCode(Integer dbData) {
        return switch (dbData) {
            case 0 -> APARTMENT;
            case 1 -> CORPORATION;
            case 2 -> OPEN_GROUP;
            default -> throw new ApiException(ExceptionEnum.ENUM_NOT_FOUND);
        };
    }
}
