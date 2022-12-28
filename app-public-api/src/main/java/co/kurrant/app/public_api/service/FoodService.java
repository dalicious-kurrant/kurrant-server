package co.kurrant.app.public_api.service;

import co.dalicious.domain.food.dto.FoodDetailDto;
import co.kurrant.app.public_api.dto.food.DailyFoodDto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface FoodService {

    List<DailyFoodDto> getDailyFood(Integer spotId, LocalDate selectedDate);

    FoodDetailDto getFoodDetail(Integer foodId);
}
