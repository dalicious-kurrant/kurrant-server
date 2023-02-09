package co.dalicious.system.util;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RequiredAuth {
    SIGNUP("1"),
    FIND_ID("2"),
    FIND_PASSWORD("3"),
    MYPAGE_CHANGE_PHONE_NUMBER("4");

    private final String id;

    RequiredAuth(String id) {
        this.id = id;
    }

    public static RequiredAuth ofId(String id) {
        return Arrays.stream(RequiredAuth.values())
                .filter(v -> v.getId().equals(id))
                .findAny()
                .orElseThrow( () -> new IllegalArgumentException("존재하지 않은 인증 요청 타입입니다."));
    }
}
