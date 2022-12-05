package co.dalicious.domain.user.entity;

import lombok.Getter;

import java.util.Arrays;
@Getter
public enum Role {
    GUEST(Authority.GUEST, 0L),
    USER(Authority.USER, 1L),
    ADMIN(Authority.ADMIN, 2L);

    private final String authority;
    private final Long code;

    Role(String authority, Long code){
        this.authority = authority;
        this.code = code;
    }

    public static class Authority{
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String GUEST = "ROLE_GUEST";
    }

    public static Role ofCode(Long code) {
        return Arrays.stream(Role.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 미식가 타입입니다."));
    }
}
