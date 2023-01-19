package co.dalicious.system.util.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Spicy {
    Level0("NULL", 0),
    Level1("약간 매움", 1),
    Level2("신라면 맵기", 2),
    Level3("불닭볶음면 맵기", 3);

    private final String spicy;
    private final Integer code;

    Spicy(String spicy, Integer code) {
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
