package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.system.util.PeriodDto;
import co.kurrant.app.admin_api.service.DailyFoodService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/dailyFoods")
public class DailyFoodController {
    private final DailyFoodService dailyFoodService;
    @Operation(summary = "식단 승인", description = "식사 예정 날짜를 기준으로 식단은 승인한다.")
    @PostMapping("/approval")
    public ResponseMessage approveSchedule(@RequestBody PeriodDto.PeriodStringDto periodDto) {
        dailyFoodService.approveSchedule(periodDto);
        return ResponseMessage.builder()
                .message(periodDto.getStartDate() + " ~ " + periodDto.getEndDate() + "사이의 식단을 승인했습니다.")
                .build();
    }

    @Operation(summary = "식단 조회", description = "")
    @GetMapping("")
    public ResponseMessage getDailyFoods(@RequestParam Map<String, Object> parameters) {
        return ResponseMessage.builder()
                .data(dailyFoodService.getDailyFoods(parameters))
                .message("식단 조회에 성공하였습니다.")
                .build();
    }
}
