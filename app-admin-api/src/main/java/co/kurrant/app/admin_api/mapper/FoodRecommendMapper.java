package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.dto.FoodGroupDto;
import co.dalicious.domain.food.entity.FoodGroup;
import co.dalicious.domain.recommend.dto.FoodRecommendDto;
import co.dalicious.domain.recommend.entity.FoodGroupRecommend;
import co.dalicious.domain.recommend.entity.FoodRecommend;
import co.dalicious.domain.recommend.entity.FoodRecommendType;
import co.dalicious.domain.recommend.entity.FoodRecommendTypes;
import co.dalicious.system.util.StringUtils;
import org.mapstruct.Mapper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", imports = {StringUtils.class})
public interface FoodRecommendMapper {
    default List<FoodRecommendDto.Response> toDtos(List<FoodRecommend> foodRecommends, List<Group> groups, List<FoodGroup> foodGroups) {
        List<FoodRecommendDto.Response> responses = new ArrayList<>();
        for (FoodRecommend foodRecommend : foodRecommends) {
            responses.add(toDto(foodRecommend, groups, foodGroups));
        }
        return responses;
    }
    default FoodRecommendDto.Response toDto(FoodRecommend foodRecommend, List<Group> groups, List<FoodGroup> foodGroups) {
        List<String> groupStr = new ArrayList<>();

        for (Group group : groups) {
             if(foodRecommend.getGroupIds().contains(group.getId()))
                 groupStr.add(group.getName());
        }

        return FoodRecommendDto.Response.builder()
                .id(foodRecommend.getId())
                .groups(StringUtils.StringListToString(groupStr))
                .foodType(toTypeDtos(foodRecommend.getFoodRecommendTypes()))
                .dailyFoodGroups(toGroupDtos(foodRecommend.getFoodGroupRecommends(), foodGroups))
                .build();
    }

    default FoodRecommendDto.TypeResponse toTypeDto(FoodRecommendTypes foodRecommendTypes) {
        List<FoodRecommendType> foodRecommendTypeList = foodRecommendTypes.getFoodRecommendTypes();
        List<String> foodTypes = foodRecommendTypeList.stream()
                .map(v -> v.getFoodTag().getTag())
                .toList();
        List<Integer> importances = foodRecommendTypeList.stream()
                .map(FoodRecommendType::getImportance)
                .toList();
        return FoodRecommendDto.TypeResponse.builder()
                .order(foodRecommendTypes.getOrder())
                .foodTypes(StringUtils.StringListToString(foodTypes))
                .importances(StringUtils.integerListToString(importances))
                .build();
    }

    default List<FoodRecommendDto.TypeResponse> toTypeDtos(List<FoodRecommendTypes> foodRecommendTypes) {
        return foodRecommendTypes.stream()
                .map(this::toTypeDto)
                .toList();
    }

    default FoodRecommendDto.GroupResponse toGroupDto(FoodGroupRecommend foodGroupRecommend, List<FoodGroup> foodGroups) {
        List<String> foodGroupStr = new ArrayList<>();
        for (FoodGroup foodGroup : foodGroups) {
            if(foodGroupRecommend.getFoodGroups().contains(foodGroup.getId()))
                foodGroupStr.add(foodGroup.getName());
        }
        return new FoodRecommendDto.GroupResponse(foodGroupRecommend.getDays().getCode(), StringUtils.StringListToString(foodGroupStr));
    }

    default List<FoodRecommendDto.GroupResponse> toGroupDtos(List<FoodGroupRecommend> foodGroupRecommends, List<FoodGroup> foodGroups) {

        return foodGroupRecommends.stream()
                .map(v -> this.toGroupDto(v, foodGroups))
                .toList();
    }
}
