package co.dalicious.domain.paycheck.entity;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.paycheck.converter.PaycheckStatusConverter;
import co.dalicious.domain.paycheck.converter.YearMonthAttributeConverter;
import co.dalicious.domain.paycheck.entity.enums.PaycheckStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.YearMonth;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CorporationPaycheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("기업 정산 ID")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Comment("정산 년월")
    @Convert(converter = YearMonthAttributeConverter.class)
    private YearMonth yearMonth;

    @Comment("정산 상태")
    @Convert(converter = PaycheckStatusConverter.class)
    private PaycheckStatus paycheckStatus;

    @Comment("예금주")
    private String accountHolder;

    @Comment("계좌 은행")
    private String nameOfBank;

    @Comment("계좌 번호")
    private String accountNumber;

    @Comment("엑셀 파일")
    private Image excelFile;

    @Comment("PDF 파일")
    private Image pdfFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @Comment("기업 ID")
    private Corporation corporation;

}
