package co.kurrant.app.client.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.kurrant.app.client.dto.StatsDailyRequestDto;
import co.kurrant.app.client.dto.StatsDailyResponseDto;
import co.kurrant.app.client.dto.StatsDailySummaryRequestDto;
import co.kurrant.app.client.dto.StatsDailySummaryResponseDto;
import co.kurrant.app.client.dto.StatsWeeklyRequestDto;
import co.kurrant.app.client.dto.StatsWeeklyResponseDto;
import co.kurrant.app.client.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "ORDER")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/v1/stats")
@RestController
public class StatsController {

  private final StatsService statsService;
/*
  @Operation(summary = "일간통계항목 조회", description = "일간통계 항목을 조회한다.")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/daily")
  public ListItemResponseDto<StatsDailyResponseDto> getDaily(HttpServletRequest request,
      @Parameter(name = "쿼리정보", description = "",
          required = false) @RequestParam Map<String, String> params,
      @PageableDefault(size = 20, sort = "createdDateTime",
          direction = Direction.DESC) OffsetBasedPageRequest pageable) {

    JsonMapper mapper = JsonMapper.builder()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build();
    StatsDailyRequestDto queryDto = mapper.convertValue(params, StatsDailyRequestDto.class);

    return statsService.getDaily(queryDto, pageable);
  }

  @Operation(summary = "일간통계 SUMMARY", description = "일간통계 SUMMARY 조회한다.")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/daily/summary")
  public StatsDailySummaryResponseDto getDailySummary(HttpServletRequest request,
      @Parameter(name = "쿼리정보", description = "",
          required = false) @RequestParam Map<String, String> params) {

    JsonMapper mapper = JsonMapper.builder()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build();
    StatsDailySummaryRequestDto queryDto =
        mapper.convertValue(params, StatsDailySummaryRequestDto.class);

    return statsService.getDailySummary(queryDto);
  }

  @Operation(summary = "주간통계", description = "주간통계를 조회한다.")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/weekly")
  public ListItemResponseDto<StatsWeeklyResponseDto> getWeekly(HttpServletRequest request,
      @Parameter(name = "쿼리정보", description = "",
          required = false) @RequestParam Map<String, String> params,
      @PageableDefault(size = 20, sort = "createdDateTime",
          direction = Direction.DESC) OffsetBasedPageRequest pageable) {

    JsonMapper mapper = JsonMapper.builder()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build();
    StatsWeeklyRequestDto queryDto = mapper.convertValue(params, StatsWeeklyRequestDto.class);

    return statsService.getWeekly(queryDto, pageable);
  }

 */
}
