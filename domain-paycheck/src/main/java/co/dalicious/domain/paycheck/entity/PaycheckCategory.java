package co.dalicious.domain.paycheck.entity;

import co.dalicious.domain.client.converter.PaycheckCategoryItemConverter;
import co.dalicious.domain.client.entity.enums.PaycheckCategoryItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class PaycheckCategory {
    @Convert(converter = PaycheckCategoryItemConverter.class)
    @Comment("지불 항목")
    private PaycheckCategoryItem paycheckCategoryItem;

    @Comment("사용 일 수")
    private Integer days;
    @Comment("개수")
    private Integer count;

    @Column(precision = 15, columnDefinition = "DECIMAL(15, 2)")
    @Comment("지불 항목 개당 금액")
    private BigDecimal price;

    @Column(precision = 15, nullable = false, columnDefinition = "DECIMAL(15, 2)")
    @Comment("총 금액")
    private BigDecimal totalPrice;

    public PaycheckCategory(PaycheckCategoryItem paycheckCategoryItem, Integer days, Integer count, BigDecimal price, BigDecimal totalPrice) {
        this.paycheckCategoryItem = paycheckCategoryItem;
        this.days = days;
        this.count = count;
        this.price = price;
        this.totalPrice = totalPrice;
    }
}