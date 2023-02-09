package co.kurrant.app.client.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import co.dalicious.domain.order.entity.OrderDetail;
import co.kurrant.app.client.dto.StatsDailyResponseDto;
import co.kurrant.app.client.dto.StatsWeeklyDto;
import co.kurrant.app.client.dto.StatsWeeklyResponseDto;

@Mapper(componentModel = "spring")
public interface StatsMapper {
  @Mappings({@Mapping(source = "price", target = "totalAmount"),
      @Mapping(source = "count", target = "quantity"),
      @Mapping(source = "foodName", target = "productName"),
      @Mapping(source = "order.createdDateTime", target = "createdDateTime")})
  StatsDailyResponseDto toDailyDto(OrderDetail order);

  StatsWeeklyResponseDto toWeeklyDto(StatsWeeklyDto weeklyDto);
}
