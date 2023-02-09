package co.dalicious.domain.food.mapper;

import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.dto.FoodDetailDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.system.util.enums.FoodTag;
import co.dalicious.domain.makers.entity.enums.Origin;
import co.dalicious.domain.food.dto.OriginDto;
import co.dalicious.domain.food.util.FoodUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @Mapping(source = "dailyFood", target = "spicy", qualifiedByName = "getSpicy")
    @Mapping(source = "dailyFood.food.name", target = "name")
    @Mapping(source = "dailyFood.food.description", target = "description")
    @Mapping(source = "dailyFood.food.makers.origins", target = "origins", qualifiedByName = "originsToDto")
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

    @Named("getSpicy")
    default String getSpicy(DailyFood dailyFood) {
        List<FoodTag> foodTags = dailyFood.getFood().getFoodTags();
        Optional<FoodTag> foodTag = foodTags.stream().filter(v -> v.getCategory().equals("맵기")).findAny();
        return foodTag.map(FoodTag::getTag).orElse(null);
    }
}
