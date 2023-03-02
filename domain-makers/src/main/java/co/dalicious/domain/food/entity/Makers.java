package co.dalicious.domain.food.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.food.converter.ServiceFormConverter;
import co.dalicious.domain.food.converter.ServiceTypeConverter;
import co.dalicious.domain.food.entity.enums.Origin;
import co.dalicious.domain.food.entity.enums.ServiceForm;
import co.dalicious.domain.food.entity.enums.ServiceType;
import co.dalicious.domain.user.converter.RoleConverter;
import co.dalicious.domain.user.entity.enums.Role;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
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
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
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

    @OneToMany(mappedBy = "makers", orphanRemoval = true)
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

    @Comment("모회사 여부")
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

    @Comment("은행")
    private String bank;

    @Comment("예금주명")
    private String depositHolder;

    @Comment("계좌번호")
    private String accountNumber;

    @Embedded
    @Comment("사업자 등록증 사진")
    private Image image;

    @OneToMany(mappedBy = "makers")
    @JsonManagedReference(value = "makers_fk")
    @Comment("원산지")
    private List<Origin> origins;

    @ElementCollection
    @Comment("식사 일정별 메이커스 픽업 시간")
    private List<PickupTime> pickupTimes;

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

    @Builder
    Makers(String code, String name, String companyName, String CEO, String CEOPhone,
           String managerName, String managerPhone, ServiceType serviceType, ServiceForm serviceForm, Boolean isParentCompany,
           BigInteger parentCompanyId, Address address, String companyRegistrationNumber, LocalDate contractStartDate, LocalDate contractEndDate,
           Boolean isNutritionInformation, LocalTime openTime, LocalTime closeTime, String bank, String depositHolder,
           String accountNumber, Timestamp createdDateTime, Timestamp updatedDateTime, String password, Role role
           ){
        this.code = code;
        this.name = name;
        this.companyName = companyName;
        this.CEO = CEO;
        this.CEOPhone = CEOPhone;
        this.managerName = managerName;
        this.managerPhone = managerPhone;
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
        this.bank = bank;
        this.depositHolder = depositHolder;
        this.accountNumber = accountNumber;
        this.createdDateTime = createdDateTime;
        this.updatedDateTime = updatedDateTime;
        this.password = password;
        this.role = role;

    }

    public MakersCapacity getMakersCapacity(DiningType diningType) {
        return getMakersCapacities().stream()
                .filter(v -> v.getDiningType().equals(diningType))
                .findAny()
                .orElse(null);
    }

    public PickupTime getPickupTime(DiningType diningType) {
        return this.getPickupTimes().stream()
                .filter(v -> v.getDiningType().equals(diningType))
                .findAny()
                .orElse(null);
    }

    public String getPickupTimeString(DiningType diningType) {
        PickupTime pickupTime = getPickupTime(diningType);
        if(pickupTime == null) return null;
        return DateUtils.timeToString(pickupTime.getPickupTime());
    }
}
