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
    private LocalDate isNutritionInformation;

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

    @NotNull
    @Convert(converter = RoleConverter.class)
    @Column(name = "e_role")
    @Comment("유저 타입")
    private Role role;

    @Builder
    Makers(BigInteger id, String name){
        this.id = id;
        this.name = name;
    }
}
