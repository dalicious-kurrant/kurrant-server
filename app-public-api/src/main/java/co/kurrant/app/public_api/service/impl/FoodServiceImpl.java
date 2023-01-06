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
import co.kurrant.app.public_api.mapper.DailyFoodMapper;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;
    private final QDailyFoodRepository qDailyFoodRepository;
    private final QOriginRepository qOriginRepository;

    private final DailyFoodMapper dailyFoodMapper;


    @Override
    public Object getDailyFood(Integer spotId, LocalDate selectedDate) {
        //결과값을 담아줄 LIST 생성
        List<DailyFoodDto> resultList = new ArrayList<>();
        //조건에 맞는 DailyFood 조회
        List<DailyFood> dailyFoodList =  qDailyFoodRepository.getDailyFood(BigInteger.valueOf(spotId), selectedDate);
        //값이 있다면 결과값으로 담아준다.
        if (!dailyFoodList.isEmpty()) {
            for (DailyFood dailyFood : dailyFoodList) {

                DailyFoodDto dailyFoodDto = dailyFoodMapper.toDailyFoodDto(dailyFood);


                //Optional<Food> food = foodRepository.findOneById(dailyFood.getFood().getId().intValue());

                /*
                Food foodId = foodRepository.findById(food.getFood().getId()).orElseThrow(
                        () -> new ApiException(ExceptionEnum.NOT_FOUND)
                );
                DailyFoodDto dailyFoodDto = DailyFoodDto.builder()
                                            .id(food.getId())
                                            .created(food.getCreated())
                                            .diningType(food.getDiningType())
                                            .food(food.getFood())
                                            .makers(foodId.get().getMakers())
                                            .isSoldOut(food.getIsSoldOut())
                                            .spotId(food.getSpotId())
                                            .status(food.getStatus())
                                            .serviceDate(food.getServiceDate())
                                            .updated(food.getUpdated())
                                            .build();*/
               // DailyFoodDto dailyFoodDto = dailyFoodMapper.toDtoByFood(dailyFood,food.get());

                resultList.add(dailyFoodDto);
            }
        }
        return resultList;  //결과값 반환
    }

    @Override
    public FoodDetailDto getFoodDetail(BigInteger foodId) {

        Food food = foodRepository.findOneById(foodId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));

        List<Origin> origin = qOriginRepository.findByFoodId(BigInteger.valueOf(foodId));

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