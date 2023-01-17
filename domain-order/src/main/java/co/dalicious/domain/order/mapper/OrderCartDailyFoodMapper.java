package co.dalicious.domain.order.mapper;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.order.entity.OrderCartDailyFood;
import co.dalicious.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderCartDailyFoodMapper {
    @Mapping(source = "user", target = "user")
    @Mapping(source = "dailyFood", target = "dailyFood")
    @Mapping(source = "count", target = "count")
    OrderCartDailyFood toEntity(User user, DailyFood dailyFood, Integer count);
}
