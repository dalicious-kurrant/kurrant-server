package co.dalicious.domain.paycheck.entity;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.paycheck.converter.PaycheckStatusConverter;
import co.dalicious.domain.paycheck.converter.YearMonthAttributeConverter;
import co.dalicious.domain.paycheck.entity.enums.PaycheckStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.YearMonth;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "paycheck__corporation_paycheck")
public class CorporationPaycheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Comment("정산 년월")
    @Convert(converter = YearMonthAttributeConverter.class)
    private YearMonth yearMonth;

    @Comment("정산 상태")
    @Convert(converter = PaycheckStatusConverter.class)
    private PaycheckStatus paycheckStatus;

    @Comment("담당자 이름")
    private String managerName;

    @Comment("담당자 전화번호")
    private String phone;

    @Embedded
    @Comment("엑셀 파일")
    @AttributeOverrides({
            @AttributeOverride(name = "key", column = @Column(name = "excel_s3_key", length = 1024)),
            @AttributeOverride(name = "location", column = @Column(name = "excel_file_location", length = 2048)),
            @AttributeOverride(name = "filename", column = @Column(name = "excel_filename", length = 1024))
    })
    private Image excelFile;

    @Embedded
    @Comment("PDF 파일")
    @AttributeOverrides({
            @AttributeOverride(name = "key", column = @Column(name = "pdf_s3_key", length = 1024)),
            @AttributeOverride(name = "location", column = @Column(name = "pdf_file_location", length = 2048)),
            @AttributeOverride(name = "filename", column = @Column(name = "pdf_filename", length = 1024))
    })
    private Image pdfFile;

    @ElementCollection
    @Comment("지불 항목 내역")
    @CollectionTable(name = "paycheck__corporation_paycheck_paycheck_categories")
    private List<PaycheckCategory> paycheckCategories;

    @ElementCollection
    @Comment("지불 항목 내역")
    @CollectionTable(name = "paycheck__corporation_paycheck_paycheck_adds")
    private List<PaycheckAdd> paycheckAdds;

    @ElementCollection
    @Comment("메모")
    @CollectionTable(name = "paycheck__corporation_paycheck__paycheck_memo")
    private List<PaycheckMemo> paycheckMemos;

    @OneToOne(mappedBy = "corporationPaycheck")
    @JsonBackReference(value = "spot_fk")
    private ExpectedPaycheck expectedPaycheck;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "corporation_id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("기업 ID")
    private Corporation corporation;

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

    public CorporationPaycheck(YearMonth yearMonth, PaycheckStatus paycheckStatus, String managerName, String phone, Image excelFile, Image pdfFile, Corporation corporation) {
        this.yearMonth = yearMonth;
        this.paycheckStatus = paycheckStatus;
        this.managerName = managerName;
        this.phone = phone;
        this.excelFile = excelFile;
        this.pdfFile = pdfFile;
        this.corporation = corporation;
    }

    @Builder
    public CorporationPaycheck(YearMonth yearMonth, PaycheckStatus paycheckStatus, String managerName, String phone, Image excelFile, Image pdfFile, List<PaycheckCategory> paycheckCategories, List<PaycheckAdd> paycheckAdds, List<PaycheckMemo> paycheckMemos, ExpectedPaycheck expectedPaycheck, Corporation corporation) {
        this.yearMonth = yearMonth;
        this.paycheckStatus = paycheckStatus;
        this.managerName = managerName;
        this.phone = phone;
        this.excelFile = excelFile;
        this.pdfFile = pdfFile;
        this.paycheckCategories = paycheckCategories;
        this.paycheckAdds = paycheckAdds;
        this.paycheckMemos = paycheckMemos;
        this.corporation = corporation;
    }


    public void updatePaycheckStatus(PaycheckStatus paycheckStatus) {
        this.paycheckStatus = paycheckStatus;
    }

    public void updateExcelFile(Image excelFile) {
        this.excelFile = excelFile;
    }

    public void updatePdfFile(Image pdfFile) {
        this.pdfFile = pdfFile;
    }

    public void updateCorporationPaycheck(YearMonth yearMonth, PaycheckStatus paycheckStatus, String managerName, String phone) {
        this.yearMonth = yearMonth;
        this.paycheckStatus = paycheckStatus;
        this.managerName = managerName;
        this.phone = phone;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (PaycheckCategory paycheckCategory : paycheckCategories) {
            totalPrice = totalPrice.add(paycheckCategory.getTotalPrice());
        }
        return totalPrice.add(getPaycheckAddsTotalPrice());
    }

    public BigDecimal getExpectedTotalPrice() {
        if(this.getExpectedPaycheck() == null) return null;
        return this.getExpectedPaycheck().getTotalPrice();
    }

    public BigDecimal getPaycheckAddsTotalPrice() {
        BigDecimal totalPrice = BigDecimal.ZERO;
        if(this.paycheckAdds != null) {
            for (PaycheckAdd paycheckAdd : this.paycheckAdds) {
                totalPrice = totalPrice.add(paycheckAdd.getPrice());
            }
        }
        return totalPrice;
    }

    public BigDecimal getPrepaidTotalPrice() {
        return this.expectedPaycheck == null ? null : this.expectedPaycheck.getTotalPrice();
    }

    public Boolean hasRequest() {
        return !this.paycheckMemos.isEmpty();
    }

    public String getYearAndMonthString() {
        return this.yearMonth.getYear() +
                ((this.yearMonth.getMonthValue() < 10) ? "0" + String.valueOf(this.yearMonth.getMonthValue()) : String.valueOf(this.yearMonth.getMonthValue()));
    }

    public String getOrdersFileName() {
        return " 거래명세서_" + this.yearMonth.getYear() + "-" +
                ((this.yearMonth.getMonthValue() < 10) ? "0" + String.valueOf(this.yearMonth.getMonthValue()) : String.valueOf(this.yearMonth.getMonthValue()));
    }

    public String getInvoiceFileName() {
        return " 인보이스_" + this.yearMonth.getYear() + "-" +
                ((this.yearMonth.getMonthValue() < 10) ? "0" + String.valueOf(this.yearMonth.getMonthValue()) : String.valueOf(this.yearMonth.getMonthValue()));
    }

    public CorporationPaycheck updatePaycheckAdds(List<PaycheckAdd> paycheckAdds) {
        this.paycheckAdds.addAll(paycheckAdds);
        return this;
    }

    public void updateMemo(PaycheckMemo paycheckMemos) {
        this.paycheckMemos.add(paycheckMemos);
    }
}
