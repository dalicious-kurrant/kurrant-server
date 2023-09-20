package co.dalicious.domain.application_form.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RecommendProgressStatus {
    PENDING("대기", 0),
    IN_PROGRESS("진행중", 1),
    COMPLETED("완료", 2),
    UNAVAILABLE("불가", 3),
    ;

    private final String status;
    private final Integer code;

    RecommendProgressStatus(String status, Integer code) {
        this.status = status;
        this.code = code;
    }

    public static RecommendProgressStatus ofCode(Integer code) {
        return Arrays.stream(RecommendProgressStatus.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 진행 타입입니다."));
    }
}
