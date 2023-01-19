package co.kurrant.app.public_api.mapper.order;

import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.FoodStatus;
import co.kurrant.app.public_api.dto.food.DailyFoodDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface DailyFoodMapper {

      @Mapping(source = "dailyFood.food", target = "food")
      @Mapping(source = "diningType", target = "diningType", qualifiedByName = "diningType")
      @Mapping(source = "status", target = "status")
      DailyFoodDto toDailyFoodDto(DailyFood dailyFood,DiningType diningType,
                                  FoodStatus status, Integer discountedPrice, BigDecimal discountRate);


//      @Named("generateFood")
//      default Food generateFood(DailyFood dailyFood){
//            return Food.builder()
//                    .id(dailyFood.getId())
//                    .name(dailyFood.getFood().getName())
//                    .discountedRate(dailyFood.getFood().getDiscountedRate())
//                    .spicy(dailyFood.getFood().getSpicy())
//                    .price(dailyFood.getFood().getPrice())
//                    .makers(dailyFood.getFood().getMakers())
//                    .img(dailyFood.getFood().getImg())
//                    .description(dailyFood.getFood().getDescription())
//                    .build();
//      }

      @Named("diningType")
      default Integer diningType(DiningType diningType){
            return diningType.getCode();
      }

}
