package co.dalicious.domain.paycheck.entity;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.system.enums.DiningType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class PaycheckDailyFood {
    @Comment("서비스 날짜")
    private LocalDate serviceDate;

    @Comment("식사 일정")
    private DiningType diningType;

    @Comment("음식 이름")
    private String name;

    @Comment("공급가")
    private BigDecimal supplyPrice;

    @Comment("개수")
    private Integer count;

    @Comment("음식 PK")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    private Food food;

    @Builder
    public PaycheckDailyFood(LocalDate serviceDate, DiningType diningType, String name, BigDecimal supplyPrice, Integer count, Food food) {
        this.serviceDate = serviceDate;
        this.diningType = diningType;
        this.name = name;
        this.supplyPrice = supplyPrice;
        this.count = count;
        this.food = food;
    }

    public BigDecimal getTotalPrice() {
        return this.supplyPrice.multiply(BigDecimal.valueOf(count));
    }
}
