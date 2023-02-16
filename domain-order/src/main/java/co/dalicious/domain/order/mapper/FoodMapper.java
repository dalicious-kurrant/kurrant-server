package co.dalicious.domain.order.mapper;

import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.dto.FoodDetailDto;
import co.dalicious.domain.food.dto.FoodListDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.enums.FoodStatus;
import co.dalicious.system.enums.FoodTag;
import co.dalicious.domain.food.entity.enums.Origin;
import co.dalicious.domain.food.dto.OriginDto;
import co.dalicious.domain.food.util.FoodUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
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
    @Mapping(target = "discountedPrice", expression = "java(FoodUtil.getFoodTotalDiscountedPrice(dailyFood.getFood(), discountDto))")
    @Mapping(source = "dailyFood.food.image.location", target = "image")
    @Mapping(source = "dailyFood", target = "spicy", qualifiedByName = "getSpicy")
    @Mapping(source = "dailyFood.food.name", target = "name")
    @Mapping(source = "dailyFood.food.description", target = "description")
    @Mapping(source = "dailyFood.food.makers.origins", target = "origins", qualifiedByName = "originsToDto")
    FoodDetailDto toDto(DailyFood dailyFood, DiscountDto discountDto);

    @Mapping(source = "foodListDto.foodName", target = "name")
    @Mapping(source = "makers", target = "makers")
    @Mapping(source = "foodListDto.foodStatus", target = "foodStatus", qualifiedByName = "getFoodStatus")
    @Mapping(source = "foodListDto.defaultPrice", target = "price")
    @Mapping(source = "foodListDto.description", target = "description")
    @Mapping(source = "foodTags", target = "foodTags")
    @Mapping(source = "customPrice", target = "customPrice")
    Food toNewEntity(FoodListDto foodListDto, Makers makers, BigDecimal customPrice, List<FoodTag> foodTags);

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

    @Named("getFoodStatus")
    default FoodStatus getFoodStatus(String foodStatusStr) {
        FoodStatus foodStatus = null;
        if(foodStatusStr != null) {
            return foodStatus = FoodStatus.ofString(foodStatusStr);
        }
        throw new ApiException(ExceptionEnum.NOT_FOUND_FOOD_STATUS);
    }
}