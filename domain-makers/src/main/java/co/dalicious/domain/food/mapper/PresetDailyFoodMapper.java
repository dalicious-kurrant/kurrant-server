package co.dalicious.domain.food.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.dto.PresetScheduleDto;
import co.dalicious.domain.food.dto.PresetScheduleResponseDto;
import co.dalicious.domain.food.entity.*;
import co.dalicious.domain.food.entity.enums.ScheduleStatus;
import co.dalicious.domain.recommend.dto.RecommendScheduleDto;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import exception.ApiException;
import exception.ExceptionEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = {DateUtils.class, DiningType.class, ScheduleStatus.class})
public interface PresetDailyFoodMapper {

    @Mapping(source = "presetDailyFood.id", target = "presetFoodId")
    @Mapping(source = "presetDailyFood.food.name", target = "foodName")
    @Mapping(source = "presetDailyFood.food.foodStatus.status", target = "foodStatus")
    @Mapping(source = "presetDailyFood.capacity", target = "foodCapacity")
    @Mapping(source = "presetDailyFood.scheduleStatus.code", target = "scheduleStatus")
    PresetScheduleResponseDto.foodSchedule toFoodScheduleDto(PresetDailyFood presetDailyFood);

    @Mapping(target = "pickupTime", expression = "java(DateUtils.timeToString(presetGroupDailyFood.getPickupTime()))")
    @Mapping(source = "presetGroupDailyFood.group.name", target = "clientName")
    @Mapping(source = "presetGroupDailyFood.capacity", target = "clientCapacity")
    @Mapping(source = "foodSchedules", target = "foodSchedule")
    PresetScheduleResponseDto.clientSchedule toClientScheduleDto(PresetGroupDailyFood presetGroupDailyFood, List<PresetScheduleResponseDto.foodSchedule> foodSchedules);

    @Mapping(source = "presetMakersDailyFood.id", target = "presetMakersId")
    @Mapping(source = "presetMakersDailyFood.scheduleStatus.code", target = "scheduleStatus")
    @Mapping(source = "presetMakersDailyFood.serviceDate", target = "serviceDate")
    @Mapping(source = "presetMakersDailyFood.diningType.diningType", target = "diningType")
    @Mapping(source = "presetMakersDailyFood.capacity", target = "makersCapacity")
    @Mapping(target = "deadline", expression = "java(DateUtils.localDateTimeToString(presetMakersDailyFood.getDeadline()))")
    @Mapping(source = "clientSchedule", target = "clientSchedule")
    @Mapping(source = "presetMakersDailyFood.makers.name", target = "makersName")
    PresetScheduleResponseDto toDto(PresetMakersDailyFood presetMakersDailyFood, List<PresetScheduleResponseDto.clientSchedule> clientSchedule);

    @Mapping(source = "food.name", target = "foodName")
    @Mapping(source = "food.foodStatus.status", target = "foodStatus")
    @Mapping(target = "foodCapacity", expression = "java(getFoodCapacity(food.getFoodCapacities(), data))")
    @Mapping(target = "scheduleStatus", expression = "java(ScheduleStatus.WAITING.getCode())")
    PresetScheduleResponseDto.foodSchedule recommendToFoodScheduleDto(Food food, RecommendScheduleDto data);

    @Mapping(source = "pickupTime", target = "pickupTime")
    @Mapping(source = "group.name", target = "clientName")
    @Mapping(source = "groupCapacity", target = "clientCapacity")
    @Mapping(source = "foodSchedules", target = "foodSchedule")
    PresetScheduleResponseDto.clientSchedule recommendToClientScheduleDto(Group group, Integer groupCapacity, String pickupTime, List<PresetScheduleResponseDto.foodSchedule> foodSchedules);

    @Mapping(target = "scheduleStatus", expression = "java(ScheduleStatus.WAITING.getCode())")
    @Mapping(source = "recommendScheduleDto.serviceDate", target = "serviceDate")
    @Mapping(source = "recommendScheduleDto.diningType.diningType", target = "diningType")
    @Mapping(target = "makersCapacity", expression = "java(getMakersCapacity(makers.getMakersCapacities(), recommendScheduleDto))")
    @Mapping(target = "deadline", ignore = true)
    @Mapping(source = "makers.name", target = "makersName")
    @Mapping(source = "clientSchedule", target = "clientSchedule")
    PresetScheduleResponseDto recommendToDto(Makers makers, RecommendScheduleDto recommendScheduleDto, List<PresetScheduleResponseDto.clientSchedule> clientSchedule);

//    @Mapping(target = "serviceDate", expression = "java(DateUtils.stringToDate(data.getServiceDate()))")
//    @Mapping(target = "diningType", expression = "java(DiningType.ofString(data.getDiningType()))")
//    @Mapping(target = "capacity", expression = "java(getMakersCapacity(makers.getMakersCapacities(), data))")
//    @Mapping(source = "makers", target = "makers")
//    @Mapping(target = "scheduleStatus", expression = "java(ScheduleStatus.REQUEST)")
//    @Mapping(target = "deadline", expression = "java(getDeadLine())")
//    PresetMakersDailyFood toMakersDailyFoodEntity(PresetScheduleDto data, Makers makers);

//
    default Integer getMakersCapacity(List<MakersCapacity> makersCapacityList, PresetScheduleDto data) {
        MakersCapacity makersCapacity = makersCapacityList.stream()
                .filter(capa -> capa.getDiningType().getDiningType().equals(data.getDiningType()))
                .findFirst().orElseThrow( () -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS_CAPACITY));
        return makersCapacity.getCapacity();
    }

    default Integer getMakersCapacity(List<MakersCapacity> makersCapacityList, RecommendScheduleDto data) {
        MakersCapacity makersCapacity = makersCapacityList.stream()
                .filter(capa -> capa.getDiningType().equals(data.getDiningType()))
                .findFirst().orElseThrow( () -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS_CAPACITY));
        return makersCapacity.getCapacity();
    }

    default Integer getFoodCapacity(List<FoodCapacity> foodCapacities, RecommendScheduleDto data) {
        FoodCapacity foodCapacity = foodCapacities.stream()
                .filter(capa -> capa.getDiningType().equals(data.getDiningType()))
                .findFirst().orElseThrow( () -> new ApiException(ExceptionEnum.NOT_FOUND_FOOD_CAPACITY));
        return foodCapacity.getCapacity();
    }
//
//    default LocalDateTime getDeadLine() { return LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusDays(3); }
}

