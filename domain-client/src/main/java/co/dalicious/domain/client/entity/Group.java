package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.UpdateSpotDetailRequestDto;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.converter.DiningTypesConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.io.ParseException;

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
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "client__group")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("아파트 고객사 PK")
    private BigInteger id;

    @Embedded
    @Comment("주소")
    private Address address;

    @NotNull
    @Convert(converter = DiningTypesConverter.class)
    @Column(name = "dining_types", nullable = false)
    @Comment("식사 타입")
    private List<DiningType> diningTypes;

    @Size(max = 64)
    @NotNull
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(64)", unique = true)
    @Comment("그룹 이름")
    private String name;

    @Column(columnDefinition = "BIT(1) DEFAULT 1")
    @Comment("활성화 여부")
    private Boolean isActive;

    @Comment("계약 시작 날짜")
    private LocalDate contractStartDate;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "created_datetime", nullable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "updated_datetime", nullable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    @JsonBackReference(value = "client__group_fk")
    @Comment("식사 정보 리스트")
    List<MealInfo> mealInfos;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    @JsonBackReference(value = "client__apartment_fk")
    @Comment("스팟 리스트")
    List<Spot> spots;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    @JsonBackReference(value = "client__department_fk")
    List<Department> departments;

    @Comment("메모")
    @Column(name="memo", columnDefinition = "text")
    private String memo;

    public Group(Address address, List<DiningType> diningTypes, String name, String memo) {
        this.address = address;
        this.diningTypes = diningTypes;
        this.name = name;
        this.memo = memo;
    }

    public Group(BigInteger id){
        this.id = id;
    }

    public static Group getGroup(List<Group> groups, String name) {
        return groups.stream()
                .filter(v -> v.getName().equals(name))
                .findAny()
                .orElse(null);
    }

    public static Group getGroup(List<Group> groups, BigInteger id) {
        return groups.stream()
                .filter(v -> v.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    public static List<Group> getGroups(List<Group> groups, List<String> name) {
        return groups.stream()
                .filter(group -> name.contains(group.getName()))
                .toList();
    }

    public MealInfo getMealInfo(DiningType diningType) {
        return this.getMealInfos().stream()
                .filter(v -> v.getDiningType().equals(diningType))
                .findAny()
                .orElse(null);
    }

    public void updateGroup(Address address, List<DiningType> diningTypeList, String name, Boolean isActive) {
        this.isActive = isActive;
        this.address = address;
        this.diningTypes = diningTypeList;
        this.name = name;
    }

    public void updateDiningTypes(List<DiningType> diningTypes) {
        this.diningTypes = diningTypes;
    }

    public void updateGroup(UpdateSpotDetailRequestDto spotResponseDto) throws ParseException {
        // TODO: Location 추가
        Address address = new Address(spotResponseDto.getZipCode(), spotResponseDto.getAddress1(), spotResponseDto.getAddress2(), null, null);
        this.isActive = spotResponseDto.getIsActive();
        this.name = spotResponseDto.getSpotName();
        this.address = address;
    }

    public void updateGroup(List<DiningType> diningTypes, String name, String memo) {
        this.diningTypes = diningTypes;
        this.name = name;
        this.memo = memo;
    }

    public void updateIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setAddress(Address address){
        this.address = address;
    }

    public void setDiningTypes(List<DiningType> diningTypes) {
        this.diningTypes = diningTypes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setContractStartDate(LocalDate contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void updateAddress(Address address){
        this.address = address;
    }
}
