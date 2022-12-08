package co.kurrant.app.public_api.service;

import co.kurrant.app.public_api.dto.food.DailyFoodDto;

import java.util.Date;

public interface FoodService {

    DailyFoodDto getDailyFood(Integer spotId, Date selectedDate);
}
