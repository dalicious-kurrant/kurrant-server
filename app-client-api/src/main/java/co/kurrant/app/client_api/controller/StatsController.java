package co.kurrant.app.client_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.client_api.model.SecurityUser;
import co.kurrant.app.client_api.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import co.kurrant.app.client_api.service.StatsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "ORDER")
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/v1/status")
@RestController
public class StatsController {

  private final StatsService statsService;
  @GetMapping("")
  public ResponseMessage getGroupList(Authentication authentication) {
    SecurityUser securityUser = UserUtil.securityUser(authentication);
    return ResponseMessage.builder()
            .message("기업 정보를 조회했습니다.")
            .data(statsService.test(securityUser))
            .build();
  }

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
