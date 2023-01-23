package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.converter.ProgressStatusConverter;
import co.dalicious.domain.application_form.entity.enums.ProgressStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "application_form__apartement")
public class ApartmentApplicationForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("아파트 스팟 개설 신청서 id")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Convert(converter = ProgressStatusConverter.class)
    @Column(name = "e_progress_status")
    @Comment("진행 상황")
    private ProgressStatus progressStatus;

    @Column
    @Comment("아파트 스팟 개설 서비스를 신청한 유저의 id")
    private BigInteger userId;

    @Size(max = 64)
    @Column(name = "applier_name", nullable = false, length = 64)
    @Comment("아파트 스팟 개설 서비스 신청자명")
    private String applierName;

    @Size(max = 16)
    @Column
    @Comment("아파트 스팟 개설 신청자 연락처")
    private String phone;

    @Size(max = 64)
    @NotNull
    @Column(name = "email", nullable = false, length = 64)
    @Comment("아파트 스팟 개설 신청자 이메일")
    private String email;

    @Size(max = 64)
    @NotNull
    @Column(name = "apartment_name", nullable = false, length = 64)
    @Comment("아파트명")
    private String apartmentName;

    @NotNull
    @Column(name = "emb_address", nullable = false)
    @Comment("주소")
    private Address address;

    @NotNull
    @Column(name = "total_family_count", nullable = false)
    @Comment("단지 총 세대수")
    private Integer totalFamilyCount;

    @NotNull
    @Column(name = "dong_count", nullable = false)
    @Comment("아파트 단지내 동 개수")
    private Integer dongCount;

    @NotNull
    @Column(name = "service_date", nullable = false)
    @Comment("서비스 이용 시작 예정일")
    private LocalDate serviceStartDate;

    @OneToMany(mappedBy = "apartmentApplicationForm", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "application_form_apartment_fk")
    @Comment("식사 정보")
    private List<ApartmentApplicationMealInfo> mealInfoList;

    @Column(name = "memo")
    @Comment("기타 내용")
    private String memo;

    @Column(name = "rejected_reason")
    @Comment("미승인 사유")
    private String rejectedReason;

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

    @Builder
    public ApartmentApplicationForm(ProgressStatus progressStatus, String applierName, String phone, String email, String apartmentName, Address address, Integer totalFamilyCount, Integer dongCount, LocalDate serviceStartDate, String memo) {
        this.progressStatus = progressStatus;
        this.applierName = applierName;
        this.phone = phone;
        this.email = email;
        this.apartmentName = apartmentName;
        this.address = address;
        this.totalFamilyCount = totalFamilyCount;
        this.dongCount = dongCount;
        this.serviceStartDate = serviceStartDate;
        this.memo = memo;
    }


    //    @Builder
//    public ApartmentApplicationForm(ProgressStatus progressStatus, BigInteger userId, ApplyUserDto applyUserDto, Address address, ApartmentApplyInfoDto apartmentApplyInfoDto, String memo) {
//        String serviceDate = apartmentApplyInfoDto.getServiceStartDate();
//
//        this.progressStatus = progressStatus;
//        this.userId = userId;
//        this.applierName = applyUserDto.getName();
//        this.phone = applyUserDto.getPhone();
//        this.email = applyUserDto.getEmail();
//        this.apartmentName = apartmentApplyInfoDto.getApartmentName();
//        this.address = address;
//        this.totalFamilyCount = apartmentApplyInfoDto.getFamilyCount();
//        this.dongCount = apartmentApplyInfoDto.getDongCount();
//        this.serviceStartDate = LocalDate.of(Integer.parseInt(serviceDate.substring(0, 4)),
//                Integer.parseInt(serviceDate.substring(4, 6)),
//                Integer.parseInt(serviceDate.substring(6, 8)));
//        this.memo = memo;
//    }

    public void setMealInfoList(List<ApartmentApplicationMealInfo> mealInfoList) {
        this.mealInfoList = mealInfoList;
    }
    public void updateMemo(String memo) {
        this.memo = memo;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public void updateRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }
}