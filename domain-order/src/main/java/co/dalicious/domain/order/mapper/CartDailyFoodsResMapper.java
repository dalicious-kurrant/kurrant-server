package co.dalicious.domain.order.mapper;

import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.order.dto.CartDailyFoodDto;
import co.dalicious.domain.order.entity.CartDailyFood;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalTime;

@Mapper(componentModel = "spring")
public interface CartDailyFoodsResMapper {

}
