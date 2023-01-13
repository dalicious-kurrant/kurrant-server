package co.kurrant.app.public_api.mapper.order;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.food.entity.DailyFood;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.FoodStatus;
import co.kurrant.app.public_api.dto.food.DailyFoodDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DailyFoodMapper extends GenericMapper<DailyFoodDto, DailyFood> {

      DailyFoodMapper INSTANCE = Mappers.getMapper(DailyFoodMapper.class);

      DailyFood toEntity(DailyFoodDto dto);

      DailyFoodDto toDto(DailyFood dailyFood);

      @Mapping(source = "food", target = "food", qualifiedByName = "generateFood")
      @Mapping(source = "diningType", target = "diningType", qualifiedByName = "diningType")
      @Mapping(source = "status", target = "status", qualifiedByName = "status")
      @Mapping(source = "discountRate", target = "discountRate")
      DailyFoodDto toDailyFoodDto(DailyFood dailyFood, Integer discountRate);


      @Named("generateFood")
      default Food generateFood(Food food){
            return Food.builder()
                    .id(food.getId())
                    .name(food.getName())
                    .discountedRate(food.getDiscountedRate())
                    .spicy(food.getSpicy())
                    .price(food.getPrice())
                    .makers(food.getMakers())
                    .img(food.getImg())
                    .description(food.getDescription())
                    .build();
      }
      @Named("diningType")
      default  String diningType(DiningType diningType){
            return diningType.getDiningType();
      }

      @Named("status")
      default String status(FoodStatus status){
            return status.getStatus();
      }
}
