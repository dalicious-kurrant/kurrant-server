package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.food.dto.FoodDetailDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Origin;
import co.dalicious.domain.food.repository.FoodRepository;
import co.dalicious.domain.food.repository.OriginRepository;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.food.repository.QOriginRepository;
import co.dalicious.domain.food.util.OriginList;
import co.dalicious.domain.makers.entity.Makers;
import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.FoodStatus;
import co.kurrant.app.public_api.dto.food.DailyFoodDto;
import co.kurrant.app.public_api.service.FoodService;
import co.kurrant.app.public_api.service.impl.mapper.DailyFoodMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;
    private final QDailyFoodRepository qDailyFoodRepository;

    private final OriginRepository originRepository;

    private final QOriginRepository qOriginRepository;


    @Override
    public List<DailyFoodDto> getDailyFood(Integer spotId, LocalDate selectedDate) {
        //결과값을 담아줄 LIST 생성
        List<DailyFoodDto> resultList = new ArrayList<>();
        //조건에 맞는 DailyFood 조회
        List<DailyFood> dailyFood =  qDailyFoodRepository.getDailyFood(spotId, selectedDate);
        //확인
        System.out.println(" dailyFood 사이즈 확인 : " + dailyFood.size());
        //값이 있다면 결과값으로 담아준다.
        if (!dailyFood.isEmpty()){
            for (DailyFood food : dailyFood) {
                resultList.add(DailyFoodMapper.INSTANCE.toDto(food));
            }
        } else {
//            Food foodTemp = new Food(0,"없음",100,"",1,"없음");
            Food foodTemp = new Food();
            Makers makers = new Makers();
            makers.builder()
                    .id(1)
                    .name("무야호")
                    .build();

            foodTemp.builder()
                    .id(1)
                    .name("없음")
                    .price(100)
                    .makers(makers)
                    .description("없음")
                    .build();

            //값이 NULL일 경우, NPE 방지를 위한 샘플처리
            DiningType diningType = DiningType.valueOf("MORNING");
            FoodStatus foodStatus = FoodStatus.ofCode(0);
            DailyFoodDto dailySample = new DailyFoodDto(0,LocalDate.from(selectedDate.atStartOfDay()), diningType, foodTemp ,false,0,foodStatus,LocalDate.from(selectedDate.atStartOfDay()),LocalDate.from(selectedDate.atStartOfDay()));
            resultList.add(dailySample);
        }
        return resultList;  //결과값 반환
    }

    @Override
    public FoodDetailDto getFoodDetail(Integer foodId) {

        Food food = foodRepository.findById(foodId);

        List<Origin> origin = qOriginRepository.findByFoodId(foodId);

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