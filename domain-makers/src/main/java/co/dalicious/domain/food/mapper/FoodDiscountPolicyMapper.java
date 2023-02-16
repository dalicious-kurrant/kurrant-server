package co.dalicious.domain.food.mapper;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.FoodDiscountPolicy;
import co.dalicious.system.enums.DiscountType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FoodDiscountPolicyMapper {

    @Mapping(source = "type", target = "discountType")
    @Mapping(source = "rate", target = "discountRate")
    @Mapping(source = "food", target = "food")
    FoodDiscountPolicy toEntity(DiscountType type, Integer rate, Food food);
}
