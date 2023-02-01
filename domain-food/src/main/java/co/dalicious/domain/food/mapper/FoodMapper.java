package co.dalicious.domain.food.mapper;

import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.dto.FoodDetailDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Origin;
import co.dalicious.domain.food.dto.OriginDto;
import co.dalicious.domain.food.util.FoodUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", imports = FoodUtil.class)
public interface FoodMapper {
    @Mapping(source = "dailyFood.food.makers.name", target = "makersName")
    @Mapping(source = "discountDto.membershipDiscountPrice", target = "membershipDiscountedPrice")
    @Mapping(source = "discountDto.membershipDiscountRate", target = "membershipDiscountedRate")
    @Mapping(source = "discountDto.makersDiscountPrice", target = "makersDiscountedPrice")
    @Mapping(source = "discountDto.makersDiscountRate", target = "makersDiscountedRate")
    @Mapping(source = "discountDto.periodDiscountPrice", target = "periodDiscountedPrice")
    @Mapping(source = "discountDto.periodDiscountRate", target = "periodDiscountedRate")
    @Mapping(source = "discountDto.price", target = "price")
    @Mapping(source = "dailyFood.capacity", target = "capacity")
    @Mapping(target = "discountedPrice", expression = "java(FoodUtil.getFoodTotalDiscountedPrice(dailyFood.getFood(), discountDto))")
    @Mapping(source = "dailyFood.food.image.location", target = "image")
    @Mapping(source = "dailyFood.food.spicy.spicy", target = "spicy")
    @Mapping(source = "dailyFood.food.name", target = "name")
    @Mapping(source = "dailyFood.food.description", target = "description")
    @Mapping(source = "dailyFood.food.origins", target = "origins", qualifiedByName = "originsToDto")
    FoodDetailDto toDto(DailyFood dailyFood, DiscountDto discountDto);

    @Named("originsToDto")
    default List<OriginDto> originsToDto(List<Origin> origins) {
        List<OriginDto> originDtos = new ArrayList<>();
        for (Origin origin : origins) {
            OriginDto originDto = OriginDto.builder()
                    .name(origin.getName())
                    .from(origin.getFrom())
                    .build();
            originDtos.add(originDto);
        }
        return originDtos;
    }
}
