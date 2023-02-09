package co.kurrant.app.client.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import co.dalicious.domain.order.entity.OrderDetail;
import co.kurrant.app.client.dto.StatsDailyRequestDto;
import co.kurrant.app.client.dto.StatsWeeklyDto;
import co.kurrant.app.client.dto.StatsWeeklyRequestDto;

public interface OrderItemCustomRepository {
  Page<OrderDetail> findAllBySearchCriteria(StatsDailyRequestDto dto, Pageable pageable);

  List<StatsWeeklyDto> findAllByWeeklyStats(StatsWeeklyRequestDto dto);
}
