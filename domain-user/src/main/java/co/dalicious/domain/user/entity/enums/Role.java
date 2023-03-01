package co.dalicious.domain.user.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;
@Getter
public enum Role {
    GUEST(Authority.GUEST, 0L),
    USER(Authority.USER, 1L),
    MANAGER(Authority.MANAGER, 2L),
    ADMIN(Authority.ADMIN, 3L);

    private final String authority;
    private final Long code;

    Role(String authority, Long code){
        this.authority = authority;
        this.code = code;
    }

    public static class Authority{
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String MANAGER = "ROLE_MANAGER";
        public static final String GUEST = "ROLE_GUEST";
    }

    public static Role ofCode(Long code) {
        return Arrays.stream(Role.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 타입입니다."));
    }

    public static Role ofString(String dbData) {
        return Arrays.stream(Role.values())
                .filter(v -> v.getAuthority().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 타입입니다."));
    }

    public static Role ofRoleName(String roleName) {
        return switch (roleName) {
            case "게스트" -> Role.GUEST;
            case "일반" -> Role.USER;
            case "관리자" -> Role.ADMIN;
            case "매니저" -> Role.MANAGER;
            default -> throw new ApiException(ExceptionEnum.ENUM_NOT_FOUND);
        };
    }

}
