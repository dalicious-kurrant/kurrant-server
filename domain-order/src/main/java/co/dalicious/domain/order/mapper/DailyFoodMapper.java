package co.dalicious.domain.order.mapper;

import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.FoodDiscountPolicy;
import co.dalicious.system.enums.FoodTag;
import co.dalicious.domain.food.util.FoodUtil;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.enums.DiscountType;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.domain.food.dto.DailyFoodDto;
import org.hibernate.Hibernate;
import org.mapstruct.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", imports = FoodUtil.class)
public interface DailyFoodMapper {
      @Mapping(source = "dailyFood.diningType.code", target = "diningType")
      @Mapping(source = "dailyFood.food.id", target = "foodId")
      @Mapping(source = "dailyFood.food.name", target = "foodName")
      @Mapping(source = "dailyFood.dailyFoodStatus", target = "status", qualifiedByName = "getStatus")
      @Mapping(source = "spotId", target = "spotId")
      @Mapping(source = "dailyFood.serviceDate", target = "serviceDate", qualifiedByName = "serviceDateToString")
      @Mapping(source = "dailyFood", target = "makersName", qualifiedByName = "getMakersName")
      @Mapping(source = "dailyFood", target = "spicy", qualifiedByName = "getSpicy")
      @Mapping(source = "dailyFood.food.image.location", target = "image")
      @Mapping(source = "dailyFood.food.description", target = "description")
      @Mapping(source = "dailyFood.food.price", target = "price")
      @Mapping(target = "discountedPrice", expression = "java(FoodUtil.getFoodTotalDiscountedPrice(dailyFood.getFood(), discountDto))")
      @Mapping(source = "discountDto.membershipDiscountPrice", target = "membershipDiscountPrice")
      @Mapping(source = "discountDto.membershipDiscountRate", target = "membershipDiscountRate")
      @Mapping(source = "discountDto.makersDiscountPrice", target = "makersDiscountPrice")
      @Mapping(source = "discountDto.makersDiscountRate", target = "makersDiscountRate")
      @Mapping(source = "discountDto.periodDiscountPrice", target = "periodDiscountPrice")
      @Mapping(source = "discountDto.periodDiscountRate", target = "periodDiscountRate")
      DailyFoodDto toDto(BigInteger spotId, DailyFood dailyFood, DiscountDto discountDto);

      @Named("getStatus")
      default Integer getStatus(DailyFoodStatus dailyFoodStatus) {
            return dailyFoodStatus.getCode();
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

      @Named("getSpicy")
      default String getSpicy(DailyFood dailyFood) {
            List<FoodTag> foodTags = dailyFood.getFood().getFoodTags();
            Optional<FoodTag> foodTag = foodTags.stream().filter(v -> v.getCategory().equals("맵기")).findAny();
            return foodTag.map(FoodTag::getTag).orElse(null);
      }

      @Named("getMakersName")
      default String getMakersName(DailyFood dailyFood) {
            Makers makers = (Makers) Hibernate.unproxy(dailyFood.getFood().getMakers());
            return makers.getName();
      }
}
