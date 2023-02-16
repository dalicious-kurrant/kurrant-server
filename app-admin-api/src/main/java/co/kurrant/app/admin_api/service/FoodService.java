package co.kurrant.app.admin_api.service;

import co.dalicious.domain.food.dto.FoodDeleteDto;
import co.dalicious.domain.food.dto.FoodListDto;
import co.dalicious.domain.food.dto.MakersFoodDetailDto;
import co.dalicious.domain.food.dto.MakersFoodDetailReqDto;

import java.math.BigInteger;
import java.util.List;

public interface FoodService {
    List<FoodListDto> getAllFoodList();
    List<FoodListDto> getAllFoodListByMakers(BigInteger makersId);
    MakersFoodDetailDto getFoodDetail(BigInteger foodId, BigInteger makersId);
    void deleteFood(FoodDeleteDto foodDeleteDto);
    void updateFoodMass(List<FoodListDto> foodListDto);
    void updateFood(MakersFoodDetailReqDto foodDetailDto);
}
