package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.dalicious.domain.client.dto.UpdateSpotDetailRequestDto;
import co.dalicious.domain.client.entity.enums.PaycheckCategoryItem;
import co.dalicious.system.converter.FoodTagsConverter;
import co.dalicious.system.enums.FoodTag;
import co.dalicious.system.converter.IdListConverter;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "client__corporation")
public class Corporation extends Group {
    @Comment("그룹 코드")
    private String code;

    @Comment("정산 선불 여부")
    private Boolean isPrepaid;

    @Comment("멤버십 계약 종료 날짜")
    private LocalDate membershipEndDate;

    @ElementCollection
    @Comment("지불 항목 내역")
    @CollectionTable(name = "client__corporation_paycheck_categories")
    private List<PrepaidCategory> prepaidCategories;

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

    @Column(name = "minimum_spend")
    @Comment("최소 구매 가능 금액")
    private BigDecimal minimumSpend;

    @Column(name = "maximum_spend")
    @Comment("최대 구매 가능 금액")
    private BigDecimal maximumSpend;

    @Column(name = "manager_id")
    @Comment("담당자 유저 id")
    private BigInteger managerId;

    @Builder
    public Corporation(Address address, LocalDate membershipEndDate, List<DiningType> diningTypes, String name, BigInteger managerId, String memo, String code, Boolean isPrepaid, List<PrepaidCategory> prepaidCategories, Boolean isMembershipSupport, Integer employeeCount, Boolean isGarbage, Boolean isHotStorage, Boolean isSetting, Boolean isSaladRequired, List<FoodTag> requiredFoodTags, List<FoodTag> excludedFoodTags, List<BigInteger> requiredMakers, List<BigInteger> excludedMakers, List<BigInteger> requiredFood, List<BigInteger> excludedFood, BigDecimal minimumSpend, BigDecimal maximumSpend) {
        super(address, diningTypes, name, memo);
        this.code = code;
        this.isPrepaid = isPrepaid;
        this.membershipEndDate = membershipEndDate;
        this.prepaidCategories = prepaidCategories;
        this.isMembershipSupport = isMembershipSupport;
        this.employeeCount = employeeCount;
        this.isGarbage = isGarbage;
        this.isHotStorage = isHotStorage;
        this.isSetting = isSetting;
        this.isSaladRequired = isSaladRequired;
        this.requiredFoodTags = requiredFoodTags;
        this.excludedFoodTags = excludedFoodTags;
        this.requiredMakers = requiredMakers;
        this.excludedMakers = excludedMakers;
        this.requiredFood = requiredFood;
        this.excludedFood = excludedFood;
        this.minimumSpend = minimumSpend;
        this.maximumSpend = maximumSpend;
        this.managerId = managerId;
    }

    public void updateCorporation(GroupExcelRequestDto groupInfoList, Address address, List<DiningType> diningTypeList) {
        updateGroup(address, diningTypeList, groupInfoList.getName());
        this.membershipEndDate = DateUtils.stringToDate(groupInfoList.getMembershipEndDate());
        this.code = groupInfoList.getCode();
        this.employeeCount = groupInfoList.getEmployeeCount();
        this.isMembershipSupport = !groupInfoList.getIsMembershipSupport().equals("미지원");
        this.isGarbage = useOrNotUse(groupInfoList.getIsGarbage());
        this.isHotStorage = useOrNotUse(groupInfoList.getIsHotStorage());
        this.isSetting = useOrNotUse(groupInfoList.getIsSetting());
        this.isPrepaid = useOrNotUse(groupInfoList.getIsPrepaid());
        this.minimumSpend = (groupInfoList.getMinimumSpend() == null) ? null : BigDecimal.valueOf(groupInfoList.getMinimumSpend());
        this.maximumSpend = (groupInfoList.getMaximumSpend() == null) ? null : BigDecimal.valueOf(groupInfoList.getMaximumSpend());
        this.managerId = groupInfoList.getManagerId();
    }

    public void updateCorporation(UpdateSpotDetailRequestDto groupInfoList, Address address, List<DiningType> diningTypeList) {
        updateGroup(address, diningTypeList, groupInfoList.getSpotName());
        this.code = groupInfoList.getCode();
        this.employeeCount = groupInfoList.getEmployeeCount();
        this.isMembershipSupport = !groupInfoList.getIsMembershipSupport().equals("미지원");
        this.isGarbage = groupInfoList.getIsGarbage();
        this.isHotStorage = groupInfoList.getIsHotStorage();
        this.isSetting = groupInfoList.getIsSetting();
        this.isPrepaid = groupInfoList.getIsPrepaid();
        this.membershipEndDate = DateUtils.stringToDate(groupInfoList.getMembershipEndDate());
        this.minimumSpend = (groupInfoList.getMinPrice() == null) ? null : groupInfoList.getMinPrice();
        this.maximumSpend = (groupInfoList.getMaxPrice() == null) ? null : groupInfoList.getMaxPrice();
        this.managerId = groupInfoList.getManagerId();
    }


    private Boolean useOrNotUse(String data) {
        Boolean use = null;
        if (data.equals("미사용") || data.equals("false")) use = false;
        else if (data.equals("사용") || data.equals("true")) use = true;
        return use;
    }

    public PrepaidCategory getPrepaidCategory(PaycheckCategoryItem paycheckCategoryItem) {
        if (this.prepaidCategories == null || this.prepaidCategories.isEmpty()) {
            return null;
        }
        return this.prepaidCategories.stream()
                .filter(v -> v.getPaycheckCategoryItem().equals(paycheckCategoryItem))
                .findAny()
                .orElse(null);
    }

    public void updateMembershipSupport(Boolean membershipSupport) {
        isMembershipSupport = membershipSupport;
    }

    public void updateGarbage(Boolean garbage) {
        isGarbage = garbage;
    }

    public void updateHotStorage(Boolean hotStorage) {
        isHotStorage = hotStorage;
    }

    public void updateSetting(Boolean setting) {
        isSetting = setting;
    }

    public void updatePrepaidCategories(List<PrepaidCategory> prepaidCategories) {
        List<PrepaidCategory> mutablePrepaidCategories = new ArrayList<>(prepaidCategories);
        mutablePrepaidCategories.removeIf(prepaidCategory -> prepaidCategory.getCount() == null && prepaidCategory.getPrice() == null && prepaidCategory.getTotalPrice() == null);
        this.prepaidCategories = mutablePrepaidCategories;
    }

    public void updatePrepaid(Boolean prepaid) {
        isPrepaid = prepaid;
    }
}