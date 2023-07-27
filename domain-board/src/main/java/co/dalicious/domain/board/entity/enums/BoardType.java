package co.dalicious.domain.board.entity.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum BoardType {
    ALL("전체공지", 0),
    SPOT("스팟공지", 1),
    POPUP("팝업", 2),
    EVENT("이벤트 공지", 3);


    private final String status;
    private final Integer code;

    BoardType(String status, Integer code) {
        this.status = status;
        this.code = code;
    }

    public static BoardType ofCode(Integer dbData) {
        return Arrays.stream(BoardType.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상태입니다."));

    }

    public static List<BoardType> showAll() {
        List<BoardType> list = new ArrayList<>();
        list.add(BoardType.ALL);
        list.add(BoardType.POPUP);
        list.add(BoardType.EVENT);
        return list;
    }

}
