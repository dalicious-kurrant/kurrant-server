package co.dalicious.domain.food.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.file.entity.embeddable.ImageWithEnum;
import co.dalicious.domain.file.entity.embeddable.enums.ImageType;
import co.dalicious.domain.food.converter.ServiceFormConverter;
import co.dalicious.domain.food.converter.ServiceTypeConverter;
import co.dalicious.domain.food.dto.SaveMakersRequestDto;
import co.dalicious.domain.food.dto.UpdateMakersReqDto;
import co.dalicious.domain.food.entity.enums.Origin;
import co.dalicious.domain.food.entity.enums.ServiceForm;
import co.dalicious.domain.food.entity.enums.ServiceType;
import co.dalicious.domain.user.converter.RoleConverter;
import co.dalicious.domain.user.entity.enums.Role;
import co.dalicious.system.converter.DaysListConverter;
import co.dalicious.system.enums.Days;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DaysUtil;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import java.math.BigInteger;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor
@Entity
@Table(name = "makers__makers")
public class Makers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Comment("ID")
    private BigInteger id;

    @Comment("그룹 코드")
    private String code;
    @Column(name = "name")
    @Comment("메이커스 이름")
    private String name;

    @Comment("법인명")
    private String companyName;

    @Comment("사업자 대표")
    private String CEO;

    @Comment("대표자 전화번호")
    private String CEOPhone;

    @Comment("담당자 이름")
    private String managerName;

    @Comment("담당자 전화번호")
    private String managerPhone;

    @OneToMany(mappedBy = "makers")
    @JsonBackReference(value = "makers_fk")
    @Comment("일일 식사 일정별 최대 수량")
    private List<MakersCapacity> makersCapacities;

    @Convert(converter = ServiceTypeConverter.class)
    @Column(name = "e_service_type")
    @Comment("서비스 업종")
    private ServiceType serviceType;

    @Convert(converter = ServiceFormConverter.class)
    @Column(name = "e_service_form")
    @Comment("서비스 형태")
    private ServiceForm serviceForm;

    @Comment("모회사 여부(이 회사가 모회사인지)")
    private Boolean isParentCompany;

    @Comment("모회사 id")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger parentCompanyId;

    @Embedded
    @Comment("주소")
    private Address address;

    @Comment("사업자 등록 번호")
    private String companyRegistrationNumber;

    @Comment("계약 시작 날짜")
    private LocalDate contractStartDate;

    @Comment("계약 종료 날짜")
    private LocalDate contractEndDate;

    @Comment("외식 영양정보 표시대상 여부")
    private Boolean isNutritionInformation;

    @Comment("영업 시작 시간")
    private LocalTime openTime;

    @Comment("영업 종료 시간")
    private LocalTime closeTime;

    @Comment("시스템 사용료")
    private String fee;

    @Comment("은행")
    private String bank;

    @Comment("예금주명")
    private String depositHolder;

    @Comment("계좌번호")
    private String accountNumber;

    @Convert(converter = DaysListConverter.class)
    @Column(name = "emb_use_days")
    @Comment("서비스 이용 요일")
    private List<Days> serviceDays;

    @ElementCollection
    @Comment("이미지 경로")
    @CollectionTable(name = "makers__images")
    private List<ImageWithEnum> images = new ArrayList<>();

    @OneToMany(mappedBy = "makers")
    @JsonManagedReference(value = "makers_fk")
    @Comment("원산지")
    private List<Origin> origins;

    @CreationTimestamp
    @Column(name = "created_datetime", nullable = false, insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6) COMMENT '생성일'")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @Column(name = "updated_datetime", nullable = false, insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6) ON UPDATE NOW(6) COMMENT '수정일'")
    private Timestamp updatedDateTime;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password",
            columnDefinition = "VARCHAR(255)")
    @Comment("비밀번호, BCrpypt")
    private String password;

    @ColumnDefault("1")
    @Convert(converter = RoleConverter.class)
    @Column(name = "e_role")
    @Comment("유저 타입")
    private Role role;

    @Comment("메모")
    @Column(name = "memo", columnDefinition = "VARCHAR(255)")
    private String memo;

    @Column(columnDefinition = "BIT(1) DEFAULT 1")
    @Comment("활성화 여부")
    private Boolean isActive;

    @Builder
    public Makers(String code, String name, String companyName, String CEO, String CEOPhone, String managerName, String managerPhone, List<MakersCapacity> makersCapacities, ServiceType serviceType, ServiceForm serviceForm, Boolean isParentCompany, BigInteger parentCompanyId, Address address, String companyRegistrationNumber, LocalDate contractStartDate, LocalDate contractEndDate, Boolean isNutritionInformation, LocalTime openTime, LocalTime closeTime, String fee, String bank, String depositHolder, String accountNumber, List<Days> serviceDays, List<ImageWithEnum> images, List<Origin> origins, String password, Role role, String memo, Boolean isActive) {
        this.code = code;
        this.name = name;
        this.companyName = companyName;
        this.CEO = CEO;
        this.CEOPhone = CEOPhone;
        this.managerName = managerName;
        this.managerPhone = managerPhone;
        this.makersCapacities = makersCapacities;
        this.serviceType = serviceType;
        this.serviceForm = serviceForm;
        this.isParentCompany = isParentCompany;
        this.parentCompanyId = parentCompanyId;
        this.address = address;
        this.companyRegistrationNumber = companyRegistrationNumber;
        this.contractStartDate = contractStartDate;
        this.contractEndDate = contractEndDate;
        this.isNutritionInformation = isNutritionInformation;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.fee = fee;
        this.bank = bank;
        this.depositHolder = depositHolder;
        this.accountNumber = accountNumber;
        this.serviceDays = serviceDays;
        this.images = images;
        this.origins = origins;
        this.password = password;
        this.role = role;
        this.memo = memo;
        this.isActive = isActive;
    }

    @Builder
    public void updateMakers(SaveMakersRequestDto saveMakersRequestDto) {
        if (saveMakersRequestDto.getCode() != null && !saveMakersRequestDto.getCode().isEmpty())
            this.code = saveMakersRequestDto.getCode();
        if (saveMakersRequestDto.getName() != null && !saveMakersRequestDto.getName().isEmpty())
            this.name = saveMakersRequestDto.getName();
        if (saveMakersRequestDto.getCompanyName() != null && !saveMakersRequestDto.getCompanyName().isEmpty())
            this.companyName = saveMakersRequestDto.getCompanyName();
        if (saveMakersRequestDto.getCeo() != null && !saveMakersRequestDto.getCeo().isEmpty())
            this.CEO = saveMakersRequestDto.getCeo();
        if (saveMakersRequestDto.getCeoPhone() != null && !saveMakersRequestDto.getCeoPhone().isEmpty())
            this.CEOPhone = saveMakersRequestDto.getCeoPhone();
        if (saveMakersRequestDto.getManagerName() != null && !saveMakersRequestDto.getManagerName().isEmpty())
            this.managerName = saveMakersRequestDto.getManagerName();
        if (saveMakersRequestDto.getManagerPhone() != null && !saveMakersRequestDto.getManagerPhone().isEmpty())
            this.CEOPhone = saveMakersRequestDto.getCeoPhone();
        if (saveMakersRequestDto.getServiceType() != null && !saveMakersRequestDto.getServiceType().isEmpty())
            this.serviceType = ServiceType.ofString(saveMakersRequestDto.getServiceType());
        if (saveMakersRequestDto.getServiceForm() != null && !saveMakersRequestDto.getServiceForm().isEmpty())
            this.serviceForm = ServiceForm.ofString(saveMakersRequestDto.getServiceForm());
        if (saveMakersRequestDto.getIsParentCompany() != null)
            this.isParentCompany = saveMakersRequestDto.getIsParentCompany();
        if (saveMakersRequestDto.getParentCompanyId() != null)
            this.parentCompanyId = saveMakersRequestDto.getParentCompanyId();
        if (saveMakersRequestDto.getCompanyRegistrationNumber() != null && !saveMakersRequestDto.getCompanyRegistrationNumber().isEmpty())
            this.companyRegistrationNumber = saveMakersRequestDto.getCompanyRegistrationNumber();
        if (!saveMakersRequestDto.getContractStartDate().isEmpty())
            this.contractStartDate = LocalDate.parse(saveMakersRequestDto.getContractStartDate());
        if (saveMakersRequestDto.getContractEndDate() != null && !saveMakersRequestDto.getContractEndDate().isEmpty())
            this.contractEndDate = LocalDate.parse(saveMakersRequestDto.getContractEndDate());
        if (saveMakersRequestDto.getIsNutritionInformation() != null)
            this.isNutritionInformation = saveMakersRequestDto.getIsNutritionInformation();
        if (saveMakersRequestDto.getOpenTime() != null && !saveMakersRequestDto.getOpenTime().isEmpty())
            this.openTime = LocalTime.parse(saveMakersRequestDto.getOpenTime());
        if (saveMakersRequestDto.getCloseTime() != null && !saveMakersRequestDto.getCloseTime().isEmpty())
            this.closeTime =  LocalTime.parse(saveMakersRequestDto.getCloseTime());
        if (saveMakersRequestDto.getFee() != null)
            this.fee = saveMakersRequestDto.getFee();
        if (saveMakersRequestDto.getBank() != null && !saveMakersRequestDto.getBank().isEmpty())
            this.bank = saveMakersRequestDto.getBank();
        if (saveMakersRequestDto.getDepositHolder() != null && !saveMakersRequestDto.getDepositHolder().isEmpty())
            this.depositHolder = saveMakersRequestDto.getDepositHolder();
        if (saveMakersRequestDto.getAccountNumber() != null && !saveMakersRequestDto.getAccountNumber().isEmpty())
            this.accountNumber = saveMakersRequestDto.getAccountNumber();
        if (saveMakersRequestDto.getIsActive() != null)
            this.isActive = saveMakersRequestDto.getIsActive();
        if (saveMakersRequestDto.getServiceDays() != null && !saveMakersRequestDto.getServiceDays().equals(""))
            this.serviceDays = DaysUtil.serviceDaysToDaysList(saveMakersRequestDto.getServiceDays());
    }

    public void updateAddress(Address address) {
        this.address = address;
    }

    public MakersCapacity getMakersCapacity(DiningType diningType) {
        return getMakersCapacities().stream()
                .filter(v -> v.getDiningType().equals(diningType))
                .findAny()
                .orElse(null);
    }

    public ImageWithEnum getImageFromType(ImageType imageType) {
        return this.getImages()
                .stream().filter(v -> v.getImageType().equals(imageType)).findAny().orElse(null);
    }

    public void updateImages(List<ImageWithEnum> images) {
        this.images = images;
    }

    public List<DiningType> getDiningTypes() {
        return this.makersCapacities.stream()
                .map(MakersCapacity::getDiningType)
                .toList();
    }

    public Integer getDailyCapacity() {
        return this.makersCapacities.stream()
                .map(MakersCapacity::getCapacity)
                .reduce(0, Integer::sum);
    }
}
