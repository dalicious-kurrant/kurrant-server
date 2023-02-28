package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.system.util.PeriodDto;
import co.kurrant.app.admin_api.dto.FoodDto;
import co.kurrant.app.admin_api.service.DailyFoodService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @Operation(summary = "그룹과 메이커스 조회", description = "그룹과 메이커스 리스트를 조회한다.")
    @GetMapping("/groupsAndMakers")
    public ResponseMessage getGroupAndMakers() {
        return ResponseMessage.builder()
                .data(dailyFoodService.getGroupAndMakers())
                .message("식단 조회에 성공하였습니다.")
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

    @Operation(summary = "식단 엑셀 저장 및 수정", description = "식단을 엑셀 파일을 통해 수정하거나 저장한다.")
    @PostMapping("/excel")
    public ResponseMessage excelDailyFood(@RequestBody List<FoodDto.DailyFood> dailyFoodList) {
        dailyFoodService.excelDailyFoods(dailyFoodList);
        return ResponseMessage.builder()
                .message("식단 조회에 성공하였습니다.")
                .build();
    }
}
