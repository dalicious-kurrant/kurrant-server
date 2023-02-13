package co.dalicious.domain.food.entity;

import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.system.util.converter.FoodTagsConverter;
import co.dalicious.domain.food.entity.enums.FoodStatus;
import co.dalicious.system.util.enums.FoodTag;
import co.dalicious.domain.food.converter.FoodStatusConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "food__food")
public class Food {
    // TODO: 추후 Item 상속 추가
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("ID")
    private BigInteger id;

    @Convert(converter = FoodStatusConverter.class)
    @Column(name = "e_status")
    @Comment("음식 상태(0. 판매종료 1. 판매중, 2. 판매종료)")
    private FoodStatus foodStatus;

    @Column(name = "name")
    @Comment("식품 이름")
    private String name;

    @Column(name = "price", columnDefinition = "DECIMAL(15, 2)")
    @Comment("가격")
    private BigDecimal price;

    @Embedded
    @Comment("이미지 경로")
    private Image image;

    @OneToMany(mappedBy = "food", orphanRemoval = true)
    @JsonBackReference(value = "food_fk")
    @Comment("식사 일정별 가능 수량")
    private List<FoodCapacity> foodCapacities;

    @OneToMany(mappedBy = "food", orphanRemoval = true)
    @JsonBackReference(value = "food_fk")
    @Comment("할인 정책")
    private List<FoodDiscountPolicy> foodDiscountPolicyList;

    @Convert(converter = FoodTagsConverter.class)
    @Comment("음식 태그")
    @Column(name = "e_food_tags")
    private List<FoodTag> foodTags;

    @ManyToOne(fetch = FetchType.LAZY ,optional = false)
    @JoinColumn(name = "makers_id", columnDefinition = "BIGINT UNSIGNED")
    @Comment("메이커스 ID")
    private Makers makers;

    @Column(name = "description")
    @Comment("설명")
    private String description;

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


}
