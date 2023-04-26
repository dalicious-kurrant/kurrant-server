package co.dalicious.domain.paycheck.entity;

import co.dalicious.domain.client.entity.PaycheckCategory;
import co.dalicious.domain.paycheck.converter.YearMonthAttributeConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.YearMonth;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "paycheck__expected_paycheck")
public class ExpectedPaycheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Comment("정산 년월")
    @Convert(converter = YearMonthAttributeConverter.class)
    private YearMonth yearMonth;

    @ElementCollection
    @Comment("지불 항목 내역")
    @CollectionTable(name = "paycheck__expected_paycheck_paycheck_categories")
    private List<PaycheckCategory> paycheckCategories;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("선불 정산")
    private CorporationPaycheck corporationPaycheck;

    public ExpectedPaycheck(YearMonth yearMonth, List<PaycheckCategory> paycheckCategories, CorporationPaycheck corporationPaycheck) {
        this.yearMonth = yearMonth;
        this.paycheckCategories = paycheckCategories;
        this.corporationPaycheck = corporationPaycheck;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (PaycheckCategory paycheckCategory : paycheckCategories) {
            totalPrice = totalPrice.add(paycheckCategory.getTotalPrice());
        }
        return totalPrice;
    }
}
