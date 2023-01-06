package co.dalicious.domain.user.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MembershipStatus {
    PROCESSING("진행중", 1),
    EXPIRED("멤버십 기간 만료", 2),
    AUTO_REFUND("멤버십 자동 환불, 결제 후 7일 이내", 3),
    BACK_OFFICE_REFUND("수동 환불, 백오피스 수정", 4);

    private final String membershipStatus;
    private final Integer code;

    MembershipStatus(String membershipStatus, Integer code) {
        this.membershipStatus = membershipStatus;
        this.code = code;
    }

    public static MembershipStatus ofCode(Integer code) {
        return Arrays.stream(MembershipStatus.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하는 멤버십 상태가 없습니다."));
    }
}
