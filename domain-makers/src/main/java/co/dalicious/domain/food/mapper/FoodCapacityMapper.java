package co.dalicious.domain.food.mapper;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.FoodCapacity;
import co.dalicious.system.enums.DiningType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FoodCapacityMapper {

    @Mapping(source = "diningType", target = "diningType")
    @Mapping(source = "capacity", target = "capacity")
    @Mapping(source = "food", target = "food")
    FoodCapacity toEntity(DiningType diningType, Integer capacity, Food food);
}
