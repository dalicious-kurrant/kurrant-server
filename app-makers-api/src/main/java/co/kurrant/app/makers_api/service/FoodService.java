package co.kurrant.app.makers_api.service;

import co.dalicious.domain.food.dto.FoodDeleteDto;
import co.dalicious.domain.food.dto.FoodListDto;
import co.dalicious.domain.food.dto.FoodManagingDto;
import co.kurrant.app.makers_api.model.SecurityUser;

import java.math.BigInteger;
import java.util.List;

public interface FoodService {
    List<FoodListDto> getAllFoodList();
    List<FoodListDto> getAllFoodListByMakers(SecurityUser securityUser);
    FoodManagingDto getFoodDetail(BigInteger foodId, SecurityUser securityUser);
    void deleteFood(FoodDeleteDto foodDeleteDto);
}
