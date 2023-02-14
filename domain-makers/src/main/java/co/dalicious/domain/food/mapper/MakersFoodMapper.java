package co.dalicious.domain.food.mapper;

import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.dto.FoodListDto;
import co.dalicious.domain.food.dto.FoodManagingDto;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.system.util.enums.FoodTag;
import exception.ApiException;
import exception.ExceptionEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
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
    @Mapping(source = "discountDto.makersDiscountRate", target = "makersDiscount")
    @Mapping(source = "discountDto.periodDiscountRate", target = "eventDiscount")
    @Mapping(source = "resultPrice", target = "resultPrice")
    @Mapping(source = "food.description", target = "description")
    @Mapping(source = "food.foodTags", target = "foodTags", qualifiedByName = "getAllFoodList")
    FoodListDto toAllFoodListDto(Food food, DiscountDto discountDto, BigDecimal resultPrice);


    @Mapping(source = "food.id", target = "id")
    @Mapping(source = "food.makers.name", target = "makersName")
    @Mapping(source = "food.name", target = "foodName")
    @Mapping(source = "food.image.location", target = "foodImage")
    @Mapping(source = "food.foodStatus.status", target = "foodStatus")
    @Mapping(source = "food.price", target = "defaultPrice")
    @Mapping(source = "discountDto.makersDiscountRate", target = "makersDiscount")
    @Mapping(source = "discountDto.periodDiscountRate", target = "eventDiscount")
    @Mapping(source = "resultPrice", target = "resultPrice")
    @Mapping(source = "food.description", target = "description")
    @Mapping(source = "food.foodTags", target = "foodTags", qualifiedByName = "getAllFoodList")
    FoodListDto toAllFoodListByMakersDto(Food food, DiscountDto discountDto, BigDecimal resultPrice);

    @Mapping(source = "food.makers.name", target = "makersName")
    @Mapping(source = "food.id", target = "foodId")
    @Mapping(source = "food.name", target = "foodName")
    @Mapping(source = "food.price", target = "foodPrice")
    @Mapping(source = "food.image.location", target = "foodImage")
    @Mapping(source = "discountDto.makersDiscountPrice", target = "makersDiscountPrice")
    @Mapping(source = "discountDto.makersDiscountRate", target = "makersDiscountRate")
    @Mapping(source = "discountDto.periodDiscountPrice", target = "periodDiscountPrice")
    @Mapping(source = "discountDto.periodDiscountRate", target = "periodDiscountRate")
    @Mapping(source = "food.foodTags", target = "foodTags", qualifiedByName = "getFoodTagList")
    @Mapping(source = "food.description", target = "description")
    @Mapping(source = "food.customPrice", target = "customPrice", qualifiedByName = "customPrice")
    FoodManagingDto toFoodManagingDto(Food food, DiscountDto discountDto);

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

    @Named("getFoodTagList")
    default List<Integer> getFoodTagList(List<FoodTag> foodTags) {
        List<Integer> foodTagList = new ArrayList<>();
        if(foodTags != null) {
            for(FoodTag tag : foodTags) {
                foodTagList.add(tag.getCode());
            }
            return foodTagList;
        }
        throw new ApiException(ExceptionEnum.NOT_FOUND);
    }

    @Named("customPrice")
    default BigDecimal customPrice(BigDecimal customPrice) {
        BigDecimal bigDecimal = BigDecimal.ZERO;
        if(customPrice != null) {
            return bigDecimal = bigDecimal.add(customPrice);
        }
        return bigDecimal;
    }
}









