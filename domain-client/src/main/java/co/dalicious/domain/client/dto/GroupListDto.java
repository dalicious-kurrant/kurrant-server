package co.dalicious.domain.client.dto;

import co.dalicious.domain.client.entity.Group;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GroupListDto {
    List<GroupInfo> groupIdList;
    List<GroupInfoList> groupInfoList;

    @Builder
    public GroupListDto(List<GroupInfo> groupIdList, List<GroupInfoList> groupInfoList) {
        this.groupIdList = groupIdList;
        this.groupInfoList = groupInfoList;
    }

    @Getter
    @Setter
    public static class GroupInfoList {
        private BigInteger id;
        private Integer groupType;
        private String code;
        private String name;
        private String zipCode;
        private String address1;
        private String address2;
        private String location;
        private List<Integer> diningTypes;
        private String serviceDays;
        private BigInteger managerId;
        private String managerName;
        private String managerPhone;
        private Boolean isMembershipSupport;
        private Integer employeeCount;
        private Boolean isSetting;
        private Boolean isGarbage;
        private Boolean isHotStorage;
        private Boolean isPrepaid;
        private Boolean isActive;
        private Boolean isSaladRequired;
        private String contractStartDate;
        private String deliveryFeeOption;
        private String membershipEndDate;
        private BigDecimal minimumSpend;
        private BigDecimal maximumSpend;
        private String memo;
        private List<MealInfo> mealInfos;
        private List<PrepaidCategory> prepaidCategoryList;
        private List<CategoryPrice> categoryPrices;

        public GroupInfoList() {
            List<CategoryPrice> categoryPrices1 = new ArrayList<>();
            for (co.dalicious.system.enums.CategoryPrice categoryPrice : co.dalicious.system.enums.CategoryPrice.values()) {
                categoryPrices1.add(new CategoryPrice(categoryPrice));
            }
            this.categoryPrices = categoryPrices1;
        }
    }

    @Getter
    public static class CategoryPrice {
        private final Integer code;
        private final String category;
        private final Integer price;

        public CategoryPrice(co.dalicious.system.enums.CategoryPrice categoryPrice) {
            this.code = categoryPrice.getCode();
            this.category = categoryPrice.getCategory();
            this.price = categoryPrice.getPrice().intValue();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PrepaidCategory {
        private Integer code;
        private String paycheckCategoryItem;
        private Integer count;
        private Integer price;
        private Integer totalPrice;

        @Builder
        public PrepaidCategory(String paycheckCategoryItem, Integer count, Integer price, Integer totalPrice, Integer code) {
            this.code = code;
            this.paycheckCategoryItem = paycheckCategoryItem;
            this.count = count;
            this.price = price;
            this.totalPrice = totalPrice;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MealInfo {
        private Integer diningType;
        private String deliveryTimes;
        private String membershipBenefitTime;
        private String lastOrderTime;
        private String serviceDays;
        private List<SupportPriceByDay> supportPriceByDays;

        @Builder
        public MealInfo(Integer diningType, String deliveryTimes, String membershipBenefitTime, String lastOrderTime, String serviceDays, List<SupportPriceByDay> supportPriceByDays) {
            this.diningType = diningType;
            this.deliveryTimes = deliveryTimes;
            this.membershipBenefitTime = membershipBenefitTime;
            this.lastOrderTime = lastOrderTime;
            this.serviceDays = serviceDays;
            this.supportPriceByDays = supportPriceByDays;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SupportPriceByDay {
        private String serviceDay;
        private BigDecimal supportPrice;

        public SupportPriceByDay(String serviceDay, BigDecimal supportPrice) {
            this.serviceDay = serviceDay;
            this.supportPrice = supportPrice;
        }
    }

    public static GroupListDto createGroupListDto(List<Group> groupList, List<GroupInfoList> groupInfoList) {
        List<GroupInfo> groupIdLists = new ArrayList<>();
        for(Group group : groupList) {
            GroupInfo groupIdList = GroupInfo.create(group.getId(), group.getName());
            groupIdLists.add(groupIdList);
        }
        return GroupListDto.builder()
                .groupIdList(groupIdLists)
                .groupInfoList(groupInfoList)
                .build();
    }
}
