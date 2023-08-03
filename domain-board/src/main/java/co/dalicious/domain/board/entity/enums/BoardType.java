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
    EVENT("이벤트 공지", 3),
    MAKERS("메이커스 공지", 4),
    CLIENT("고객사 공지", 5),
    APPROVE_CHANGE_INFO("정보 변경 승인", 6),
    APPROVE_CHANGE_PRICE("가격 변경 승인", 7),
    PAYCHECK_COMPLETE("정산 완료", 8),
    ;


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

    public static List<BoardType> showApp() {
        List<BoardType> list = new ArrayList<>();
        list.add(BoardType.ALL);
        list.add(BoardType.POPUP);
        list.add(BoardType.EVENT);
        return list;
    }

    public static List<BoardType> showMakers() {
        List<BoardType> list = new ArrayList<>();
        list.add(BoardType.ALL);
        list.add(BoardType.MAKERS);
        list.add(BoardType.EVENT);
        list.add(BoardType.APPROVE_CHANGE_INFO);
        list.add(BoardType.APPROVE_CHANGE_PRICE);
        list.add(BoardType.PAYCHECK_COMPLETE);
        return list;
    }

    public static List<BoardType> showClient() {
        List<BoardType> list = new ArrayList<>();
        list.add(BoardType.ALL);
        list.add(BoardType.CLIENT);
        list.add(BoardType.EVENT);
        list.add(BoardType.APPROVE_CHANGE_INFO);
        list.add(BoardType.APPROVE_CHANGE_PRICE);
        list.add(BoardType.PAYCHECK_COMPLETE);
        return list;
    }
}
