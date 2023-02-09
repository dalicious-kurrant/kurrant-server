package co.kurrant.app.client.service;

import org.springframework.data.domain.Pageable;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.kurrant.app.client.dto.StatsDailyRequestDto;
import co.kurrant.app.client.dto.StatsDailyResponseDto;
import co.kurrant.app.client.dto.StatsDailySummaryRequestDto;
import co.kurrant.app.client.dto.StatsDailySummaryResponseDto;
import co.kurrant.app.client.dto.StatsWeeklyRequestDto;
import co.kurrant.app.client.dto.StatsWeeklyResponseDto;

public interface StatsService {
  public ListItemResponseDto<StatsDailyResponseDto> getDaily(StatsDailyRequestDto query,
      Pageable pageable);

  public StatsDailySummaryResponseDto getDailySummary(StatsDailySummaryRequestDto query);

  public ListItemResponseDto<StatsWeeklyResponseDto> getWeekly(StatsWeeklyRequestDto query,
      Pageable pageable);
}
