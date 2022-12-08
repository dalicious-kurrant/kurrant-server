package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.food.repository.FoodRepository;
import co.kurrant.app.public_api.dto.food.DailyFoodDto;
import co.kurrant.app.public_api.service.FoodService;
import co.kurrant.app.public_api.service.impl.mapper.DailyFoodMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;

    @Override
    public DailyFoodDto getDailyFood(Integer spotId, Date selectedDate) {
        //작업중
        return DailyFoodMapper.INSTANCE.toDto(foodRepository.getDailyFood(spotId, selectedDate));
    }
}
