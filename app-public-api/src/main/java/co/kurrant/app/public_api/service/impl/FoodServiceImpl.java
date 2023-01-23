package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.client.repository.SpotRepository;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.dto.FoodDetailDto;
import co.dalicious.domain.food.dto.RetrieveDailyFoodDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.repository.DailyFoodRepository;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.food.dto.DailyFoodDto;
import co.dalicious.domain.food.mapper.FoodMapper;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.system.util.enums.DiningType;
import co.kurrant.app.public_api.mapper.order.DailyFoodMapper;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserUtil;
import co.kurrant.app.public_api.service.FoodService;
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

    private final UserUtil userUtil;
    private final QDailyFoodRepository qDailyFoodRepository;
    private final DailyFoodMapper dailyFoodMapper;
    private final FoodMapper foodMapper;
    private final SpotRepository spotRepository;
    private final DailyFoodRepository dailyFoodRepository;


    @Override
    @Transactional
    public RetrieveDailyFoodDto getDailyFood(SecurityUser securityUser, BigInteger spotId, LocalDate selectedDate) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);
        // 스팟 정보 가져오기
        Spot spot = spotRepository.findById(spotId).orElseThrow(
                () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
        );
        // 유저가 그 그룹의 스팟에 포함되는지 확인.
        List<UserGroup> userGroups = user.getGroups();
        userGroups.stream().filter(v -> v.getGroup().equals(spot.getGroup()) && v.getClientStatus().equals(ClientStatus.BELONG))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.UNAUTHORIZED));
        // 유저가 당일날에 해당하는 식사타입이 몇 개인지 확인
        List<Integer> diningTypes = new ArrayList<>();
        for (DiningType diningType : spot.getDiningTypes()) {
            diningTypes.add(diningType.getCode());
        }
        // 결과값을 담아줄 LIST 생성
        List<DailyFoodDto> dailyFoodDtos = new ArrayList<>();
        // 조건에 맞는 DailyFood 조회
        List<DailyFood> dailyFoodList = qDailyFoodRepository.getSellingAndSoldOutDailyFood(spotId, selectedDate);
        // 값이 있다면 결과값으로 담아준다.
        for (DailyFood dailyFood : dailyFoodList) {
            DiscountDto discountDto = DiscountDto.getDiscount(dailyFood.getFood());
            DailyFoodDto dailyFoodDto = dailyFoodMapper.toDto(dailyFood, discountDto);
            dailyFoodDtos.add(dailyFoodDto);
        }
        return RetrieveDailyFoodDto.builder()
                .diningTypes(diningTypes)
                .dailyFoodDtos(dailyFoodDtos)
                .build();
    }

    @Override
    @Transactional
    public FoodDetailDto getFoodDetail(BigInteger dailyFoodId, SecurityUser securityUser) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        DailyFood dailyFood = dailyFoodRepository.findById(dailyFoodId).orElseThrow(
                () -> new ApiException(ExceptionEnum.DAILY_FOOD_NOT_FOUND)
        );
        Food food = dailyFood.getFood();
        DiscountDto discountDto = DiscountDto.getDiscount(food);

        return foodMapper.toDto(food, discountDto);
    }
}