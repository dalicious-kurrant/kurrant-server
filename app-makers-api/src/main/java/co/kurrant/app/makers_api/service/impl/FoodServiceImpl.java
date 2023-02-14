package co.kurrant.app.makers_api.service.impl;

import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.dto.FoodDeleteDto;
import co.dalicious.domain.food.dto.FoodListDto;
import co.dalicious.domain.food.dto.FoodManagingDto;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.mapper.MakersFoodMapper;
import co.dalicious.domain.food.repository.FoodRepository;
import co.dalicious.domain.food.util.FoodUtil;
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
            DiscountDto discountDto = DiscountDto.getDiscountDtoWithoutMembershipDiscount(food);
            BigDecimal resultPrice = FoodUtil.getFoodTotalDiscountedPriceWithoutMembershipDiscount(food, discountDto);
            FoodListDto dto = makersFoodMapper.toAllFoodListDto(food, discountDto, resultPrice);
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
            DiscountDto discountDto = DiscountDto.getDiscountDtoWithoutMembershipDiscount(food);
            BigDecimal resultPrice = FoodUtil.getFoodTotalDiscountedPriceWithoutMembershipDiscount(food, discountDto);
            FoodListDto dto = makersFoodMapper.toAllFoodListByMakersDto(food, discountDto, resultPrice);
            dtoList.add(dto);
        }

        return dtoList;
    }

    @Override
    @Transactional
    public FoodManagingDto getFoodDetail(BigInteger foodId, SecurityUser securityUser) {
        Makers makers = userUtil.getMakers(securityUser);
        Food food = foodRepository.findByIdAndMakers(foodId, makers);

        if(food == null) { throw new ApiException(ExceptionEnum.NOT_FOND_FOOD); }

        DiscountDto discountDto = DiscountDto.getDiscountDtoWithoutMembershipDiscount(food);

        return makersFoodMapper.toFoodManagingDto(food, discountDto);
    }

    @Override
    public void deleteFood(FoodDeleteDto foodDeleteDto) {
        for(BigInteger foodId : foodDeleteDto.getFoodId()){
            Food food = foodRepository.findById(foodId).orElseThrow(
                    () -> new ApiException(ExceptionEnum.NOT_FOND_FOOD)
            );

            foodRepository.delete(food);
        }
    }
}
