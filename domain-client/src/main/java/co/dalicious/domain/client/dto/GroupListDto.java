package co.dalicious.domain.client.dto;

import co.dalicious.domain.client.entity.Group;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class GroupListDto {
    List<GroupInfo> groupIdList;
    List<GroupInfoList> groupInfoList;

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
        private String supportDays;
        private String notSupportDays;
        private BigInteger managerId;
        private String managerName;
        private String managerPhone;
        private Boolean isMembershipSupport;
        private Integer employeeCount;
        private Boolean isSetting;
        private Boolean isGarbage;
        private Boolean isHotStorage;
        private Boolean isPrepaid;
        private String membershipEndDate;
        private BigDecimal morningSupportPrice;
        private BigDecimal lunchSupportPrice;
        private BigDecimal dinnerSupportPrice;
        private BigDecimal minimumSpend;
        private BigDecimal maximumSpend;
        private String memo;
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
