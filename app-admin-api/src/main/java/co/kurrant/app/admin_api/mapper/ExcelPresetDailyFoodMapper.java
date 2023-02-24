package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.entity.*;
import co.dalicious.domain.food.entity.enums.ConfirmStatus;
import co.dalicious.domain.food.entity.enums.ScheduleStatus;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.schedules.ExcelPresetDailyFoodDto;
import co.kurrant.app.admin_api.dto.schedules.ExcelPresetDto;
import exception.ApiException;
import exception.ExceptionEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", imports = {DateUtils.class, DiningType.class, ScheduleStatus.class, ConfirmStatus.class})
public interface ExcelPresetDailyFoodMapper {

    @Mapping(source = "presetDto.serviceDate", target = "serviceDate")
    @Mapping(source = "presetDto.diningType" ,target = "diningType")
    @Mapping(target = "capacity", expression = "java(checkMakersCapacity(makers.getMakersCapacities(), presetDto))")
    @Mapping(source = "makers", target = "makers")
    @Mapping(target = "scheduleStatus", expression = "java(ScheduleStatus.ofCode(scheduleStatus))")
    @Mapping(target = "deadline", expression = "java(DateUtils.stringToLocalDateTime(deadLine))")
    @Mapping(target = "confirmStatus", source = "confirmStatus")
    PresetMakersDailyFood toMakersDailyFoodEntity(ExcelPresetDto presetDto, Integer scheduleStatus, Makers makers, String deadLine, ConfirmStatus confirmStatus);

    @Mapping(source = "data.groupCapacity", target = "capacity")
    @Mapping(source = "group", target = "group")
    @Mapping(target = "pickupTime", expression = "java(DateUtils.stringToLocalTime(data.getPickupTime()))")
    @Mapping(source = "presetMakersDailyFood", target = "presetMakersDailyFood")
    PresetGroupDailyFood toGroupDailyFoodEntity(ExcelPresetDailyFoodDto.ExcelData data, Group group, PresetMakersDailyFood presetMakersDailyFood);

    @Mapping(target = "capacity", expression = "java(checkFoodCapacity(food.getFoodCapacities(), data))")
    @Mapping(source = "food", target = "food")
    @Mapping(target = "scheduleStatus", expression = "java(ScheduleStatus.ofCode(data.getFoodScheduleStatus()))")
    @Mapping(target = "presetGroupDailyFood", source = "groupDailyFood")
    PresetDailyFood toPresetDailyFoodEntity(ExcelPresetDailyFoodDto.ExcelData data, Food food, PresetGroupDailyFood groupDailyFood);

    default Integer checkFoodCapacity(List<FoodCapacity> foodCapacities, ExcelPresetDailyFoodDto.ExcelData data) {
        for(FoodCapacity foodCapacity : foodCapacities) {
            if(foodCapacity.getDiningType().getDiningType().equals(data.getDiningType()) &&
            foodCapacity.getCapacity().equals(data.getFoodCapacity())) {
                return foodCapacity.getCapacity();
            }
        }
        throw new ApiException(ExceptionEnum.NOT_FOUND_FOOD_CAPACITY);
    }

    default Integer checkMakersCapacity(List<MakersCapacity> makersCapacities, ExcelPresetDto data) {
        for(MakersCapacity makersCapacity : makersCapacities) {
            if(makersCapacity.getDiningType().equals(data.getDiningType()) &&
                    makersCapacity.getCapacity().equals(data.getMakersCapacity())) {
                return makersCapacity.getCapacity();
            }
        }
        throw new ApiException(ExceptionEnum.NOT_FOUND_MAKERS_CAPACITY);
    }




}
