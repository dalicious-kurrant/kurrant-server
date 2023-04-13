package co.dalicious.domain.client.mapper;

import co.dalicious.domain.client.dto.SpotResponseDto;
import co.dalicious.domain.client.dto.UpdateSpotDetailRequestDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.system.enums.DiningType;
import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import javax.annotation.MatchesPattern;
import javax.persistence.NamedEntityGraph;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalTime;

@Mapper(componentModel = "spring")
public interface MealInfoMapper {
    @Mapping(source = "lastOrderTime", target = "lastOrderTime")
    @Mapping(source = "serviceDays", target = "serviceDays")
    @Mapping(source = "deliveryTime", target = "deliveryTime", qualifiedByName = "getDeliveryTime")
    @Mapping(source = "diningType", target = "diningType", qualifiedByName = "getDiningType")
    @Mapping(source = "group", target = "group")
    MealInfo toEntity(SpotResponseDto spotInfo, String deliveryTime, String serviceDays, String diningType, DayAndTime lastOrderTime, Group group);

    @Named("getDiningType")
    default DiningType getDiningType(String diningType){
        if (diningType.contains("1")){
            return DiningType.MORNING;
        }
        if (diningType.contains("2")){
            return DiningType.LUNCH;
        }
        return DiningType.DINNER;
    }

    @Named("getDeliveryTime")
    default LocalTime getDeliveryTime(String deliveryTime){
        return LocalTime.parse(deliveryTime);
    }


    @Mapping(source = "mealInfo.lastOrderTime", target = "lastOrderTime")
    @Mapping(source = "serviceDays", target = "serviceDays")
    @Mapping(source = "mealInfo.deliveryTime", target = "deliveryTime", qualifiedByName = "getDeliveryTime")
    @Mapping(source = "diningType", target = "diningType", qualifiedByName = "getDiningTypeByString")
    @Mapping(source = "mealInfo.group", target = "group")
    @Mapping(source = "mealInfo.membershipBenefitTime", target = "membershipBenefitTime")
    @Mapping(source = "updateSpotDetailRequestDto", target = "supportPrice", qualifiedByName = "getSupportPrice")
    CorporationMealInfo toEntityUpdateSpotDetail(MealInfo mealInfo, String serviceDays, String diningType, UpdateSpotDetailRequestDto updateSpotDetailRequestDto);

    @Named("getSupportPrice")
    default BigDecimal getSupportPrice(UpdateSpotDetailRequestDto updateSpotDetailRequestDto){
        String[] split = updateSpotDetailRequestDto.getDiningTypes().split(",");
        for (String diningType : split){
            if (diningType.equals("아침")){
                return updateSpotDetailRequestDto.getBreakfastSupportPrice();
            }

            if (diningType.equals("저녁")){
                return updateSpotDetailRequestDto.getDinnerSupportPrice();
            }
        }
        return updateSpotDetailRequestDto.getLunchSupportPrice();
    }

    @Named("getDiningTypeByString")
    default DiningType getDiningTypeByString(String diningType){
        return DiningType.ofString(diningType);
    }

}
