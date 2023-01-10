package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.food.dto.FoodDetailDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Origin;
import co.dalicious.domain.food.repository.FoodRepository;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.food.repository.QOriginRepository;
import co.dalicious.domain.food.util.OriginList;
import co.kurrant.app.public_api.dto.food.DailyFoodDto;
import co.kurrant.app.public_api.service.FoodService;
import co.kurrant.app.public_api.mapper.order.DailyFoodMapper;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;
    private final QDailyFoodRepository qDailyFoodRepository;
    private final QOriginRepository qOriginRepository;

    private final DailyFoodMapper dailyFoodMapper;


    @Override
    @Transactional
    public Object getDailyFood(Integer spotId, LocalDate selectedDate) {
        //결과값을 담아줄 LIST 생성
        List<DailyFoodDto> resultList = new ArrayList<>();
        //조건에 맞는 DailyFood 조회
        List<DailyFood> dailyFoodList =  qDailyFoodRepository.getDailyFood(BigInteger.valueOf(spotId), selectedDate);
        //값이 있다면 결과값으로 담아준다.
        if (!dailyFoodList.isEmpty()) {
            for (DailyFood dailyFood : dailyFoodList) {

                DailyFoodDto dailyFoodDto = dailyFoodMapper.toDailyFoodDto(dailyFood);

                resultList.add(dailyFoodDto);
            }
        }
        return resultList;  //결과값 반환
    }

    @Override
    @Transactional
    public FoodDetailDto getFoodDetail(BigInteger foodId) {

        Food food = foodRepository.findOneById(foodId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));

        List<Origin> origin = qOriginRepository.findAllByFoodId(foodId);

        List<OriginList> originList = new ArrayList<>();
        for (Origin origin1 : origin){
            OriginList originList2 =  OriginList.builder()
                    .origin(origin1).build();
            originList.add(originList2);
        }

        return FoodDetailDto.builder()
                .food(food)
                .origin(originList)
                .build();
    }
}