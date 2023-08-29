package co.dalicious.domain.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class UpdateGroupListDto {

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
        private List<GroupListDto.MealInfo> mealInfos;
        private List<GroupListDto.PrepaidCategory> prepaidCategoryList;
    }
}
