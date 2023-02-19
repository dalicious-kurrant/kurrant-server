package co.dalicious.domain.food.mapper;

import co.dalicious.domain.food.dto.PresetScheduleRequestDto;
import co.dalicious.domain.food.dto.PresetScheduleResponseDto;
import co.dalicious.domain.food.entity.PresetDailyFood;
import co.dalicious.domain.food.entity.PresetGroupDailyFood;
import co.dalicious.domain.food.entity.PresetMakersDailyFood;
import co.dalicious.system.util.DateUtils;
import exception.ApiException;
import exception.ExceptionEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface PresetDailyFoodMapper {

    @Mapping(source = "presetDailyFood.id", target = "presetFoodId")
    @Mapping(source = "presetDailyFood.food.name", target = "foodName")
    @Mapping(source = "presetDailyFood.food.foodStatus.status", target = "foodStatus")
    @Mapping(source = "presetDailyFood.capacity", target = "foodCapacity")
    @Mapping(source = "presetDailyFood.scheduleStatus.status", target = "scheduleStatus")
    PresetScheduleResponseDto.foodSchedule toFoodScheduleDto(PresetDailyFood presetDailyFood);

    @Mapping(target = "pickupTime", expression = "java(DateUtils.timeToString(presetGroupDailyFood.getPickupTime()))")
    @Mapping(source = "presetGroupDailyFood.group.name", target = "clientName")
    @Mapping(source = "presetGroupDailyFood.capacity", target = "clientCapacity")
    @Mapping(source = "foodSchedules", target = "foodSchedule")
    PresetScheduleResponseDto.clientSchedule toClientScheduleDto(PresetGroupDailyFood presetGroupDailyFood, List<PresetScheduleResponseDto.foodSchedule> foodSchedules);

    @Mapping(source = "presetMakersDailyFood.id", target = "presetMakersId")
    @Mapping(source = "presetMakersDailyFood.scheduleStatus.status", target = "scheduleStatus")
    @Mapping(source = "presetMakersDailyFood.serviceDate", target = "serviceDate")
    @Mapping(source = "presetMakersDailyFood.diningType", target = "diningType")
    @Mapping(source = "presetMakersDailyFood.capacity", target = "makersCapacity")
    @Mapping(source = "presetMakersDailyFood.deadline", target = "deadline")
    @Mapping(source = "clientSchedule", target = "clientSchedule")
    PresetScheduleResponseDto toDto(PresetMakersDailyFood presetMakersDailyFood, List<PresetScheduleResponseDto.clientSchedule> clientSchedule);

}

