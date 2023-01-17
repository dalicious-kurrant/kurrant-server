package co.kurrant.app.public_api.mapper.order;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.DailyFood;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.FoodDiscountPolicy;
import co.dalicious.domain.order.service.DiscountPolicyImpl;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.enums.DiscountType;
import co.dalicious.system.util.enums.FoodStatus;
import co.dalicious.domain.food.dto.DailyFoodDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface DailyFoodMapper extends GenericMapper<DailyFoodDto, DailyFood> {
      @Mapping(source = "diningType.diningType", target = "diningType")
      @Mapping(source = "food.id", target = "foodId")
      @Mapping(source = "food.name", target = "foodName")
      @Mapping(source = "foodStatus", target = "isSoldOut", qualifiedByName = "isSoldOut")
      @Mapping(source = "spot.id", target = "spotId")
      @Mapping(source = "serviceDate", target = "serviceDate", qualifiedByName = "serviceDateToString")
      @Mapping(source = "food.makers.name", target = "makersName")
      @Mapping(source = "food.spicy.spicy", target = "spicy")
      @Mapping(source = "food.image.location", target = "image")
      @Mapping(source = "food.description", target = "description")
      @Mapping(source = "food.price", target = "price")
      @Mapping(source = "food", target = "membershipDiscountedPrice", qualifiedByName = "discount.membershipDiscountedPrice")
      @Mapping(source = "food", target = "membershipDiscountedRate", qualifiedByName = "discount.membershipDiscountedRate")
      @Mapping(source = "food", target = "makersDiscountedPrice", qualifiedByName = "discount.makersDiscountedPrice")
      @Mapping(source = "food", target = "makersDiscountedRate", qualifiedByName = "discount.makersDiscountedRate")
      @Mapping(source = "food", target = "periodDiscountedPrice", qualifiedByName = "discount.periodDiscountedPrice")
      @Mapping(source = "food", target = "periodDiscountedRate", qualifiedByName = "discount.periodDiscountedRate")
      DailyFoodDto toDto(DailyFood dailyFood);

      @Named("isSoldOut")
      default Boolean isSoldOut(FoodStatus foodStatus) {
            return foodStatus.equals(FoodStatus.SOLD_OUT);
      }

      @Named("serviceDateToString")
      default String serviceDateToString(LocalDate serviceDate) {
            return DateUtils.format(serviceDate, "yyyy-MM-dd");
      }

      @Named("getMembershipDiscountedRate")
      default Integer getMembershipDiscountedRate(List<FoodDiscountPolicy> foodDiscountPolicyList) {
            Optional<FoodDiscountPolicy> foodDiscountPolicyOptional = foodDiscountPolicyList.stream()
                    .filter(v -> v.getDiscountType().equals(DiscountType.MEMBERSHIP_DISCOUNT))
                    .findAny();
            return foodDiscountPolicyOptional.map(FoodDiscountPolicy::getDiscountRate).orElse(null);
      }

      @Named("discount")
      default DiscountDto getDiscount(Food food) {
            DiscountDto discountDto = new DiscountDto();
            // 기본 가격
            BigDecimal price = food.getPrice();
            discountDto.setPrice(price);
            // 할인 비율
            Integer membershipDiscountedRate = 0;
            Integer makersDiscountedRate = 0;
            Integer periodDiscountedRate = 0;
            // 할인 가격
            BigDecimal membershipDiscountedPrice = null;
            BigDecimal makersDiscountedPrice = null;
            BigDecimal periodDiscountedPrice = null;

            for(FoodDiscountPolicy foodDiscountPolicy : food.getFoodDiscountPolicyList()) {
                  switch (foodDiscountPolicy.getDiscountType()) {
                        case MEMBERSHIP_DISCOUNT -> membershipDiscountedRate = foodDiscountPolicy.getDiscountRate();
                        case MAKERS_DISCOUNT -> makersDiscountedRate = foodDiscountPolicy.getDiscountRate();
                        case PERIOD_DISCOUNT -> periodDiscountedRate = foodDiscountPolicy.getDiscountRate();
                  }
            }

            // 1. 멤버십 할인
            if(membershipDiscountedRate != 0) {
                  membershipDiscountedPrice = DiscountPolicyImpl.discountedTotalPrice(price, membershipDiscountedRate);
                  price = membershipDiscountedPrice;
            }
            // 2. 메이커스 할인
            if(makersDiscountedRate != 0) {
                  makersDiscountedPrice = DiscountPolicyImpl.discountedTotalPrice(price, makersDiscountedRate);
                  price = makersDiscountedPrice;
            }
            // 3. 기간 할인
            if(periodDiscountedRate != 0) {
                  periodDiscountedPrice = DiscountPolicyImpl.discountedTotalPrice(price, periodDiscountedRate);
                  price = membershipDiscountedPrice;
            }
            discountDto.setMembershipDiscountedRate(membershipDiscountedRate);
            discountDto.setMembershipDiscountedPrice(membershipDiscountedPrice);
            discountDto.setMakersDiscountedRate(makersDiscountedRate);
            discountDto.setMakersDiscountedPrice(makersDiscountedPrice);
            discountDto.setPeriodDiscountedRate(periodDiscountedRate);
            discountDto.setPeriodDiscountedPrice(periodDiscountedPrice);

            return discountDto;
      }
}
