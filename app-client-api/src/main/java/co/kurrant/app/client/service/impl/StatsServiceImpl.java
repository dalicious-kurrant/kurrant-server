package co.kurrant.app.client.service.impl;

import co.kurrant.app.client.service.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Service
public class StatsServiceImpl implements StatsService {
  /*
  private final OrderItemRepository orderItemRepository;

  private final StatsMapper statsMapper;

  @Override
  public ListItemResponseDto<StatsDailyResponseDto> getDaily(StatsDailyRequestDto query,
      Pageable pageable) {

    Page<OrderDetail> foundOrderItems =
        orderItemRepository.findAllBySearchCriteria(query, pageable);

    List<StatsDailyResponseDto> items =
        foundOrderItems.get().map((foundOrderItem) -> statsMapper.toDailyDto(foundOrderItem))
            .collect(Collectors.toList());

    return ListItemResponseDto.<StatsDailyResponseDto>builder().items(items)
        .total(foundOrderItems.getTotalElements()).count(foundOrderItems.getNumberOfElements())
        .limit(pageable.getPageSize()).offset(pageable.getOffset()).build();
  }

  @Override
  public StatsDailySummaryResponseDto getDailySummary(StatsDailySummaryRequestDto query) {
    return StatsDailySummaryResponseDto.builder().totalOrderAmount(null).totalOrderItemCount(null)
        .totalOrderUserCount(null).build();
  }

  @Override
  public ListItemResponseDto<StatsWeeklyResponseDto> getWeekly(StatsWeeklyRequestDto query,
      Pageable pageable) {

    List<StatsWeeklyDto> foundWeeklyItems = orderItemRepository.findAllByWeeklyStats(query);

    List<StatsWeeklyResponseDto> items =
        foundWeeklyItems.stream().map((foundWeeklyItem) -> statsMapper.toWeeklyDto(foundWeeklyItem))
            .collect(Collectors.toList());

    return ListItemResponseDto.<StatsWeeklyResponseDto>builder().items(items)
        .total((long) foundWeeklyItems.size()).count(foundWeeklyItems.size())
        .limit(pageable.getPageSize()).offset(pageable.getOffset()).build();
  }
  */

}
