package co.dalicious.domain.food.mapper;

import co.dalicious.domain.food.dto.FoodGroupDto;
import co.dalicious.domain.food.entity.FoodGroup;
import co.dalicious.system.util.StringUtils;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FoodGroupMapper {
    default FoodGroupDto.Response toDto(FoodGroup foodGroup) {
        return FoodGroupDto.Response.builder()
                .makers(foodGroup.getMakers().getName())
                .id(foodGroup.getId())
                .name(foodGroup.getName())
                .groupNumbers(StringUtils.integerListToString(foodGroup.getGroupNumbers()))
//                .saladPercent()
//                .bentoPercent()
//                .proteinPercent()
//                .dietPercent()
//                .postpartumPercent()
//                .sandwichPercent()
//                .conveniencePercent()
                .build();
    };
}
