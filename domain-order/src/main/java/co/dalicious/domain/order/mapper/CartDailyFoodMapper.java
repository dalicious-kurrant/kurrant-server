package co.dalicious.domain.order.mapper;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.dto.CartDailyFoodDto;
import co.dalicious.domain.order.entity.CartDailyFood;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalTime;

@Mapper(componentModel = "spring")
public interface CartDailyFoodMapper {
    @Mapping(source = "cartDailyFood.id", target = "id")
    @Mapping(source = "cartDailyFood.dailyFood.id", target = "dailyFoodId")
    @Mapping(source = "cartDailyFood.dailyFood.dailyFoodStatus.code", target = "status")
    @Mapping(source = "cartDailyFood.dailyFood.food.name", target = "name")
    @Mapping(target = "image", expression = "java(cartDailyFood.getDailyFood().getFood().getImages() == null || cartDailyFood.getDailyFood().getFood().getImages().isEmpty() ? null : cartDailyFood.getDailyFood().getFood().getImages().get(0).getLocation())")
    @Mapping(source = "cartDailyFood.dailyFood.food.makers.name", target = "makers")
    @Mapping(source = "cartDailyFood.count", target = "count")
    @Mapping(source = "cartDailyFood.dailyFood.food.price", target = "price")
    @Mapping(source = "cartDailyFood.deliveryTime", target = "deliveryTime", qualifiedByName = "timeToString")
    @Mapping(target = "discountedPrice", expression = "java(discountDto.getDiscountedPrice())")
    @Mapping(source = "discountDto.membershipDiscountPrice", target = "membershipDiscountPrice")
    @Mapping(source = "discountDto.membershipDiscountRate", target = "membershipDiscountRate")
    @Mapping(source = "discountDto.makersDiscountPrice", target = "makersDiscountPrice")
    @Mapping(source = "discountDto.makersDiscountRate", target = "makersDiscountRate")
    @Mapping(source = "discountDto.periodDiscountPrice", target = "periodDiscountPrice")
    @Mapping(source = "cartDailyFood.dailyFood.isEatIn", target = "isEatIn")
    CartDailyFoodDto.DailyFood toDto(CartDailyFood cartDailyFood, DiscountDto discountDto);

    @Named("serviceDateToString")
    default String serviceDateToString(LocalDate serviceDate) {
        return DateUtils.format(serviceDate, "yyyy-MM-dd");
    }

    @Named("timeToString")
    default String timeToString(LocalTime deliveryTime) {
        return DateUtils.timeToString(deliveryTime);
    }
}
