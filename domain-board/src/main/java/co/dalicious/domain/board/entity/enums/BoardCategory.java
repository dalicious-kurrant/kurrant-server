package co.dalicious.domain.board.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.*;

@Getter
public enum BoardCategory {

    ALL("공지", 0),
    APPROVE_CHANGE("변경 승인", 1),
    PAYCHECK("정산", 2),
    EVENT("이벤트", 3),
    ;
    private String category;
    private Integer code;

    BoardCategory(String category, Integer code) {
        this.category = category;
        this.code = code;
    }

    public static BoardCategory ofCode(Integer dbData) {
        return Arrays.stream(BoardCategory.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상태입니다."));

    }

    public static BoardCategory getBoardTypeByCategory(BoardType boardType) {
        Map<BoardCategory, List<BoardType>> categoryBoardTypeMap;

        // static initializer block to initialize the map
        categoryBoardTypeMap = new HashMap<>();
        categoryBoardTypeMap.put(BoardCategory.ALL, Arrays.asList(BoardType.ALL, BoardType.MAKERS, BoardType.CLIENT));
        categoryBoardTypeMap.put(BoardCategory.APPROVE_CHANGE, Arrays.asList(BoardType.APPROVE_CHANGE_INFO, BoardType.APPROVE_CHANGE_PRICE));
        categoryBoardTypeMap.put(BoardCategory.PAYCHECK, Collections.singletonList(BoardType.PAYCHECK_COMPLETE));
        categoryBoardTypeMap.put(BoardCategory.EVENT, Collections.singletonList(BoardType.EVENT));

        return categoryBoardTypeMap.entrySet().stream()
                .filter(entry -> entry.getValue().contains(boardType))
                .findAny().orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상태입니다."))
                .getKey();
    }
}
