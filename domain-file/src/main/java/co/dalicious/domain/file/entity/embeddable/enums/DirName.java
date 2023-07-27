package co.dalicious.domain.file.entity.embeddable.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DirName {
    BOARD("공지사항", 0);

    private final String name;
    private final Integer code;

    DirName(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    public static DirName ofCode(Integer dbData) {
        return Arrays.stream(DirName.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }

}
