package co.kurrant.app.makers_api.mapper;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.FoodCapacity;
import co.dalicious.domain.food.entity.FoodSchedule;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.makers_api.dto.DailyFoodDto;
import org.mapstruct.Mapper;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface MakersDailyFoodMapper {

    default DailyFoodDto toDailyFoodDto(LocalDate serviceDate, List<DailyFoodDto.DailyFoodDining> dailyFoodDiningList) {
        DailyFoodDto dailyFoodDto = new DailyFoodDto();

        dailyFoodDiningList = dailyFoodDiningList.stream().sorted(Comparator.comparing(v -> DiningType.ofCode(v.getDiningType()))).collect(Collectors.toList());

        dailyFoodDto.setServiceDate(DateUtils.localDateToString(serviceDate));
        dailyFoodDto.setDailyFoodDiningList(dailyFoodDiningList);

        return dailyFoodDto;
    }

    default DailyFoodDto.DailyFoodDining toDailyFoodDining(DiningType diningType, Integer groupCapacity, List<DailyFoodDto.DailyFood> dailyFoodList) {
        DailyFoodDto.DailyFoodDining dailyFoodDining = new DailyFoodDto.DailyFoodDining();

        dailyFoodDining.setDiningType(diningType.getCode());
        dailyFoodDining.setGroupCapacity(groupCapacity);
        dailyFoodDining.setDailyFoodList(dailyFoodList);

        return dailyFoodDining;
    }

    default DailyFoodDto.DailyFood toDailyFood(Food food, FoodSchedule foodSchedule, DiningType diningType) {
        DailyFoodDto.DailyFood dtoDailyFood = new DailyFoodDto.DailyFood();

        dtoDailyFood.setFoodName(food.getName());

        if(foodSchedule == null) {
            List<FoodCapacity> foodCapacities = food.getFoodCapacities();

            FoodCapacity foodCapacity = foodCapacities.stream().filter(capa -> capa.getDiningType().equals(diningType)).findFirst().orElse(null);

            dtoDailyFood.setFoodCapacity(foodCapacity != null ? foodCapacity.getCapacity() : null);
        }
        else {
            dtoDailyFood.setFoodCapacity(foodSchedule.getCapacity());
        }

        return dtoDailyFood;
    }
}
