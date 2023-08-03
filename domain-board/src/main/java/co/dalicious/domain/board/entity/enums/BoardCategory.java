package co.dalicious.domain.board.entity.enums;

import lombok.Getter;

import java.util.*;

@Getter
public enum BoardCategory {

    MAKERS_ALL("공지", 0),
    CLIENT_ALL("공지", 1),
    APPROVE_CHANGE("변경 승인", 2),
    PAYCHECK("정산", 3),
    EVENT("이벤트", 4),
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

    public static List<BoardType> getBoardTypeByCategory(BoardCategory boardCategory) {
        Map<BoardCategory, List<BoardType>> categoryBoardTypeMap;

        // static initializer block to initialize the map
        categoryBoardTypeMap = new HashMap<>();
        categoryBoardTypeMap.put(BoardCategory.MAKERS_ALL, Arrays.asList(BoardType.ALL, BoardType.MAKERS));
        categoryBoardTypeMap.put(BoardCategory.CLIENT_ALL, Arrays.asList(BoardType.ALL, BoardType.CLIENT));
        categoryBoardTypeMap.put(BoardCategory.APPROVE_CHANGE, Arrays.asList(BoardType.APPROVE_CHANGE_INFO, BoardType.APPROVE_CHANGE_PRICE));
        categoryBoardTypeMap.put(BoardCategory.PAYCHECK, Collections.singletonList(BoardType.PAYCHECK_COMPLETE));
        categoryBoardTypeMap.put(BoardCategory.EVENT, Collections.singletonList(BoardType.EVENT));

        return categoryBoardTypeMap.get(boardCategory);
    }
}
