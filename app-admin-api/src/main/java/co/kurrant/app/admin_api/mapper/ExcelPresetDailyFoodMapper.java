package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.entity.*;
import co.dalicious.domain.food.entity.enums.ScheduleStatus;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.ExcelPresetDailyFoodDto;
import co.kurrant.app.admin_api.dto.ExcelPresetDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {DateUtils.class, DiningType.class, ScheduleStatus.class})
public interface ExcelPresetDailyFoodMapper {

    @Mapping(source = "presetDto.serviceDate", target = "serviceDate")
    @Mapping(source = "presetDto.diningType" ,target = "diningType")
    @Mapping(source = "presetDto.makersCapacity" ,target = "capacity")
    @Mapping(source = "makers", target = "makers")
    @Mapping(target = "scheduleStatus", expression = "java(ScheduleStatus.ofCode(scheduleStatus))")
    @Mapping(target = "deadline", expression = "java(DateUtils.stringToLocalDateTime(deadLine))")
    PresetMakersDailyFood toMakersDailyFoodEntity(ExcelPresetDto presetDto, Integer scheduleStatus, Makers makers, String deadLine);

    @Mapping(source = "data.groupCapacity", target = "capacity")
    @Mapping(source = "group", target = "group")
    @Mapping(target = "pickupTime", expression = "java(DateUtils.stringToLocalTime(data.getPickupTime()))")
    @Mapping(source = "presetMakersDailyFood", target = "presetMakersDailyFood")
    PresetGroupDailyFood toGroupDailyFoodEntity(ExcelPresetDailyFoodDto.ExcelData data, Group group, PresetMakersDailyFood presetMakersDailyFood);

    @Mapping(source = "data.foodCapacity", target = "capacity")
    @Mapping(source = "food", target = "food")
    @Mapping(target = "scheduleStatus", expression = "java(ScheduleStatus.ofCode(data.getFoodScheduleStatus()))")
    @Mapping(target = "presetGroupDailyFood", source = "groupDailyFood")
    PresetDailyFood toPresetDailyFoodEntity(ExcelPresetDailyFoodDto.ExcelData data, Food food, PresetGroupDailyFood groupDailyFood);

}
