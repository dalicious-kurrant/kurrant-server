package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.converter.DiningTypesConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
@Getter
@Table(name = "client__spot")
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
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MEAL_INFO));
    }

    public LocalTime getDeliveryTime(DiningType diningType) {
        return this.mealInfos.stream()
                .filter(v -> v.getDiningType().equals(diningType))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MEAL_INFO)).getDeliveryTime();
    }
    
    public Point getLocation(){
        return this.address.getLocation();
    }

}
