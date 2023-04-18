package co.dalicious.domain.paycheck.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class PaycheckAdd {
    @Comment("이슈 날짜")
    private LocalDate issueDate;

    @Comment("이슈 항목")
    private String issueItem;

    @Comment("정산 항목")
    private String paycheckItem;

    @Comment("금액")
    private BigDecimal price;

    @Comment("이슈 내용")
    private String memo;

    @Builder
    public PaycheckAdd(LocalDate issueDate, String issueItem, String paycheckItem, BigDecimal price, String memo) {
        this.issueDate = issueDate;
        this.issueItem = issueItem;
        this.paycheckItem = paycheckItem;
        this.price = price;
        this.memo = memo;
    }
}
