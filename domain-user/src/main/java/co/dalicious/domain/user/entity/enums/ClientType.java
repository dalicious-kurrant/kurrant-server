package co.dalicious.domain.user.entity.enums;

import lombok.Getter;

@Getter
public enum ClientType {
    APARTMENT("아파트", 0),
    CORPORATION("기업", 1);

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
            default -> throw new IllegalArgumentException("존재하지 않는 그룹입니다.");
        };
    }
}
