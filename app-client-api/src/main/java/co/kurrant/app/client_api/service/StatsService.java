package co.kurrant.app.client_api.service;

import co.kurrant.app.client_api.model.SecurityUser;

public interface StatsService {
    Object test(SecurityUser securityUser);
  /*
  public ListItemResponseDto<StatsDailyResponseDto> getDaily(StatsDailyRequestDto query,
      Pageable pageable);

  public StatsDailySummaryResponseDto getDailySummary(StatsDailySummaryRequestDto query);

  public ListItemResponseDto<StatsWeeklyResponseDto> getWeekly(StatsWeeklyRequestDto query,
      Pageable pageable);
      */
}
