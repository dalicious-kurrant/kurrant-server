package co.kurrant.app.makers_api.service;

import co.kurrant.app.makers_api.dto.DailyFoodDto;
import co.kurrant.app.makers_api.model.SecurityUser;

import java.util.List;
import java.util.Map;

public interface DailyFoodService {
    List<DailyFoodDto> getDailyFood(SecurityUser securityUser, Map<String, Object> parameter);
}
