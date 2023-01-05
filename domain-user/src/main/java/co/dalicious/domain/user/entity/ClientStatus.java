package co.dalicious.domain.user.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ClientStatus {
    WITHDRAWAL("탈퇴", 0),
    BELONG("가입", 1);

    private final String clientStatus;
    private final Integer code;

    ClientStatus(String clientStatus, Integer code) {
        this.clientStatus = clientStatus;
        this.code = code;
    }

    public static ClientStatus ofCode(Integer dbData) {
        return Arrays.stream(ClientStatus.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저의 그룹상태입니다."));

    }
}
