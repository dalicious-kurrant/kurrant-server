package co.dalicious.domain.logs.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum LogType {
    CREATE("생성", 1),
    UPDATE("수정", 2),
    DELETE("삭제", 3);

    private final String logType;
    private final Integer code;

    LogType(String logType, Integer code) {
        this.logType = logType;
        this.code = code;
    }

    public static LogType ofCode(Integer dbData) {
        return Arrays.stream(LogType.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElse(null);
    }
}
