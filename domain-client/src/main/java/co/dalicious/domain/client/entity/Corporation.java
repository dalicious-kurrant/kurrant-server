package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.converter.DiningTypesConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor
@Getter
@Table(name = "client__corporation")
public class Corporation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("기업 고객사 PK")
    private BigInteger id;

    @NotNull
    @Convert(converter = DiningTypesConverter.class)
    @Column(name = "dining_types", nullable = false)
    @Comment("식사 타입")
    private List<DiningType> diningTypes;

    @Size(max = 64)
    @NotNull
    @Column(name = "name", nullable = false, length = 64)
    @Comment("기업명")
    private String name;

    @Column(name = "employee_count")
    @Comment("사원수")
    private Integer employeeCount;

    @Column(name = "manager_id")
    @Comment("담당자 유저 id")
    private Long managerId;

    @Embedded
    @Comment("주소")
    private Address address;

    @Column(name = "is_garbage")
    @Comment("쓰레기 수거 서비스 사용 유무")
    private Boolean isGarbage;

    @Column(name = "is_hot_storage")
    @Comment("온장고 대여 서비스 사용 유무")
    private Boolean isHotStorage;

    @Column(name = "is_setting")
    @Comment("식사 세팅 지원 서비스 사용 유무")
    private Boolean isSetting;

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

    @OneToMany(mappedBy = "corporation", fetch = FetchType.LAZY)
    @JsonBackReference(value = "client__corporation_fk")
    @Comment("스팟 리스트")
    List<CorporationSpot> spots;

    @OneToMany(mappedBy = "corporation", fetch = FetchType.LAZY)
    @JsonBackReference(value = "client__corporation_fk")
    @Comment("식사 정보 리스트")
    List<CorporationMealInfo> mealInfos;
}