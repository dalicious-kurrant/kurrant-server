package co.dalicious.domain.client.mapper;

import co.dalicious.domain.client.dto.SpotResponseDto;
import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.Spot;
import co.dalicious.system.enums.DiningType;
import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import javax.annotation.MatchesPattern;
import javax.persistence.NamedEntityGraph;
import java.math.BigInteger;
import java.time.LocalTime;

@Mapper(componentModel = "spring")
public interface MealInfoMapper {
    @Mapping(source = "lastOrderTime", target = "lastOrderTime")
    @Mapping(source = "serviceDays", target = "serviceDays")
    @Mapping(source = "deliveryTime", target = "deliveryTime", qualifiedByName = "getDeliveryTime")
    @Mapping(source = "diningType", target = "diningType", qualifiedByName = "getDiningType")
    @Mapping(source = "spot", target = "spot")
    MealInfo toEntity(SpotResponseDto spotInfo, String deliveryTime, String serviceDays, String diningType, DayAndTime lastOrderTime, Spot spot);

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


}
