package co.kurrant.app.makers_api.service.impl;

import co.dalicious.domain.food.dto.*;
import co.dalicious.domain.food.entity.*;
import co.dalicious.domain.food.mapper.CapacityMapper;
import co.dalicious.domain.food.mapper.FoodDiscountPolicyMapper;
import co.dalicious.domain.food.mapper.MakersFoodMapper;
import co.dalicious.domain.food.repository.*;
import co.dalicious.domain.food.util.FoodUtil;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;
    private final MakersFoodMapper makersFoodMapper;
    private final UserUtil userUtil;
    private final FoodMapper foodMapper;
    private final MakersRepository makersRepository;
    private final FoodDiscountPolicyMapper foodDiscountPolicyMapper;
    private final FoodDiscountPolicyRepository foodDiscountPolicyRepository;
    private final CapacityMapper capacityMapper;
    private final FoodCapacityRepository foodCapacityRepository;
    private final QFoodRepository qFoodRepository;

    @Override
    @Transactional
    public List<FoodListDto> getAllFoodList() {
        // TODO:페이지네이션
        // 모든 상품 불러오기
        List<Food> allFoodList = foodRepository.findAll();
        if (allFoodList.size() == 0) {
            throw new ApiException(ExceptionEnum.NOT_FOUND);
        }

        // 상품 dto에 담기
        List<FoodListDto> dtoList = new ArrayList<>();

        for (Food food : allFoodList) {
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
        if (foodListByMakers == null) {
            throw new ApiException(ExceptionEnum.NOT_FOUND);
        }

        // 상품 dto에 담기
        List<FoodListDto> dtoList = new ArrayList<>();

        for (Food food : foodListByMakers) {
            DiscountDto discountDto = DiscountDto.getDiscountDtoWithoutMembershipDiscount(food);
            BigDecimal resultPrice = FoodUtil.getFoodTotalDiscountedPriceWithoutMembershipDiscount(food, discountDto);
            FoodListDto dto = makersFoodMapper.toAllFoodListByMakersDto(food, discountDto, resultPrice);
            dtoList.add(dto);
        }

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

        DiscountDto discountDto = DiscountDto.getDiscountDtoWithoutMembershipDiscount(food);

        return makersFoodMapper.toFoodManagingDto(food, discountDto);
    }

    @Override
    @Transactional
    public void updateFoodStatus(FoodStatusUpdateDto foodStatusUpdateDto) {
    }

    //대량 수정
    @Override
    @Transactional
    public void updateFoodMass(List<FoodListDto> foodListDtoList) {
        for (FoodListDto foodListDto : foodListDtoList) {
            Food food = foodRepository.findById(foodListDto.getFoodId()).orElse(null);

            List<FoodTag> foodTags = new ArrayList<>();
            List<String> foodTagStrs = foodListDto.getFoodTags();
            if(foodTagStrs == null) foodTags = null;
            else { for (String tag : foodTagStrs) foodTags.add(FoodTag.ofString(tag)); }
            Makers makers = makersRepository.findById(foodListDto.getMakersId()).orElseThrow(
                    () -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS)
            );

            // 기존 푸드가 없으면 생성
            if(food == null) {
                BigDecimal customPrice = BigDecimal.ZERO;

                // 푸드 생성
                Food newFood = foodMapper.toNewEntity(foodListDto, makers, customPrice, foodTags);
                foodRepository.save(newFood);

                // 푸드 할인 정책 생성
                FoodDiscountPolicy makersDiscount = foodDiscountPolicyMapper.toEntity(DiscountType.MAKERS_DISCOUNT, foodListDto.getMakersDiscount(), newFood);
                FoodDiscountPolicy periodDiscount = foodDiscountPolicyMapper.toEntity(DiscountType.PERIOD_DISCOUNT, foodListDto.getEventDiscount(), newFood);

                foodDiscountPolicyRepository.save(makersDiscount);
                foodDiscountPolicyRepository.save(periodDiscount);

                // 푸드 capacity 생성
                List<MakersCapacity> makersCapacityList = makers.getMakersCapacities();
                if (makersCapacityList == null) {
                    throw new ApiException(ExceptionEnum.NOT_FOUND_MAKERS_CAPACITY);
                }
                for (MakersCapacity makersCapacity : makersCapacityList) {
                    DiningType diningType = makersCapacity.getDiningType();
                    Integer capacity = makersCapacity.getCapacity();

                    FoodCapacity foodCapacity = capacityMapper.toEntity(diningType, capacity, newFood);
                    foodCapacityRepository.save(foodCapacity);
                }
            }

            // food가 있으면
            else {
                //food UPDATE
                food.updateFoodMass(foodListDto, foodTags, makers);
                foodRepository.save(food);

                //food discount policy UPDATE
                List<FoodDiscountPolicy> discountPolicyList = food.getFoodDiscountPolicyList();
                for (FoodDiscountPolicy discountPolicy : discountPolicyList) {
                    if (discountPolicy.getDiscountType().equals(DiscountType.MAKERS_DISCOUNT)) {
                        discountPolicy.updateFoodDiscountPolicy(foodListDto.getMakersDiscount());
                        foodDiscountPolicyRepository.save(discountPolicy);
                    } else if (discountPolicy.getDiscountType().equals(DiscountType.PERIOD_DISCOUNT)) {
                        discountPolicy.updateFoodDiscountPolicy(foodListDto.getEventDiscount());
                        foodDiscountPolicyRepository.save(discountPolicy);
                    }
                }
            }
        }
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
