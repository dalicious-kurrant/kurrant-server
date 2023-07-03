package co.kurrant.app.makers_api.service;

import co.dalicious.domain.food.dto.FoodStatusUpdateDto;
import co.dalicious.domain.food.dto.FoodListDto;
import co.dalicious.domain.food.dto.MakersFoodDetailDto;
import co.dalicious.domain.food.dto.MakersFoodDetailReqDto;
import co.kurrant.app.makers_api.model.SecurityUser;

import java.math.BigInteger;
import java.util.List;

public interface FoodService {
    List<FoodListDto.FoodList> getAllFoodListByMakers(SecurityUser securityUser);
    MakersFoodDetailDto getFoodDetail(BigInteger foodId, SecurityUser securityUser);
    void updateFoodStatus(FoodStatusUpdateDto foodStatusUpdateDto);
    void updateFood(MakersFoodDetailReqDto foodDetailDto);
}
