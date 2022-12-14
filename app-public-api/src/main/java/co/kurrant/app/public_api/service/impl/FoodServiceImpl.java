package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.repository.FoodRepository;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.kurrant.app.public_api.dto.food.DailyFoodDto;
import co.kurrant.app.public_api.service.FoodService;
import co.kurrant.app.public_api.service.impl.mapper.DailyFoodMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;
    private final QDailyFoodRepository qDailyFoodRepository;

    @Override
    public List<DailyFoodDto> getDailyFood(Integer spotId, String selectedDate) {
        List<DailyFoodDto> resultList = new ArrayList<>();
        List<DailyFood> dailyFood =  qDailyFoodRepository.getDailyFood(spotId, selectedDate);
        System.out.println(dailyFood.size() + " dalilyFood 사이즈 확인");
        System.out.println(dailyFood.get(0).getDiningType() + " DiningType");
        if (!dailyFood.isEmpty()){
            resultList.add(DailyFoodMapper.INSTANCE.toDto(qDailyFoodRepository.getDailyFood(spotId, selectedDate).get(0)));
        } else {
            System.out.println("널..");
            return null;
        }
        return resultList;
    }
}
