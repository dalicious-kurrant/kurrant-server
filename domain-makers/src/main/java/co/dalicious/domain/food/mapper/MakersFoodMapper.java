package co.dalicious.domain.food.mapper;

import co.dalicious.domain.food.dto.FoodListDto;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.FoodDiscountPolicy;
import co.dalicious.system.util.enums.DiscountType;
import co.dalicious.system.util.enums.FoodTag;
import exception.ApiException;
import exception.ExceptionEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MakersFoodMapper {

    @Mapping(source = "food.id", target = "id")
    @Mapping(source = "food.makers.name", target = "makersName")
    @Mapping(source = "food.name", target = "foodName")
    @Mapping(target = "foodImage", ignore = true)
    @Mapping(source = "food.foodStatus.status", target = "foodStatus")
    @Mapping(source = "food.price", target = "defaultPrice")
    @Mapping(source = "food.foodDiscountPolicyList", target = "makersDiscount", qualifiedByName = "getDiscount")
    @Mapping(source = "food.foodDiscountPolicyList", target = "eventDiscount", qualifiedByName = "getDiscount")
    @Mapping(source = "resultPrice", target = "resultPrice")
    @Mapping(source = "food.description", target = "description")
    @Mapping(source = "food.foodTags", target = "foodTags", qualifiedByName = "getAllFoodList")
    FoodListDto toAllFoodListDto(Food food, BigDecimal resultPrice);


    @Mapping(source = "food.id", target = "id")
    @Mapping(source = "food.makers.name", target = "makersName")
    @Mapping(source = "food.name", target = "foodName")
    @Mapping(source = "food.image.location", target = "foodImage")
    @Mapping(source = "food.foodStatus.status", target = "foodStatus")
    @Mapping(source = "food.price", target = "defaultPrice")
    @Mapping(source = "food.foodDiscountPolicyList", target = "makersDiscount", qualifiedByName = "getDiscount")
    @Mapping(source = "food.foodDiscountPolicyList", target = "eventDiscount", qualifiedByName = "getDiscount")
    @Mapping(source = "resultPrice", target = "resultPrice")
    @Mapping(source = "food.description", target = "description")
    @Mapping(source = "food.foodTags", target = "foodTags", qualifiedByName = "getAllFoodList")
    FoodListDto toAllFoodListByMakersDto(Food food, BigDecimal resultPrice);


    @Named("getAllFoodList")
    default List<String> getAllFoodList(List<FoodTag> foodTags) {
        List<String> foodTagSrt = new ArrayList<>();
        if(foodTags != null) {
            for(FoodTag tag : foodTags) {
                foodTagSrt.add(tag.getTag());
            }
            return foodTagSrt;
        }
        throw  new ApiException(ExceptionEnum.NOT_FOUND);
    }

    @Named("getDiscount")
    default Integer getDiscount(List<FoodDiscountPolicy> discountPolicys) {
        Integer discountRate = null;
        for(FoodDiscountPolicy policy : discountPolicys ) {
            if(policy.getDiscountType() == DiscountType.MAKERS_DISCOUNT) {
                return discountRate = policy.getDiscountRate();
            } else if (policy.getDiscountType() == DiscountType.PERIOD_DISCOUNT) {
                return discountRate = policy.getDiscountRate();
            }
        }
        return discountRate;
    }
}









