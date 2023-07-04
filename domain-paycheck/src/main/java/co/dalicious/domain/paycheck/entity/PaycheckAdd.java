package co.dalicious.domain.paycheck.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class PaycheckAdd {
    @Comment("이슈 날짜")
    private LocalDate issueDate;

    @Comment("금액")
    @Column(columnDefinition="Decimal(15,2) default '0.00'")
    private BigDecimal price;

    @Comment("이슈 내용")
    private String memo;

    @Builder
    public PaycheckAdd(LocalDate issueDate, BigDecimal price, String memo) {
        this.issueDate = issueDate;
        this.price = price;
        this.memo = memo;
    }
}
