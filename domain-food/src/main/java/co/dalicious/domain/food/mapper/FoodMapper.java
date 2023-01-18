package co.dalicious.domain.food.mapper;

import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.dto.FoodDetailDto;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Origin;
import co.dalicious.domain.food.util.OriginDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FoodMapper {
    @Mapping(source = "food.makers.name", target = "makersName")
    @Mapping(source = "discountDto.membershipDiscountedPrice", target = "membershipDiscountedPrice")
    @Mapping(source = "discountDto.membershipDiscountedRate", target = "membershipDiscountedRate")
    @Mapping(source = "discountDto.makersDiscountedRate", target = "makersDiscountedRate")
    @Mapping(source = "discountDto.periodDiscountedPrice", target = "periodDiscountedPrice")
    @Mapping(source = "discountDto.periodDiscountedRate", target = "periodDiscountedRate")
    @Mapping(source = "discountDto.price", target = "price")
    @Mapping(source = "food.image.location", target = "image")
    @Mapping(source = "food.spicy.spicy", target = "spicy")
    @Mapping(source = "food.description", target = "description")
    @Mapping(source = "food.origins", target = "origins", qualifiedByName = "originsToDto")
    FoodDetailDto toDto(Food food, DiscountDto discountDto);

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
