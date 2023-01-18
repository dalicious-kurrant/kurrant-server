package co.dalicious.domain.order.mapper;

import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.order.dto.CartDailyFoodDto;
import co.dalicious.domain.order.entity.CartDailyFood;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface CartDailyFoodResMapper {
    @Mapping(source = "cartDailyFood.id", target = "id")
    @Mapping(source = "cartDailyFood.dailyFood.id", target = "dailyFoodId")
    @Mapping(source = "cartDailyFood.dailyFood.food.name", target = "name")
    @Mapping(source = "cartDailyFood.dailyFood.food.image.location", target = "image")
    @Mapping(source = "cartDailyFood.dailyFood.food.image.location", target = "makers")
    @Mapping(source = "cartDailyFood.dailyFood.diningType.diningType", target = "diningType")
    @Mapping(source = "count", target = "count")
    @Mapping(source = "cartDailyFood.dailyFood.food.price", target = "price")
    @Mapping(source = "discountDto.membershipDiscountPrice", target = "membershipDiscountPrice")
    @Mapping(source = "discountDto.membershipDiscountRate", target = "membershipDiscountRate")
    @Mapping(source = "discountDto.makersDiscountPrice", target = "makersDiscountPrice")
    @Mapping(source = "discountDto.makersDiscountRate", target = "makersDiscountRate")
    @Mapping(source = "discountDto.periodDiscountPrice", target = "periodDiscountPrice")
    @Mapping(source = "discountDto.periodDiscountRate", target = "periodDiscountRate")
    @Mapping(source = "cartDailyFood.dailyFood.serviceDate", target = "serviceDate")
    CartDailyFoodDto toDto(CartDailyFood cartDailyFood, DiscountDto discountDto);

    @Named("serviceDateToString")
    default String serviceDateToString(LocalDate serviceDate) {
        return DateUtils.format(serviceDate, "yyyy-MM-dd");
    }
}
