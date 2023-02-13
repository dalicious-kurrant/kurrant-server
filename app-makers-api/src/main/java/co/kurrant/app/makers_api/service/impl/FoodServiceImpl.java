package co.kurrant.app.makers_api.service.impl;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.FoodDiscountPolicy;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.mapper.MakersFoodMapper;
import co.dalicious.domain.food.repository.FoodRepository;
import co.dalicious.system.util.enums.DiscountType;
import co.dalicious.domain.food.dto.FoodListDto;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.FoodService;
import co.kurrant.app.makers_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;
    private final MakersFoodMapper makersFoodMapper;
    private final UserUtil userUtil;

    @Override
    @Transactional
    public List<FoodListDto> getAllFoodList() {
        // 모든 상품 불러오기
        List<Food> allFoodList = foodRepository.findAll();
        if(allFoodList.size() == 0) { throw new ApiException(ExceptionEnum.NOT_FOUND); }

        // 상품 dto에 담기
        List<FoodListDto> dtoList = new ArrayList<>();

        for(Food food : allFoodList) {
            BigDecimal resultPrice = calculate(food);
            FoodListDto dto = makersFoodMapper.toAllFoodListDto(food, resultPrice);
            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    @Transactional
    public List<FoodListDto> getAllFoodListByMakers(SecurityUser securityUser) {
        Makers makers = userUtil.getMakers(securityUser);

        // makersId로 상품 조회
        List<Food> foodListByMakers = foodRepository.findByMakersOrderById(makers);
        if(foodListByMakers == null) { throw new ApiException(ExceptionEnum.NOT_FOUND); }

        // 상품 dto에 담기
        List<FoodListDto> dtoList = new ArrayList<>();
        for(Food food : foodListByMakers) {
            BigDecimal resultPrice = calculate(food);
            FoodListDto dto = makersFoodMapper.toAllFoodListDto(food, resultPrice);
            dtoList.add(dto);
        }

        return dtoList;
    }

    // 할인된 금액 계산
    @Transactional
    BigDecimal calculate(Food food) {
        // 할인률 계산
        BigDecimal defaultPrice = BigDecimal.ZERO;
        BigDecimal makersDiscountPrice = BigDecimal.ZERO;
        BigDecimal eventDiscountPrice = BigDecimal.ZERO;
        BigDecimal resultPrice = BigDecimal.ZERO;

        defaultPrice = defaultPrice.add(food.getPrice());

        List<FoodDiscountPolicy> discountPolicyList = food.getFoodDiscountPolicyList();
        // makersDiscount
        FoodDiscountPolicy makersDiscount = discountPolicyList.stream()
                .filter(policy -> policy.getDiscountType().equals(DiscountType.MAKERS_DISCOUNT))
                .findFirst().orElse(null);
        Integer makersRate = null;
        if(makersDiscount != null) {
            makersRate = makersDiscount.getDiscountRate();
            makersDiscountPrice = makersDiscountPrice.add(defaultPrice).multiply(BigDecimal.valueOf(makersRate * 0.01));
        }
        //eventDiscount
        FoodDiscountPolicy eventDiscount = discountPolicyList.stream()
                .filter(policy -> policy.getDiscountType().equals(DiscountType.PERIOD_DISCOUNT))
                .findFirst().orElse(null);
        Integer eventRate = null;
        if(eventDiscount != null) {
            eventRate = eventDiscount.getDiscountRate();
            eventDiscountPrice = eventDiscountPrice.add(defaultPrice).multiply(BigDecimal.valueOf(eventRate * 0.01));
        }

        resultPrice = resultPrice.add(defaultPrice).subtract(makersDiscountPrice).subtract(eventDiscountPrice);

        return resultPrice;
    }
}
