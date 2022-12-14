package co.kurrant.app.public_api.service.impl.mapper;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.food.entity.DailyFood;

import co.kurrant.app.public_api.dto.food.DailyFoodDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DailyFoodMapper extends GenericMapper<DailyFoodDto, DailyFood> {

      DailyFoodMapper INSTANCE = Mappers.getMapper(DailyFoodMapper.class);
}
