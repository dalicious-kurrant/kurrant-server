package co.dalicious.domain.user.entity.enums;

import co.dalicious.system.util.DiningType;
import lombok.Getter;

import java.util.Arrays;

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
