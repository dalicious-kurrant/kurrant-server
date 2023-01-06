package co.kurrant.app.public_api.service.impl.mapper;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.food.entity.DailyFood;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.makers.entity.Makers;
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

      @Mapping(source = "makersName", target = "food", qualifiedByName = "generateMakers")
      DailyFoodDto toDailyFoodDto(DailyFood dailyFood);

      @Named("generateMakers")
      default  String generateMakers(Food food){return food.getMakers().getName();}

}
