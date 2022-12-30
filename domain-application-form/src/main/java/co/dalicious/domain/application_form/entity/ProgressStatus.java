package co.dalicious.domain.application_form.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ProgressStatus {
    APPLY("스팟 개설 신청", 1),
    DISCUSSING("운영 사항 협의", 2),
    APPROVED("스팟 개설 완료", 3),
    DISAPPROVED("미승인", 4);

    private final String progressStatus;
    private final Integer code;

    ProgressStatus(String progressStatus, Integer code) {
        this.progressStatus = progressStatus;
        this.code = code;
    }

    public static ProgressStatus ofCode(Integer code) {
       return Arrays.stream(ProgressStatus.values())
               .filter(v -> v.getCode().equals(code))
               .findAny()
               .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 진행 타입입니다."));
    }
}
