package co.dalicious.domain.food.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.dto.FoodDto;
import co.dalicious.domain.food.entity.*;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.enums.FoodTag;
import co.dalicious.domain.food.util.FoodUtil;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.enums.DiscountType;
import co.dalicious.domain.food.entity.enums.DailyFoodStatus;
import co.dalicious.domain.food.dto.DailyFoodDto;
import exception.ApiException;
import exception.ExceptionEnum;
import org.hibernate.Hibernate;
import org.mapstruct.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", imports = FoodUtil.class)
public interface DailyFoodMapper {
      @Mapping(source = "presetGroupDailyFood.presetMakersDailyFood.diningType", target = "diningType")
      @Mapping(target = "dailyFoodStatus", constant = "SALES")
      @Mapping(source = "presetGroupDailyFood.presetMakersDailyFood.serviceDate", target = "serviceDate")
      @Mapping(source = "food", target = "food")
      @Mapping(source = "presetGroupDailyFood.group", target = "group")
      DailyFood toDailyFood(PresetDailyFood presetDailyFood);

      default List<DailyFood> toDailyFoods(List<FoodDto.DailyFood> dailyFoodDtos, List<Group> groups, List<Food> foods) {
            List<DailyFood> dailyFoods = new ArrayList<>();
            for (FoodDto.DailyFood dailyFoodDto : dailyFoodDtos) {
                  dailyFoods.add(toDailyFood(groups, dailyFoodDto, foods));
            }
            return dailyFoods;
      }


      default DailyFood toDailyFood(List<Group> groups, FoodDto.DailyFood dailyFoodDto, List<Food> foods) {
            Food food = Food.getFood(foods, dailyFoodDto.getMakersName(), dailyFoodDto.getFoodName());
            if(food == null) {
                  throw new ApiException(ExceptionEnum.NOT_FOUND_FOOD);
            }
            Group group = Group.getGroup(groups, dailyFoodDto.getGroupName());
            // 그룹이 가지고 있지 않은 식사 일정을 추가할 경우
            if(!group.getDiningTypes().contains(DiningType.ofCode(dailyFoodDto.getDiningType()))) {
                  throw new ApiException(ExceptionEnum.GROUP_DOSE_NOT_HAVE_DINING_TYPE);
            }
            return DailyFood.builder()
                    .id(dailyFoodDto.getDailyFoodId() == null ? null : dailyFoodDto.getDailyFoodId())
                    .dailyFoodStatus(DailyFoodStatus.SALES)
                    .dailyFoodStatus(DailyFoodStatus.SALES)
                    .diningType(DiningType.ofCode(dailyFoodDto.getDiningType()))
                    .serviceDate(DateUtils.stringToDate(dailyFoodDto.getServiceDate()))
                    .food(food)
                    .group(group)
                    .build();
      };

      @Mapping(source = "dailyFood.diningType.code", target = "diningType")
      @Mapping(source = "dailyFood.food.id", target = "foodId")
      @Mapping(source = "dailyFood.food.name", target = "foodName")
      @Mapping(source = "dailyFood.dailyFoodStatus", target = "status", qualifiedByName = "getStatus")
      @Mapping(source = "spotId", target = "spotId")
      @Mapping(source = "dailyFood.serviceDate", target = "serviceDate", qualifiedByName = "serviceDateToString")
      @Mapping(source = "dailyFood", target = "makersName", qualifiedByName = "getMakersName")
      @Mapping(source = "dailyFood", target = "spicy", qualifiedByName = "getSpicy")
      @Mapping(target = "image", expression = "java(dailyFood.getFood().getImages() == null || dailyFood.getFood().getImages().isEmpty() ? null : dailyFood.getFood().getImages().get(0).getLocation())")
      @Mapping(source = "dailyFood.food.description", target = "description")
      @Mapping(source = "dailyFood.food.price", target = "price")
      @Mapping(target = "discountedPrice", expression = "java(discountDto.getDiscountedPrice())")
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