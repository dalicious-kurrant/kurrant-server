package co.dalicious.domain.paycheck.entity;

import co.dalicious.domain.paycheck.converter.YearMonthAttributeConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
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

    @OneToOne(mappedBy = "expectedPaycheck")
    @JsonBackReference(value = "spot_fk")
    private CorporationPaycheck corporationPaycheck;

    @ElementCollection
    @Comment("지불 항목 내역")
    @CollectionTable(name = "paycheck__expected_paycheck_paycheck_categories")
    private List<PaycheckCategory> paycheckCategories;
}
