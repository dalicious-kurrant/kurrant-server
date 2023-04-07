package co.dalicious.domain.paycheck.entity;

import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.paycheck.converter.PaycheckStatusConverter;
import co.dalicious.domain.paycheck.converter.YearMonthAttributeConverter;
import co.dalicious.domain.paycheck.entity.enums.PaycheckStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.YearMonth;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "paycheck__makers_paycheck")
public class MakersPaycheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("메이커스 정산 ID")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Comment("정산 년월")
    @Convert(converter = YearMonthAttributeConverter.class)
    private YearMonth yearMonth;

    @Comment("정산 상태")
    @Convert(converter = PaycheckStatusConverter.class)
    private PaycheckStatus paycheckStatus;

    @Comment("엑셀 파일")
    @AttributeOverrides({
            @AttributeOverride(name = "key", column = @Column(name = "excel_s3_key", length = 1024)),
            @AttributeOverride(name = "location", column = @Column(name = "excel_file_location", length = 2048)),
            @AttributeOverride(name = "filename", column = @Column(name = "excel_filename", length = 1024))
    })
    private Image excelFile;

    @Comment("PDF 파일")
    @AttributeOverrides({
            @AttributeOverride(name = "key", column = @Column(name = "pdf_s3_key", length = 1024)),
            @AttributeOverride(name = "location", column = @Column(name = "pdf_file_location", length = 2048)),
            @AttributeOverride(name = "filename", column = @Column(name = "pdf_filename", length = 1024))
    })
    private Image pdfFile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "makers_id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("기업 ID")
    private Makers makers;

    @ElementCollection
    @Comment("식사 일정별 음식 내역")
    @CollectionTable(name = "paycheck__makers_paycheck__paycheck_daily_foods")
    private List<PaycheckDailyFood> paycheckDailyFoods;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;

    public MakersPaycheck(YearMonth yearMonth, PaycheckStatus paycheckStatus, Image excelFile, Image pdfFile, Makers makers) {
        this.yearMonth = yearMonth;
        this.paycheckStatus = paycheckStatus;
        this.excelFile = excelFile;
        this.pdfFile = pdfFile;
        this.makers = makers;
    }

    public void updatePaycheckStatus(PaycheckStatus paycheckStatus) {
        this.paycheckStatus = paycheckStatus;
    }

    public void updateMakersPaycheck(YearMonth yearMonth, PaycheckStatus paycheckStatus) {
        this.yearMonth = yearMonth;
        this.paycheckStatus = paycheckStatus;
    }

    public void updateExcelFile(Image excelFile) {
        this.excelFile = excelFile;
    }

    public void updatePdfFile(Image pdfFile) {
        this.pdfFile = pdfFile;
    }
}
