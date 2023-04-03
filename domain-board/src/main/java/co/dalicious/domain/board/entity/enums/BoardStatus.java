package co.dalicious.domain.board.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BoardStatus {
    INACTIVE("비활성", 0),
    ACTIVE("활성", 1),
    POPUP("팝업", 2),
    SPOT("스팟공지", 3),
    EVENT("이벤트 공지", 4);


    private final String status;
    private final Integer code;

    BoardStatus(String status, Integer code) {
        this.status = status;
        this.code = code;
    }

    public static BoardStatus ofCode(Integer dbData) {
        return Arrays.stream(BoardStatus.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상태입니다."));

    }


}
