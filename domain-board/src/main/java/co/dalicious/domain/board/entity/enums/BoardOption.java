package co.dalicious.domain.board.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BoardOption {

    NOTICE("공지", 0),
    POPUP("팝업", 1),
    EVENT("이벤트", 2),
    ;

    private String option;
    private Integer code;

    BoardOption(String option, Integer code) {
        this.option = option;
        this.code = code;
    }

    public static BoardOption ofCode(Integer dbData) {
        return Arrays.stream(BoardOption.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상태입니다."));

    }
}
