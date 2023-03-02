package co.kurrant.app.admin_api.mapper;


import co.dalicious.domain.client.entity.ApartmentMealInfo;
import co.dalicious.domain.client.entity.CorporationMealInfo;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.time.LocalTime;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface CorporationMealInfoMapper {

    @Mapping(source = "supportPrice", target = "supportPrice")
    CorporationMealInfo toEntity(DiningType diningType, LocalTime deliveryTime, LocalTime lastOrderTime, String serviceDays, Spot spot, BigDecimal supportPrice);

    @Mapping(source = "diningType", target = "diningType")
    @Mapping(target = "deliveryTime", expression = "java(DateUtils.stringToLocalTime(defaultTime))")
    @Mapping(target = "lastOrderTime", expression = "java(DateUtils.stringToLocalTime(defaultTime))")
    @Mapping(source = "groupInfoList.serviceDays", target = "serviceDays")
    @Mapping(source = "spot", target = "spot")
    @Mapping(target = "membershipBenefitTime", expression = "java(DateUtils.stringToLocalTime(groupInfoList.getMembershipBenefitTime()))")
    @Mapping(target = "supportPrice", expression = "java(getSupportPrice(groupInfoList, diningType))")
    CorporationMealInfo toCorporationMealInfoEntity(GroupExcelRequestDto groupInfoList, Spot spot, DiningType diningType, String defaultTime);

    @Mapping(source = "diningType", target = "diningType")
    @Mapping(target = "deliveryTime", expression = "java(DateUtils.stringToLocalTime(defaultTime))")
    @Mapping(target = "lastOrderTime", expression = "java(DateUtils.stringToLocalTime(defaultTime))")
    @Mapping(source = "groupInfoList.serviceDays", target = "serviceDays")
    @Mapping(source = "spot", target = "spot")
    @Mapping(target = "membershipBenefitTime", expression = "java(DateUtils.stringToLocalTime(groupInfoList.getMembershipBenefitTime()))")
    ApartmentMealInfo toApartmentMealInfoEntity(GroupExcelRequestDto groupInfoList, Spot spot, DiningType diningType, String defaultTime);

    default BigDecimal getSupportPrice(GroupExcelRequestDto groupInfoList, DiningType diningType) {
        BigDecimal supportPrice = BigDecimal.ZERO;
        if(diningType.equals(DiningType.MORNING)) supportPrice = BigDecimal.valueOf(Integer.parseInt(groupInfoList.getMorningSupportPrice()));
        else if(diningType.equals(DiningType.LUNCH)) supportPrice = BigDecimal.valueOf(Integer.parseInt(groupInfoList.getLunchSupportPrice()));
        else if(diningType.equals(DiningType.DINNER)) supportPrice = BigDecimal.valueOf(Integer.parseInt(groupInfoList.getDinnerSupportPrice()));

        return supportPrice;
    }

}
