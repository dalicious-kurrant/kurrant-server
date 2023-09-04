package co.dalicious.domain.food.mapper;

import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.dto.FoodListDto;
import co.dalicious.domain.food.dto.MakersFoodDetailDto;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.FoodCapacity;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.MakersCapacity;
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

@Mapper(componentModel = "spring", imports = {Image.class, DiningType.class, DayAndTime.class})
public interface MakersFoodMapper {
    @Mapping(source = "food.calorie", target = "calorie")
    @Mapping(source = "food.fat", target = "fat")
    @Mapping(source = "food.protein", target = "protein")
    @Mapping(source = "food.carbohydrate", target = "carbohydrate")
    @Mapping(source = "food.id", target = "foodId")
    @Mapping(source = "food.makers.name", target = "makersName")
    @Mapping(source = "food.makers.id", target = "makersId")
    @Mapping(source = "food.name", target = "foodName")
    @Mapping(target = "foodImage", ignore = true)
    @Mapping(source = "food.foodStatus.status", target = "foodStatus")
    @Mapping(source = "food.supplyPrice", target = "supplyPrice")
    @Mapping(source = "food.price", target = "defaultPrice")
    @Mapping(source = "discountDto.makersDiscountRate", target = "makersDiscount")
    @Mapping(source = "discountDto.membershipDiscountRate", target = "membershipDiscount")
    @Mapping(source = "discountDto.periodDiscountRate", target = "eventDiscount")
    @Mapping(source = "resultPrice", target = "resultPrice")
    @Mapping(source = "food.description", target = "description")
    @Mapping(target = "foodGroupId", expression = "java(food.getFoodGroup() == null ? null : food.getFoodGroup().getId())")
    @Mapping(target = "foodGroup", expression = "java(food.getFoodGroup() == null ? null : food.getFoodGroup().getName())")
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
    @Mapping(target = "foodGroupId", expression = "java(food.getFoodGroup() == null ? null : food.getFoodGroup().getId())")
    @Mapping(target = "foodGroup", expression = "java(food.getFoodGroup() == null ? null : food.getFoodGroup().getName())")
    FoodListDto.FoodList toAllFoodListByMakersDto(Food food, DiscountDto discountDto, BigDecimal resultPrice);

    @Mapping(source = "food.makers.name", target = "makersName")
    @Mapping(source = "food.id", target = "foodId")
    @Mapping(source = "food.name", target = "foodName")
    @Mapping(target = "foodGroup", expression = "java(food.getFoodGroup() == null ? null : food.getFoodGroup().getName())")
    @Mapping(source = "food.supplyPrice", target = "supplyPrice")
    @Mapping(source = "food.price", target = "foodPrice")
    @Mapping(target = "foodImages", expression = "java(Image.getImagesLocation(food.getImages()))")
    @Mapping(target = "introImages", expression = "java(Image.getImagesLocation(food.getIntroImages()))")
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
    @Mapping(source = "food", target = "morningLastOrderTime", qualifiedByName = "getMorningLastOrderTime")
    @Mapping(source = "food", target = "lunchLastOrderTime", qualifiedByName = "getLunchLastOrderTime")
    @Mapping(source = "food", target = "dinnerLastOrderTime", qualifiedByName = "getDinnerLastOrderTime")
    @Mapping(source = "food.calorie", target = "calorie")
    @Mapping(source = "food.carbohydrate", target = "carbohydrate")
    @Mapping(source = "food.fat", target = "fat")
    @Mapping(source = "food.protein", target = "protein")
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
        if (foodTags != null) {
            for (FoodTag tag : foodTags) {
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
        if (customPrice != null) {
            return bigDecimal = bigDecimal.add(customPrice);
        }
        return bigDecimal;
    }

    @Named("getDinnerLastOrderTime")
    default String getDinnerLastOrderTime(Food food) {
        FoodCapacity foodCapacity = food.getFoodCapacity(DiningType.DINNER);
        if (foodCapacity == null) return "정보 없음";
        return DayAndTime.dayAndTimeToString(foodCapacity.getLastOrderTime());
    }

    @Named("getLunchLastOrderTime")
    default String getLunchLastOrderTime(Food food) {
        FoodCapacity foodCapacity = food.getFoodCapacity(DiningType.LUNCH);
        if (foodCapacity == null) return "정보 없음";
        return DayAndTime.dayAndTimeToString(foodCapacity.getLastOrderTime());
    }

    @Named("getMorningLastOrderTime")
    default String getMorningLastOrderTime(Food food) {
        FoodCapacity foodCapacity = food.getFoodCapacity(DiningType.MORNING);
        if (foodCapacity == null) return "정보 없음";
        return DayAndTime.dayAndTimeToString(foodCapacity.getLastOrderTime());
    }
}









