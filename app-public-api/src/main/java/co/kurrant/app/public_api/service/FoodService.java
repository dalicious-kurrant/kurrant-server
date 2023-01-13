package co.kurrant.app.public_api.service;

import co.dalicious.domain.food.dto.FoodDetailDto;
import co.kurrant.app.public_api.dto.food.DailyFoodDto;
import co.kurrant.app.public_api.model.SecurityUser;
import org.springframework.security.core.Authentication;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface FoodService {

    Object getDailyFood(Integer spotId, LocalDate selectedDate, SecurityUser securityUser);

    Object getFoodDetail(BigInteger foodId, SecurityUser securityUser);
}
