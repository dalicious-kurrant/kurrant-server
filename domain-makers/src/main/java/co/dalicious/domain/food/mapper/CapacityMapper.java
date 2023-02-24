package co.dalicious.domain.food.mapper;

import co.dalicious.domain.food.entity.*;
import co.dalicious.system.enums.DiningType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CapacityMapper {

    @Mapping(source = "diningType", target = "diningType")
    @Mapping(source = "capacity", target = "capacity")
    @Mapping(source = "food", target = "food")
    FoodCapacity toEntity(DiningType diningType, Integer capacity, Food food);

    default FoodSchedule toFoodSchedule(PresetDailyFood presetDailyFood) {
        MakersCapacity makersCapacity = presetDailyFood.getPresetGroupDailyFood().getPresetMakersDailyFood().getMakers().getMakersCapacity(presetDailyFood.getPresetGroupDailyFood().getPresetMakersDailyFood().getDiningType());
        FoodCapacity foodCapacity = presetDailyFood.getFood().getFoodCapacity(presetDailyFood.getPresetGroupDailyFood().getPresetMakersDailyFood().getDiningType());
        // makers에서 기본으로 설정한 메이커스의 기본 capacity라면 FoodSchedule 생성하지 않음
        if(presetDailyFood.getCapacity().equals(makersCapacity.getCapacity())) {
            return null;
        }
        // makers에서 기본으로 설정한 음식 기본 capacity라면 FoodSchedule 생성하지 않음
        if(presetDailyFood.getCapacity().equals(foodCapacity.getCapacity())) {
            return null;
        }
        return FoodSchedule.builder()
                .serviceDate(presetDailyFood.getPresetGroupDailyFood().getPresetMakersDailyFood().getServiceDate())
                .diningType(presetDailyFood.getPresetGroupDailyFood().getPresetMakersDailyFood().getDiningType())
                .capacity(presetDailyFood.getCapacity())
                .makers(presetDailyFood.getPresetGroupDailyFood().getPresetMakersDailyFood().getMakers())
                .food(presetDailyFood.getFood())
                .build();
    }

    default MakersSchedule toMakersSchedule(PresetMakersDailyFood presetMakersDailyFood) {
        MakersCapacity makersCapacity = presetMakersDailyFood.getMakers().getMakersCapacity(presetMakersDailyFood.getDiningType());
        // makers에서 기본으로 설정한 메이커스의 기본 capacity라면 FoodSchedule 생성하지 않음
        if(presetMakersDailyFood.getCapacity().equals(makersCapacity.getCapacity())) {
            return null;
        }
        return new MakersSchedule(presetMakersDailyFood.getServiceDate(), presetMakersDailyFood.getDiningType(), presetMakersDailyFood.getCapacity(), presetMakersDailyFood.getMakers());
    };
}
