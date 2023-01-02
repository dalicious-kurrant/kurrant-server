package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.converter.ProgressStausConverter;
import co.dalicious.domain.application_form.dto.ApplyUserDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationApplyInfoDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationOptionsDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "application_form__corporation")
public class CorporationApplicationForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("기업 스팟 개설 신청서 id")
    private Long id;

    @Convert(converter = ProgressStausConverter.class)
    @Comment("진행 상황")
    private ProgressStatus progressStatus;

    @Column
    @Comment("기업 스팟 개설 서비스를 신청한 유저의 id")
    private BigInteger userId;

    @Size(max = 64)
    @NotNull
    @Column(name = "name", nullable = false, length = 64)
    @Comment("기업 스팟 개설 서비스 신청자명")
    private String applierName;

    @Size(max = 16)
    @NotNull
    @Column(name = "phone", nullable = false, length = 16)
    @Comment("기업 스팟 개설 신청자 연락처")
    private String phone;

    @Size(max = 64)
    @NotNull
    @Column(name = "email", nullable = false, length = 64)
    @Comment("기업 스팟 개설 신청자 이메일")
    private String email;

    @Size(max = 64)
    @NotNull
    @Column(name = "corporation_name", nullable = false, length = 64)
    @Comment("기업명")
    private String corporationName;


    @NotNull
    @Column(name = "emb_address", nullable = false)
    @Comment("주소")
    private Address address;

    @NotNull
    @Column(name = "employee_count", nullable = false)
    @Comment("기업 총 인원수(미이용자 포함)")
    private Integer employeeCount;

    @NotNull
    @Column(name = "service_date", nullable = false)
    @Comment("서비스 이용 시작 예정일")
    private LocalDate serviceStartDate;

    @Column(name = "is_garbage")
    @Comment("쓰레기 수거 서비스 사용 유무")
    private Boolean isGarbage;

    @Column(name = "is_hot_storage")
    @Comment("온장고 대여 서비스 사용 유무")
    private Boolean isHotStorage;

    @Column(name = "is_setting")
    @Comment("식사 세팅 지원 서비스 사용 유무")
    private Boolean isSetting;

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

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;

    @OneToMany(mappedBy = "corporationApplicationForm", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "application_form__corporation_fk")
    @Comment("식사 정보")
    private List<CorporationApplicationMealInfo> mealInfoList;

    @OneToMany(mappedBy = "corporationApplicationForm", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "application_form__corporation_fk")
    @Comment("스팟 정보")
    private List<CorporationApplicationFormSpot> spots;


    public void setMealInfoList(List<CorporationApplicationMealInfo> mealInfoList) {
        this.mealInfoList = mealInfoList;
    }

    public void setSpots(List<CorporationApplicationFormSpot> spots) {
        this.spots = spots;
    }

    @Builder
    public CorporationApplicationForm(ProgressStatus progressStatus, BigInteger userId, ApplyUserDto applyUserDto, CorporationApplyInfoDto applyInfoDto, Address address, CorporationOptionsDto corporationOptionsDto) {
        String date = applyInfoDto.getStartDate();
        this.progressStatus = progressStatus;
        this.userId = userId;
        this.applierName = applyUserDto.getName();
        this.phone = applyUserDto.getPhone();
        this.email = applyUserDto.getEmail();
        this.corporationName = applyInfoDto.getCorporationName();
        this.address = address;
        this.employeeCount = applyInfoDto.getEmployeeCount();
        this.serviceStartDate = LocalDate.of(Integer.parseInt(date.substring(0, 4)),
                Integer.parseInt(date.substring(4, 6)),
                Integer.parseInt(date.substring(6, 8)));
        this.isGarbage = corporationOptionsDto.getIsGarbage();
        this.isHotStorage = corporationOptionsDto.getIsHotStorage();
        this.isSetting = corporationOptionsDto.getIsSetting();
        this.memo = corporationOptionsDto.getMemo();
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }

    public void updateRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }
}