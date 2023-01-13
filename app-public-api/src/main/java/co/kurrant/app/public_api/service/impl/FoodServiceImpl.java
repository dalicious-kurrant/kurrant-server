package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Origin;
import co.dalicious.domain.food.repository.FoodRepository;
import co.dalicious.domain.food.repository.QDailyFoodRepository;
import co.dalicious.domain.food.repository.QOriginRepository;
import co.dalicious.domain.food.util.OriginList;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.food.DailyFoodDto;
import co.kurrant.app.public_api.mapper.food.FoodMapper;
import co.kurrant.app.public_api.mapper.order.DailyFoodMapper;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserUtil;
import co.kurrant.app.public_api.service.FoodService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final UserUtil userUtil;
    private final FoodRepository foodRepository;
    private final QDailyFoodRepository qDailyFoodRepository;
    private final QOriginRepository qOriginRepository;
    private final DailyFoodMapper dailyFoodMapper;
    private final FoodMapper foodMapper;



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
    public Object getFoodDetail(BigInteger foodId, SecurityUser securityUser) {

        User user = userUtil.getUser(securityUser);

        Food food = foodRepository.findOneById(foodId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));

        List<Origin> origin = qOriginRepository.findAllByFoodId(foodId);

        List<OriginList> originList = new ArrayList<>();
        for (Origin origin1 : origin){
            OriginList originList2 =  OriginList.builder()
                    .origin(origin1).build();
            originList.add(originList2);
        }

        //할인금액
        //멤버십 할인 가격
        BigDecimal membershipPrice;
        Integer price = food.getPrice();
        Integer countPrice = food.getPrice();
        if (user.getIsMembership()) {
            membershipPrice = BigDecimal.valueOf(price - (price * 80L / 100));
            price = price - membershipPrice.intValue();
        }
        //판매자 할인 가격
        BigDecimal discountPrice = BigDecimal.valueOf(price - (price * 85L / 100));
        //개발 단계에서는 기본할인 + 기간할인 무조건 적용해서 진행
        price = price - discountPrice.intValue();
        //기간 할인 가격
        BigDecimal periodDiscountPrice = BigDecimal.valueOf((price - price * 90L / 100));
        price = price - periodDiscountPrice.intValue();

        //할인율 구하기
        BigDecimal discountRate = BigDecimal.valueOf(( countPrice - (double) Math.abs(price)) / countPrice);

        //결과값을 담아줄 List 생성
        return foodMapper.toFoodDetailDto(food, originList, price, discountRate);
    }
}