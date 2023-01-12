package co.kurrant.app.public_api.mapper.food;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.food.dto.FoodDetailDto;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.util.OriginList;
import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FoodMapper extends GenericMapper<FoodDetailDto, Food> {

    @Mapping(source = "food", target="food", qualifiedByName = "food")
    @Mapping(source = "originList", target="originList")
    @Mapping(source = "price", target = "price")
    FoodDetailDto toFoodDetailDto(Food food, List<OriginList> originList, Integer price, BigDecimal discountRate);

    @Named("food")
    default Food food(Food food){
        return Food.builder()
                .description(food.getDescription())
                .img(food.getImg())
                .makers(food.getMakers())
                .price(food.getPrice())
                .spicy(food.getSpicy())
                .id(food.getId())
                .name(food.getName())
                .discountedRate(food.getDiscountedRate())
                .build();
    }

}
