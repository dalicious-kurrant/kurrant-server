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
    @NoArgsConstructor
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
