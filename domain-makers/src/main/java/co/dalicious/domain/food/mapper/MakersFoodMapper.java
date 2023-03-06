package co.dalicious.domain.food.mapper;

import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.dto.FoodListDto;
import co.dalicious.domain.food.dto.MakersFoodDetailDto;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.enums.FoodTag;
import exception.ApiException;
import exception.ExceptionEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {Image.class, DiningType.class})
public interface MakersFoodMapper {

    @Mapping(source = "food.id", target = "foodId")
    @Mapping(source = "food.makers.name", target = "makersName")
    @Mapping(source = "food.makers.id", target = "makersId")
    @Mapping(source = "food.name", target = "foodName")
    @Mapping(target = "foodImage", ignore = true)
    @Mapping(source = "food.foodStatus.status", target = "foodStatus")
    @Mapping(source = "food.price", target = "defaultPrice")
    @Mapping(source = "discountDto.makersDiscountRate", target = "makersDiscount")
    @Mapping(source = "discountDto.membershipDiscountRate", target = "membershipDiscount")
    @Mapping(source = "discountDto.periodDiscountRate", target = "eventDiscount")
    @Mapping(source = "resultPrice", target = "resultPrice")
    @Mapping(source = "food.description", target = "description")
    @Mapping(source = "food.foodTags", target = "foodTags", qualifiedByName = "getAllFoodList")
    FoodListDto.FoodList toAllFoodListDto(Food food, DiscountDto discountDto, BigDecimal resultPrice);


    @Mapping(source = "food.id", target = "foodId")
    @Mapping(source = "food.makers.name", target = "makersName")
    @Mapping(target = "makersId", ignore = true)
    @Mapping(source = "food.name", target = "foodName")
    @Mapping(target = "foodImage", expression = "java(food.getImages().isEmpty() ? null : food.getImages().get(0).getLocation())")
    @Mapping(source = "food.foodStatus.status", target = "foodStatus")
    @Mapping(source = "food.price", target = "defaultPrice")
    @Mapping(source = "discountDto.makersDiscountRate", target = "makersDiscount")
    @Mapping(source = "discountDto.periodDiscountRate", target = "eventDiscount")
    @Mapping(source = "discountDto.membershipDiscountRate", target = "membershipDiscount")
    @Mapping(source = "resultPrice", target = "resultPrice")
    @Mapping(source = "food.description", target = "description")
    @Mapping(source = "food.foodTags", target = "foodTags", qualifiedByName = "getAllFoodList")
    FoodListDto.FoodList toAllFoodListByMakersDto(Food food, DiscountDto discountDto, BigDecimal resultPrice);

    @Mapping(source = "food.makers.name", target = "makersName")
    @Mapping(source = "food.id", target = "foodId")
    @Mapping(source = "food.name", target = "foodName")
    @Mapping(source = "food.price", target = "foodPrice")
    @Mapping(target = "foodImages", expression = "java(Image.getImagesLocation(food.getImages()))")
    @Mapping(source = "discountDto.membershipDiscountPrice", target = "membershipDiscountPrice")
    @Mapping(source = "discountDto.membershipDiscountRate", target = "membershipDiscountRate")
    @Mapping(source = "discountDto.makersDiscountPrice", target = "makersDiscountPrice")
    @Mapping(source = "discountDto.makersDiscountRate", target = "makersDiscountRate")
    @Mapping(source = "discountDto.periodDiscountPrice", target = "periodDiscountPrice")
    @Mapping(source = "discountDto.periodDiscountRate", target = "periodDiscountRate")
    @Mapping(source = "food.foodTags", target = "foodTags", qualifiedByName = "getFoodTagList")
    @Mapping(source = "food.description", target = "description")
    @Mapping(source = "food.customPrice", target = "customPrice", qualifiedByName = "customPrice")
    @Mapping(target = "morningCapacity", expression = "java(food.getFoodCapacity(DiningType.MORNING) == null ? 0 : food.getFoodCapacity(DiningType.MORNING).getCapacity())")
    @Mapping(target = "lunchCapacity", expression = "java(food.getFoodCapacity(DiningType.LUNCH) == null ? 0 : food.getFoodCapacity(DiningType.LUNCH).getCapacity())")
    @Mapping(target = "dinnerCapacity", expression = "java(food.getFoodCapacity(DiningType.DINNER) == null ? 0 : food.getFoodCapacity(DiningType.DINNER).getCapacity())")
    MakersFoodDetailDto toFoodManagingDto(Food food, DiscountDto discountDto);
    @Named("getAllFoodList")
    default List<String> getAllFoodList(List<FoodTag> foodTags) {
        if (foodTags == null) {
            throw new ApiException(ExceptionEnum.NOT_FOUND);
        }
        List<Integer> foodTagCodes = foodTags.stream()
                .map(FoodTag::getCode)
                .sorted()
                .toList();
        return foodTagCodes.stream()
                .map(FoodTag::ofCode)
                .map(FoodTag::getTag)
                .collect(Collectors.toList());
    }

    @Named("getFoodTagList")
    default List<Integer> getFoodTagList(List<FoodTag> foodTags) {
        List<Integer> foodTagList = new ArrayList<>();
        if(foodTags != null) {
            for(FoodTag tag : foodTags) {
                foodTagList.add(tag.getCode());
            }
            Collections.sort(foodTagList);
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









