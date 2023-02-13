package co.kurrant.app.makers_api.service;

import co.dalicious.domain.food.dto.FoodListDto;
import co.kurrant.app.makers_api.model.SecurityUser;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface FoodService {
    List<FoodListDto> getAllFoodList();
    List<FoodListDto> getAllFoodListByMakers(SecurityUser securityUser);
}
