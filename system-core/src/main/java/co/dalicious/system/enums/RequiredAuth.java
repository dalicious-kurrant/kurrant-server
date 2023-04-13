package co.dalicious.system.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RequiredAuth {
    SIGNUP("1"),
    FIND_ID("2"),
    FIND_PASSWORD("3"),
    MYPAGE_CHANGE_PHONE_NUMBER("4"),
    MYPAGE_SETTING_EMAIL_AND_PASSWORD("5"),
    PAYMENT_PASSWORD_CHECK("6"),
    PAYMENT_PASSWORD_CREATE("7"),
    PAYMENT_PASSWORD_CREATE_APPLE("8");

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
