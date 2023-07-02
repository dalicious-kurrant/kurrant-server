package co.dalicious.domain.food.mapper;

import co.dalicious.domain.food.dto.FoodGroupDto;
import co.dalicious.domain.food.entity.FoodGroup;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.system.enums.FoodTag;
import co.dalicious.system.util.NumberUtils;
import co.dalicious.system.util.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", imports = {NumberUtils.class, StringUtils.class})
public interface FoodGroupMapper {
    default FoodGroupDto.Response toDto(FoodGroup foodGroup) {
        Integer totalCount = foodGroup.totalFoodCount();
        return FoodGroupDto.Response.builder()
                .makers(foodGroup.getMakers().getName())
                .id(foodGroup.getId())
                .name(foodGroup.getName())
                .groupNumbers(StringUtils.integerListToString(foodGroup.getGroupNumbers()))
                .saladPercent(NumberUtils.getPercentString(totalCount, foodGroup.foodTagCount(FoodTag.FOOD_TAG_SALAD)))
                .dinnerBoxPercent(NumberUtils.getPercentString(totalCount, foodGroup.foodTagCount(FoodTag.FOOD_TAG_DINNER_BOX)))
                .dietPercent(NumberUtils.getPercentString(totalCount, foodGroup.foodTagCount(FoodTag.FOOD_TAG_DIET)))
                .postpartumPercent(NumberUtils.getPercentString(totalCount, foodGroup.foodTagCount(FoodTag.FOOD_TAG_POSTPARTUM)))
                .proteinPercent(NumberUtils.getPercentString(totalCount, foodGroup.foodTagCount(FoodTag.FOOD_TAG_PROTEIN)))
                .singleBowlPercent(NumberUtils.getPercentString(totalCount, foodGroup.foodTagCount(FoodTag.FOOD_TAG_SINGLE_BOWL)))
                .conveniencePercent(NumberUtils.getPercentString(totalCount, foodGroup.foodTagCount(FoodTag.FOOD_TAG_CONVENIENCE_FOOD)))
                .build();
    };

    default List<FoodGroupDto.Response> toDtos(List<FoodGroup> foodGroupList) {
        return foodGroupList.stream()
                .map(this::toDto)
                .toList();
    }

    default FoodGroup toEntity(FoodGroupDto.Request request, Makers makers) {
        return FoodGroup.builder()
                .makers(makers)
                .name(request.getName())
                .groupNumbers(StringUtils.parseIntegerList(request.getGroupNumbers()))
                .build();
    }

    FoodGroupDto.NameList toNameList(FoodGroup foodGroup);
}
