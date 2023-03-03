package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.client.dto.SpotResponseDto;
import co.dalicious.domain.client.entity.enums.SpotStatus;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.converter.DiningTypesConverter;
import co.dalicious.system.util.DiningTypesUtils;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
@Getter
@Table(name = "client__spot", uniqueConstraints={@UniqueConstraint(columnNames={"name", "client_group_id"})})
public class Spot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    @Comment("스팟 PK")
    private BigInteger id;

    @Size(max = 32)
    @NotNull
    @Column(name = "name", nullable = false, length = 32)
    @Comment("스팟 이름")
    private String name;

    @NotNull
    @Column(name = "emb_address", nullable = false)
    @Comment("스팟 주소")
    private Address address;

    @NotNull
    @Convert(converter = DiningTypesConverter.class)
    @Column(name = "dining_types", nullable = false)
    @Comment("식사 타입")
    private List<DiningType> diningTypes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_group_id")
    @JsonManagedReference(value = "client__group_fk")
    @Comment("그룹")
    private Group group;

    @OneToMany(mappedBy = "spot", fetch = FetchType.LAZY)
    @JsonBackReference(value = "client__spot_fk")
    @Comment("식사 정보 리스트")
    List<MealInfo> mealInfos;

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

    @Comment("스팟 상태 ( 0: 비활성, 1: 활성 )")
    private SpotStatus status = SpotStatus.ACTIVE;

    public Spot(String name, Address address, List<DiningType> diningTypes, Group group) {
        this.name = name;
        this.address = address;
        this.diningTypes = diningTypes;
        this.group = group;
    }

    public MealInfo getMealInfo(DiningType diningType) {
        return this.mealInfos.stream()
                .filter(v -> v.getDiningType().equals(diningType))
                .findAny()
                .orElse(null);
    }

    public LocalTime getDeliveryTime(DiningType diningType) {
        return this.mealInfos.stream()
                .filter(v -> v.getDiningType().equals(diningType))
                .findAny()
                .map(MealInfo::getDeliveryTime)
                .orElse(null);
    }

    public LocalTime getMembershipBenefitTime(DiningType diningType) {
        return this.mealInfos.stream()
                .filter(v -> v.getDiningType().equals(diningType))
                .findAny()
                .map(MealInfo::getMembershipBenefitTime)
                .orElse(null);
    }

    public Geometry getLocation(){
        return this.address.getLocation();
    }

    public void updateSpot(Address address, Group group) {
        this.address = address;
        this.diningTypes = group.getDiningTypes();
    }

    public void updateSpot(SpotResponseDto spotResponseDto) throws ParseException {
        if(this.status == SpotStatus.INACTIVE) {
            this.status = SpotStatus.ACTIVE;
        }

        // TODO: Location 추가
        Address address = new Address(spotResponseDto.getZipCode(), spotResponseDto.getAddress1(), spotResponseDto.getAddress2(), null);
        this.name = spotResponseDto.getSpotName();
        this.status = SpotStatus.ofCode(spotResponseDto.getStatus());
        this.address = address;
    }

    public void updateDiningTypes(List<DiningType> diningTypes) {
        this.diningTypes = diningTypes;
    }

    public void updatedDiningTypes(MealInfo morningMealInfo, MealInfo lunchMealInfo, MealInfo dinnerMealInfo) {
        List<DiningType> newDiningTypes = Stream.of(morningMealInfo, lunchMealInfo, dinnerMealInfo)
                .filter(Objects::nonNull)
                .map(MealInfo::getDiningType)
                .toList();
        Set<DiningType> groupDiningTypes = new HashSet<>(this.getGroup().getDiningTypes());
        if (!groupDiningTypes.containsAll(newDiningTypes)) {
            throw new ApiException(ExceptionEnum.GROUP_DOSE_NOT_HAVE_DINING_TYPE);
        }
        updateDiningTypes(newDiningTypes);
    }
}