package co.dalicious.system.util;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Spicy {
    NULL("표기없음", 0L),
    Level1("약간 매움", 1L),
    Level2("신라면 맵기", 2L),
    Level3("불닭볶음면 맵기", 3L);

    private final String spicy;
    private final Long code;

    Spicy(String spicy, Long code) {
        this.spicy = spicy;
        this.code = code;
    }

    public static Spicy ofCode(Long code) {
        return Arrays.stream(Spicy.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 맵기 정도입니다."));
    }
}
