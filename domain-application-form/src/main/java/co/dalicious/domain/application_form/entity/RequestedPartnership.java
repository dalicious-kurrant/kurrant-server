package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.application_form.converter.HomepageRequestedTypeConverter;
import co.dalicious.domain.application_form.converter.ProgressStatusConverter;
import co.dalicious.domain.application_form.entity.enums.HomepageRequestedType;
import co.dalicious.domain.application_form.entity.enums.ProgressStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "application_form__requested_partnership")
public class RequestedPartnership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("홈페이지 신청 PK")
    private BigInteger id;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;

    @Column(name = "user_name")
    @Comment("신청한 유저 이름")
    private String username;

    @NotNull
    @Column(name = "address", nullable = false)
    @Comment("주소")
    private String address;

    @Column(name = "user_phone")
    @Comment("유저 핸드폰 번호")
    private String phone;

    @Column(name = "memo")
    @Comment("메모")
    private String memo;

    @Column(name = "makers_name")
    @Comment("신청한 메이커스 이름")
    private String makersName;

    @Column(name = "main_product")
    @Comment("메인 상품")
    private String mainProduct;

    @Convert(converter = ProgressStatusConverter.class)
    @Comment("진행 상황")
    @Column(name = "e_status")
    private ProgressStatus progressStatus;

    @Convert(converter = HomepageRequestedTypeConverter.class)
    @Comment("신청 티압 - 0: 기업 / 1: 메이커스")
    @Column(name = "e_type")
    private HomepageRequestedType requestedType;

    @Builder
    public RequestedPartnership(String username, String address, String phone, String memo, String makersName, String mainProduct, ProgressStatus progressStatus, HomepageRequestedType requestedType) {
        this.username = username;
        this.address = address;
        this.phone = phone;
        this.memo = memo;
        this.makersName = makersName;
        this.mainProduct = mainProduct;
        this.progressStatus = progressStatus;
        this.requestedType = requestedType;
    }
}
