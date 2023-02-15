package co.kurrant.app.client_api.mapper;

import co.kurrant.app.client_api.dto.StatsWeeklyDto;
import co.kurrant.app.client_api.dto.StatsWeeklyResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatsMapper {
  /*
  @Mappings({@Mapping(source = "price", target = "totalAmount"),
      @Mapping(source = "count", target = "quantity"),
      @Mapping(source = "foodName", target = "productName"),
      @Mapping(source = "order.createdDateTime", target = "createdDateTime")})
  StatsDailyResponseDto toDailyDto(OrderDetail order);*/

  StatsWeeklyResponseDto toWeeklyDto(StatsWeeklyDto weeklyDto);
}
