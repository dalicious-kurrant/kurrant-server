package co.kurrant.app.client_api.mapper;

import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.embeddable.ServiceDaysAndSupportPrice;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.enums.Days;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DaysUtil;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", imports = {DateUtils.class, DiningType.class})
public interface GroupInfoMapper {

    default GroupListDto.GroupInfoList toCorporationListDto(Group group, User managerUser) {
        GroupListDto.GroupInfoList groupInfoList = new GroupListDto.GroupInfoList();
        boolean isCorporation = group instanceof Corporation;

        groupInfoList.setId(group.getId());

        Integer groupType = null;
        Integer employeeCount = null;
        if(group instanceof Corporation corporation) {
            groupType = GroupDataType.CORPORATION.getCode();
            employeeCount = corporation.getEmployeeCount();
            groupInfoList.setMinimumSpend(corporation.getMinimumSpend());
            groupInfoList.setMaximumSpend(corporation.getMaximumSpend());
        }
        else if(group instanceof OpenGroup openGroup) {
            groupType = GroupDataType.OPEN_GROUP.getCode();
            employeeCount = openGroup.getOpenGroupUserCount();
        }

        groupInfoList.setGroupType(groupType);
        groupInfoList.setEmployeeCount(employeeCount);

        groupInfoList.setCode((isCorporation) ? ((Corporation) group).getCode() : null);
        groupInfoList.setName(group.getName());
        groupInfoList.setZipCode(group.getAddress().getZipCode());
        groupInfoList.setAddress1(group.getAddress().getAddress1());
        groupInfoList.setAddress2(group.getAddress().getAddress2());
        groupInfoList.setLocation((group.getAddress().getLocation() != null) ? String.valueOf(group.getAddress().getLocation()) : null);

        List<DiningType> diningTypeList = group.getDiningTypes();
        groupInfoList.setDiningTypes(diningTypeList.stream().map(DiningType::getCode).toList());
        if(managerUser != null) {
            groupInfoList.setManagerId(managerUser.getId());
            groupInfoList.setManagerName(managerUser.getName());
            groupInfoList.setManagerPhone(managerUser.getPhone());
        }
        groupInfoList.setIsMembershipSupport((isCorporation) ? ((Corporation) group).getIsMembershipSupport() : null);
        groupInfoList.setIsGarbage((isCorporation) ? ((Corporation) group).getIsGarbage() : null);
        groupInfoList.setIsHotStorage((isCorporation) ? ((Corporation) group).getIsHotStorage() : null);
        groupInfoList.setIsSetting((isCorporation) ? ((Corporation) group).getIsSetting() : null);

        List<MealInfo> mealInfoList = group.getMealInfos();
        List<Days> serviceDays = new ArrayList<>();
        List<Days> notSupportDays = new ArrayList<>();
        BigDecimal morningSupportPrice = BigDecimal.ZERO;
        BigDecimal lunchSupportPrice = BigDecimal.ZERO;
        BigDecimal dinnerSupportPrice = BigDecimal.ZERO;

        for(MealInfo mealInfo : mealInfoList) {
            serviceDays = mealInfo.getServiceDays();
            if(mealInfo instanceof CorporationMealInfo corporationMealInfo && diningTypeList.contains(mealInfo.getDiningType())) {
                List<ServiceDaysAndSupportPrice> serviceDaysAndSupportPriceList = corporationMealInfo.getServiceDaysAndSupportPrices();
                for(ServiceDaysAndSupportPrice serviceDaysAndSupportPrice : serviceDaysAndSupportPriceList) {
                    List<Days> days = serviceDaysAndSupportPrice.getSupportDays();
                    BigDecimal supportPrice = serviceDaysAndSupportPrice.getSupportPrice();

                    switch (mealInfo.getDiningType()) {
                        case MORNING -> morningSupportPrice = morningSupportPrice.add(supportPrice);
                        case LUNCH -> lunchSupportPrice = lunchSupportPrice.add(supportPrice);
                        case DINNER -> dinnerSupportPrice = dinnerSupportPrice.add(supportPrice);
                    }

                    notSupportDays = serviceDays.stream().filter(d -> !days.contains(d)).toList();
                }
            }
        }

        List<Days> supportDays = new ArrayList<>(serviceDays);
        supportDays.removeAll(notSupportDays);

        groupInfoList.setServiceDays(DaysUtil.serviceDaysToDaysString(serviceDays));

        return groupInfoList;
    }

}
