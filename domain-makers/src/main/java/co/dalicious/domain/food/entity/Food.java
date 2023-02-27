package co.dalicious.domain.food.entity;

import co.dalicious.domain.file.dto.ImageCreateRequestDto;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.food.dto.FoodListDto;
import co.dalicious.domain.food.dto.MakersFoodDetailReqDto;
import co.dalicious.system.converter.FoodTagsConverter;
import co.dalicious.domain.food.entity.enums.FoodStatus;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.enums.DiscountType;
import co.dalicious.system.enums.FoodTag;
import co.dalicious.domain.food.converter.FoodStatusConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
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

    @ElementCollection
    @Comment("이미지 경로")
    private List<Image> images = new ArrayList<>();

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
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

    @Column(name = "custom_price")
    @Comment("커스텀 상품 가격")
    private BigDecimal customPrice;

    @Builder
    public Food(FoodStatus foodStatus, String name, BigDecimal price, List<FoodTag> foodTags, Makers makers, String description, BigDecimal customPrice) {
        this.foodStatus = foodStatus;
        this.name = name;
        this.price = price;
        this.foodTags = foodTags;
        this.makers = makers;
        this.description = description;
        this.customPrice = customPrice;
    }

    public void updateFoodMass(FoodListDto foodListDto, List<FoodTag> foodTags, Makers makers) {
        this.foodStatus = FoodStatus.ofString(foodListDto.getFoodStatus());
        this.name = foodListDto.getFoodName();
        this.price = foodListDto.getDefaultPrice();
        this.foodTags = foodTags;
        this.description = foodListDto.getDescription();
        this.makers = makers;
    }

    public void updateFoodStatus(FoodStatus foodStatus) {
        this.foodStatus = foodStatus;
    }

    public void updateFood(MakersFoodDetailReqDto makersFoodDetailReqDto) {
        if (!this.getId().equals(makersFoodDetailReqDto.getFoodId())) {
            throw new ApiException(ExceptionEnum.NOT_FOUND_FOOD);
        }
        this.price = makersFoodDetailReqDto.getDefaultPrice();
        this.foodTags = FoodTag.ofCodes(makersFoodDetailReqDto.getFoodTags());
        this.customPrice = makersFoodDetailReqDto.getCustomPrice();
        this.description = makersFoodDetailReqDto.getDescription();
    }

    public void updateImages(List<Image> images) {
        this.images = images;
    }

    public FoodDiscountPolicy getFoodDiscountPolicy(DiscountType discountType) {
        return this.foodDiscountPolicyList.stream()
                .filter(v -> v.getDiscountType().equals(discountType))
                .findAny()
                .orElse(null);
    }

    public FoodCapacity getFoodCapacity(DiningType diningType) {
        return getFoodCapacities().stream()
                .filter(v -> v.getDiningType().equals(diningType))
                .findAny()
                .orElse(null);
    }

    public FoodCapacity updateFoodCapacity(DiningType diningType, Integer capacity) {
        FoodCapacity foodCapacity = getFoodCapacity(diningType);
        if (foodCapacity == null) {
            if (this.makers.getMakersCapacity(diningType).getCapacity().equals(capacity)) {
                return null;
            } else FoodCapacity.builder()
                    .capacity(capacity)
                    .food(this)
                    .diningType(diningType)
                    .build();
        } else {
            if (foodCapacity.getCapacity().equals(capacity)) {
                return null;
            } else {
                foodCapacity.updateCapacity(capacity);
            }

        }
        return null;
    }
}
