package co.dalicious.domain.client.mapper;

import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.MySpotZoneMealInfo;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DaysUtil;
import org.mapstruct.Mapper;

import java.time.LocalTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MealInfoMapper {
//    @Mapping(source = "lastOrderTime", target = "lastOrderTime")
//    @Mapping(source = "serviceDays", target = "serviceDays")
//    @Mapping(source = "deliveryTime", target = "deliveryTime", qualifiedByName = "getDeliveryTime")
//    @Mapping(source = "diningType", target = "diningType", qualifiedByName = "getDiningType")
//    @Mapping(source = "group", target = "group")
//    MealInfo toEntity(SpotResponseDto spotInfo, String deliveryTime, String serviceDays, String diningType, DayAndTime lastOrderTime, Group group);
//
//    @Named("getDiningType")
//    default DiningType getDiningType(String diningType){
//        if (diningType.contains("1")){
//            return DiningType.MORNING;
//        }
//        if (diningType.contains("2")){
//            return DiningType.LUNCH;
//        }
//        return DiningType.DINNER;
//    }
//
//    @Named("getDeliveryTime")
//    default LocalTime getDeliveryTime(String deliveryTime){
//        return LocalTime.parse(deliveryTime);
//    }
//
//
//    @Mapping(source = "mealInfo.lastOrderTime", target = "lastOrderTime")
//    @Mapping(source = "serviceDays", target = "serviceDays")
//    @Mapping(source = "mealInfo.deliveryTime", target = "deliveryTime", qualifiedByName = "getDeliveryTime")
//    @Mapping(source = "diningType", target = "diningType", qualifiedByName = "getDiningTypeByString")
//    @Mapping(source = "mealInfo.group", target = "group")
//    @Mapping(source = "mealInfo.membershipBenefitTime", target = "membershipBenefitTime")
//    @Mapping(source = "updateSpotDetailRequestDto", target = "supportPrice", qualifiedByName = "getSupportPrice")
//    CorporationMealInfo toEntityUpdateSpotDetail(MealInfo mealInfo, String serviceDays, String diningType, UpdateSpotDetailRequestDto updateSpotDetailRequestDto);
//
//    @Named("getSupportPrice")
//    default BigDecimal getSupportPrice(UpdateSpotDetailRequestDto updateSpotDetailRequestDto){
//        String[] split = updateSpotDetailRequestDto.getDiningTypes().split(",");
//        for (String diningType : split){
//            if (diningType.equals("1")){
//                return updateSpotDetailRequestDto.getBreakfastSupportPrice();
//            }
//
//            if (diningType.equals("3")){
//                return updateSpotDetailRequestDto.getDinnerSupportPrice();
//            }
//        }
//        return updateSpotDetailRequestDto.getLunchSupportPrice();
//    }
//
//    @Named("getDiningTypeByString")
//    default DiningType getDiningTypeByString(String diningType){
//        return DiningType.ofCode(Integer.valueOf(diningType));
//    }
//
    default MySpotZoneMealInfo toMealInfo(Group group, DiningType diningType, LocalTime deliveryTime, String lastOrderTime, String useDays, String membershipBenefitTime) {
        // MealInfo 를 생성하기 위한 기본값이 존재하지 않으면 객체 생성 X
        if (lastOrderTime == null || useDays == null) {
            return null;
        }

        return MySpotZoneMealInfo.builder()
                .group(group)
                .diningType(diningType)
                .lastOrderTime(DayAndTime.stringToDayAndTime(lastOrderTime))
                .deliveryTimes(List.of(deliveryTime))
                .serviceDays(DaysUtil.serviceDaysToDaysList(useDays))
                .membershipBenefitTime(MealInfo.stringToDayAndTime(membershipBenefitTime))
                .build();

    }

    default MySpotZoneMealInfo toMealInfo(Group group, DiningType diningType, List<LocalTime> deliveryTime, String lastOrderTime, String useDays, String membershipBenefitTime) {
        // MealInfo 를 생성하기 위한 기본값이 존재하지 않으면 객체 생성 X
        if (lastOrderTime == null || useDays == null) {
            return null;
        }

        return MySpotZoneMealInfo.builder()
                .group(group)
                .diningType(diningType)
                .lastOrderTime(DayAndTime.stringToDayAndTime(lastOrderTime))
                .deliveryTimes(deliveryTime)
                .serviceDays(DaysUtil.serviceDaysToDaysList(useDays))
                .membershipBenefitTime(MealInfo.stringToDayAndTime(membershipBenefitTime))
                .build();

    }
}
