package co.dalicious.domain.food.mapper;

import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.food.dto.MakersCapacityDto;
import co.dalicious.domain.food.dto.UpdateMakersReqDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.MakersCapacity;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", imports = {DayAndTime.class, DateUtils.class})
public interface MakersCapacityMapper {

    @Mapping(source = "capacity", target = "capacity")
    @Mapping(source = "diningType", target = "diningType", qualifiedByName = "getDiningType")
    @Mapping(source = "makers", target = "makers")
    @Mapping(source = "lastOrderTime", target = "lastOrderTime", qualifiedByName = "stringToLastOrderTime")
    MakersCapacity toEntityForCapacitySave(Makers makers, Integer diningType, Integer capacity, String lastOrderTime);


    default MakersCapacity toEntity(Makers makers, MakersCapacityDto diningTypes) {
        return MakersCapacity.builder()
                .makers(makers)
                .diningType(DiningType.ofCode(diningTypes.getDiningType()))
                .capacity(diningTypes.getCapacity())
                .minTime(DateUtils.stringToLocalTime(diningTypes.getMinTime()))
                .maxTime(DateUtils.stringToLocalTime(diningTypes.getMaxTime()))
                .lastOrderTime(DayAndTime.stringToDayAndTime(diningTypes.getLastOrderTime()))
                .build();
    };

    @Named("getDiningType")
    default DiningType getDiningType(Integer diningType){
        return DiningType.ofCode(diningType);
    }

    @Named("stringToLastOrderTime")
    default DayAndTime stringToLastOrderTime(String lastOrderTime) {
        return DayAndTime.stringToDayAndTime(lastOrderTime);
    }



}

