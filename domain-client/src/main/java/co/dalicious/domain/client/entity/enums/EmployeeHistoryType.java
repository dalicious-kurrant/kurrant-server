package co.dalicious.domain.client.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum EmployeeHistoryType {

    USER("기존유저", 0),
    WAIT_USER("가입 대기 유저", 1);

    private final String employeeType;
    private final Integer code;

    EmployeeHistoryType(String employeeType, Integer code) {
        this.employeeType = employeeType;
        this.code = code;
    }

    public static SpotStatus ofCode(Integer dbData) {
        return Arrays.stream(SpotStatus.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버 히스토리 상태입니다."));
    }

}
