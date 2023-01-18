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
public interface DailyFoodMapper {
      @Mapping(source = "dailyFood.diningType.diningType", target = "diningType")
      @Mapping(source = "dailyFood.food.id", target = "foodId")
      @Mapping(source = "dailyFood.food.name", target = "foodName")
      @Mapping(source = "dailyFood.foodStatus", target = "isSoldOut", qualifiedByName = "isSoldOut")
      @Mapping(source = "dailyFood.spot.id", target = "spotId")
      @Mapping(source = "dailyFood.serviceDate", target = "serviceDate", qualifiedByName = "serviceDateToString")
      @Mapping(source = "dailyFood.food.makers.name", target = "makersName")
      @Mapping(source = "dailyFood.food.spicy.spicy", target = "spicy")
      @Mapping(source = "dailyFood.food.image.location", target = "image")
      @Mapping(source = "dailyFood.food.description", target = "description")
      @Mapping(source = "dailyFood.food.price", target = "price")
      @Mapping(source = "discountDto.membershipDiscountPrice", target = "membershipDiscountPrice")
      @Mapping(source = "discountDto.membershipDiscountRate", target = "membershipDiscountRate")
      @Mapping(source = "discountDto.makersDiscountPrice", target = "makersDiscountPrice")
      @Mapping(source = "discountDto.makersDiscountRate", target = "makersDiscountRate")
      @Mapping(source = "discountDto.periodDiscountPrice", target = "periodDiscountPrice")
      @Mapping(source = "discountDto.periodDiscountRate", target = "periodDiscountRate")
      DailyFoodDto toDto(DailyFood dailyFood, DiscountDto discountDto);

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
}
