package co.dalicious.domain.order.mapper;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.entity.CartDailyFood;
import co.dalicious.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartDailyFoodMapper {
    @Mapping(source = "user", target = "user")
    @Mapping(source = "dailyFood", target = "dailyFood")
    @Mapping(source = "count", target = "count")
    CartDailyFood toEntity(User user, Integer count, DailyFood dailyFood);
}
