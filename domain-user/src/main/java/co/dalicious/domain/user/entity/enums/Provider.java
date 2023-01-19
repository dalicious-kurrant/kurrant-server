package co.dalicious.domain.user.entity.enums;

import lombok.Getter;

@Getter
public enum Provider {
    GENERAL("GENERAL"),
    KAKAO("KAKAO"),
    NAVER("NAVER"),
    FACEBOOK("FACEBOOK"),
    GOOGLE("GOOGLE"),
    APPLE("APPLE");

    private final String provider;

    Provider(String provider) {
        this.provider = provider;
    }

}
