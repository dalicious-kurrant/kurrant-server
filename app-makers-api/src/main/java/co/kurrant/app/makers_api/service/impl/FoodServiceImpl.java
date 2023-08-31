package co.kurrant.app.makers_api.service.impl;

import co.dalicious.domain.food.dto.*;
import co.dalicious.domain.food.entity.*;
import co.dalicious.domain.food.mapper.CapacityMapper;
import co.dalicious.domain.food.mapper.FoodDiscountPolicyMapper;
import co.dalicious.domain.food.mapper.MakersFoodMapper;
import co.dalicious.domain.food.repository.*;
import co.dalicious.domain.order.mapper.FoodMapper;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.enums.DiscountType;
import co.dalicious.system.enums.FoodTag;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.FoodService;
import co.kurrant.app.makers_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;
    private final MakersFoodMapper makersFoodMapper;
    private final UserUtil userUtil;
    private final FoodMapper foodMapper;
    private final FoodDiscountPolicyRepository foodDiscountPolicyRepository;
    private final QFoodRepository qFoodRepository;

    @Override
    @Transactional
    public List<FoodListDto.FoodList> getAllFoodListByMakers(SecurityUser securityUser, Integer status) {
        Makers makers = userUtil.getMakers(securityUser);

        // makersId로 상품 조회
        List<Food> foodListByMakers = qFoodRepository.findByMakersIdAndStatus(makers, status);
        if (foodListByMakers.isEmpty()) { return null; }

        // 상품 dto에 담기
        List<FoodListDto.FoodList> dtoList = new ArrayList<>();

        for (Food food : foodListByMakers) {
            DiscountDto discountDto = DiscountDto.getDiscount(food);
            BigDecimal resultPrice = discountDto.getDiscountedPrice();
            FoodListDto.FoodList dto = makersFoodMapper.toAllFoodListByMakersDto(food, discountDto, resultPrice);
            dtoList.add(dto);
        }
        dtoList = dtoList.stream().sorted(Comparator.comparing(FoodListDto.FoodList::getFoodStatus).thenComparing(FoodListDto.FoodList::getFoodId)).toList();
        return dtoList;
    }

    @Override
    @Transactional
    public MakersFoodDetailDto getFoodDetail(BigInteger foodId, SecurityUser securityUser) {
        // maker와 food를 찾고
        Makers makers = userUtil.getMakers(securityUser);
        Food food = qFoodRepository.findByIdAndMakers(foodId, makers);
        // 만약 food가 없으면 예외처리
        if (food == null) throw new ApiException(ExceptionEnum.NOT_FOUND_FOOD);

        DiscountDto discountDto = DiscountDto.getDiscount(food);

        return makersFoodMapper.toFoodManagingDto(food, discountDto);
    }

    @Override
    @Transactional
    public void updateFoodStatus(FoodStatusUpdateDto foodStatusUpdateDto) {
    }

    @Override
    @Transactional
    public void updateFood(MakersFoodDetailReqDto foodDetailDto) {
        Food food = foodRepository.findById(foodDetailDto.getFoodId()).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_FOOD)
        );

        // 음식 업데이트
        food.updateFood(foodDetailDto);

        //음식 할인 정책 저장
        if (food.getFoodDiscountPolicy(DiscountType.MAKERS_DISCOUNT) == null) {
            foodDiscountPolicyRepository.save(foodMapper.toFoodDiscountPolicy(food, DiscountType.MAKERS_DISCOUNT, foodDetailDto.getMakersDiscountRate()));
        }
        else if (foodDetailDto.getMakersDiscountRate() == 0) {
            foodDiscountPolicyRepository.delete(food.getFoodDiscountPolicy(DiscountType.MAKERS_DISCOUNT));
        }
        else {
            food.getFoodDiscountPolicy(DiscountType.MAKERS_DISCOUNT).updateFoodDiscountPolicy(foodDetailDto.getMakersDiscountRate());
        }

        if (food.getFoodDiscountPolicy(DiscountType.PERIOD_DISCOUNT) == null) {
            foodDiscountPolicyRepository.save(foodMapper.toFoodDiscountPolicy(food, DiscountType.PERIOD_DISCOUNT, foodDetailDto.getMakersDiscountRate()));
        }
        else if (foodDetailDto.getMakersDiscountRate() == 0) {
            foodDiscountPolicyRepository.delete(food.getFoodDiscountPolicy(DiscountType.PERIOD_DISCOUNT));
        }
        else {
            food.getFoodDiscountPolicy(DiscountType.PERIOD_DISCOUNT).updateFoodDiscountPolicy(foodDetailDto.getMakersDiscountRate());
        }

    }
}
