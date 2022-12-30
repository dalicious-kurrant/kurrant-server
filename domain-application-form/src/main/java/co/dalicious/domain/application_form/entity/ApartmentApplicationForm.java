package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.converter.ProgressStausConverter;
import co.dalicious.domain.application_form.dto.apartment.ApartmentApplyInfoDto;
import co.dalicious.domain.application_form.dto.ApplyUserDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "application_form__apartement")
public class ApartmentApplicationForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("아파트 스팟 개설 신청서 id")
    private Long id;

    @Convert(converter = ProgressStausConverter.class)
    @Comment("진행 상황")
    private ProgressStatus progressStatus;

    @Column
    @Comment("아파트 스팟 개설 서비스를 신청한 유저의 id")
    private BigInteger userId;

    @Size(max = 64)
    @Column(name = "name", nullable = false, length = 64)
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
    @Column(name = "expected_family_count", nullable = false)
    @Comment("서비스 이용 예상 세대수")
    private Integer expectedFamilyCount;

    @NotNull
    @Column(name = "service_date", nullable = false)
    @Comment("서비스 이용 시작 예정일")
    private LocalDate serviceStartDate;

    @OneToMany(mappedBy = "apartmentApplicationForm", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "application_form_apartment_fk")
    @Comment("식사 정보")
    private List<ApartmentMealInfo> mealInfoList;

    @Column(name = "memo")
    @Comment("기타 내용")
    private String memo;

    @Column(name = "rejected_reason")
    @Comment("미승인 사유")
    private String rejectedReason;

    @Builder
    public ApartmentApplicationForm(ProgressStatus progressStatus, BigInteger userId, ApplyUserDto applyUserDto, Address address, ApartmentApplyInfoDto apartmentApplyInfoDto, String memo) {
        String serviceDate = apartmentApplyInfoDto.getServiceStartDate();

        this.progressStatus = progressStatus;
        this.userId = userId;
        this.applierName = applyUserDto.getName();
        this.phone = applyUserDto.getPhone();
        this.email = applyUserDto.getEmail();
        this.apartmentName = applyUserDto.getEmail();
        this.address = address;
        this.totalFamilyCount = apartmentApplyInfoDto.getFamilyCount();
        this.dongCount = apartmentApplyInfoDto.getDongCount();
        this.expectedFamilyCount = apartmentApplyInfoDto.getFamilyCount();
        this.serviceStartDate = LocalDate.of(Integer.parseInt(serviceDate.substring(0, 4)),
                Integer.parseInt(serviceDate.substring(4, 6)),
                Integer.parseInt(serviceDate.substring(6, 8)));
        this.memo = memo;
    }

    public void setMealInfoList(List<ApartmentMealInfo> mealInfoList) {
        this.mealInfoList = mealInfoList;
    }
    public void updateMemo(String memo) {
        this.memo = memo;
    }
}