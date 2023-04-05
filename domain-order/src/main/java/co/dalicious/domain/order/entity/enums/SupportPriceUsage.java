package co.dalicious.domain.order.entity.enums;

import lombok.Getter;

@Getter
public enum SupportPriceUsage {
    NORMAL_DAILYFOOD("일반 정기식사 지원금", 1),
    MEMBERSHIP("기업 멤버십 지원금", 2);

    private final String supportPriceUsage;
    private final Integer code;

    SupportPriceUsage(String supportPriceUsage, Integer code) {
        this.supportPriceUsage = supportPriceUsage;
        this.code = code;
    }
}
