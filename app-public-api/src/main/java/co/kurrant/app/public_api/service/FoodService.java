package co.kurrant.app.public_api.service;

import co.dalicious.domain.food.dto.DailyFoodDto;
import co.dalicious.domain.food.dto.FoodDetailDto;
import co.dalicious.domain.food.dto.RetrieveDailyFoodDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public interface FoodService {

    RetrieveDailyFoodDto getDailyFood(SecurityUser securityUser, BigInteger spotId, LocalDate selectedDate);

    FoodDetailDto getFoodDetail(BigInteger foodId, SecurityUser securityUser);
}
