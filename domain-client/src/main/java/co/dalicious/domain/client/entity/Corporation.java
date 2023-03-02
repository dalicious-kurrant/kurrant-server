package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.system.converter.FoodTagsConverter;
import co.dalicious.system.enums.FoodTag;
import co.dalicious.system.converter.IdListConverter;
import co.dalicious.system.enums.DiningType;
import com.querydsl.core.group.GroupExpression;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "client__corporation")
public class Corporation extends Group{
    @Comment("그룹 코드")
    private String code;

    @Column(name = "is_membership_support", columnDefinition = "BIT(1) DEFAULT 0")
    @Comment("기업 멤버십 지원 여부")
    private Boolean isMembershipSupport;

    @Column(name = "employee_count")
    @Comment("사원수")
    private Integer employeeCount;

    @Column(name = "is_garbage", columnDefinition = "BIT(1) DEFAULT 0")
    @Comment("쓰레기 수거 서비스 사용 유무")
    private Boolean isGarbage;

    @Column(name = "is_hot_storage", columnDefinition = "BIT(1) DEFAULT 0")
    @Comment("온장고 대여 서비스 사용 유무")
    private Boolean isHotStorage;

    @Column(name = "is_setting", columnDefinition = "BIT(1) DEFAULT 0")
    @Comment("식사 세팅 지원 서비스 사용 유무")
    private Boolean isSetting;

    @Column(columnDefinition = "BIT(1) DEFAULT 0")
    @Comment("샐러드 필수")
    private Boolean isSaladRequired;

    @Convert(converter = FoodTagsConverter.class)
    @Comment("필수 포함 음식 태그")
    @Column(name = "e_required_food_tags")
    private List<FoodTag> requiredFoodTags;

    @Convert(converter = FoodTagsConverter.class)
    @Column(name = "e_excluded_food_tags")
    @Comment("필수 제외 음식 태그")
    private List<FoodTag> excludedFoodTags;

    @Convert(converter = IdListConverter.class)
    @Comment("특정 메이커스 포함")
    private List<BigInteger> requiredMakers;

    @Convert(converter = IdListConverter.class)
    @Comment("특정 메이커스 제외")
    private List<BigInteger> excludedMakers;

    @Convert(converter = IdListConverter.class)
    @Comment("특정 상품 포함")
    private List<BigInteger> requiredFood;

    @Convert(converter = IdListConverter.class)
    @Comment("특정 상품 제외")
    private List<BigInteger> excludedFood;

    @Builder
    public Corporation(Address address, List<DiningType> diningTypes, String name, BigInteger managerId, Integer employeeCount, Boolean isGarbage, Boolean isHotStorage, Boolean isSetting, String code, Boolean isMembershipSupport) {
        super(address, diningTypes, name, managerId);
        this.code = code;
        this.employeeCount = employeeCount;
        this.isMembershipSupport = isMembershipSupport;
        this.isGarbage = isGarbage;
        this.isHotStorage = isHotStorage;
        this.isSetting = isSetting;
    }

    public void updateCorporation(GroupExcelRequestDto groupInfoList, Address address, List<DiningType> diningTypeList, Boolean isMembershipSupport, Boolean isSetting, Boolean isGarbage, Boolean isHotStorage) {
        updateGroup(address, diningTypeList, groupInfoList.getName(), groupInfoList.getManagerId());
        this.code = groupInfoList.getCode();
        this.employeeCount = groupInfoList.getEmployeeCount();
        this.isMembershipSupport = isMembershipSupport;
        this.isGarbage = isGarbage;
        this.isHotStorage = isHotStorage;
        this.isSetting = isSetting;
    }
}