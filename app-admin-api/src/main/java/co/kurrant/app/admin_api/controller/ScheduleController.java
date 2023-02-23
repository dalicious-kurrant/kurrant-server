package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.admin_api.dto.schedules.ExcelPresetDailyFoodDto;
import co.kurrant.app.admin_api.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "식단 전체 조회", description = "존재하는 식단을 모두 조회합니다.")
    @GetMapping("/all")
    public ResponseMessage getAllPresetScheduleList(@RequestParam Integer size, @RequestParam Integer page, OffsetBasedPageRequest pageable) {
        return ResponseMessage.builder()
                .message("모든 상품을 조회했습니다.")
                .data(scheduleService.getAllPresetScheduleList(pageable, size, page))
//                .data(scheduleService.getAllPresetScheduleList(paramiters,pageable, size, page))
                .build();
    }

    @Operation(summary = "식단 생성 및 수정", description = "엑셀로 받은 데이터로 식단을 생성 및 수정합니다.")
    @PostMapping("/excel")
    public ResponseMessage postPresetDailyFoodByExcel(@RequestBody ExcelPresetDailyFoodDto dtoList) {
        scheduleService.makePresetSchedulesByExcel(dtoList);
        return ResponseMessage.builder()
                .message("모든 식단을 요청했습니다.")
                .build();
    }

    @Operation(summary = "추천 식단 생성", description = "추천 데이터를 기반으로 식단을 생성합니다.")
    @GetMapping("/recommends")
    public ResponseMessage getRecommendPresetSchedule(@RequestParam String startDate, @RequestParam Integer size, @RequestParam Integer page, OffsetBasedPageRequest pageable) {
        return ResponseMessage.builder()
                .message("모든 식단을 요청했습니다.")
                .data(scheduleService.getRecommendPresetSchedule(startDate, pageable, size, page))
                .build();
    }

    @Operation(summary = "저장 하기", description = "데이터를 임시 저장 합니다.")
    @PostMapping("/pause")
    public ResponseMessage updateDataInTemporary(@RequestBody ExcelPresetDailyFoodDto dtoList) {
        scheduleService.updateDataInTemporary(dtoList);
        return ResponseMessage.builder()
                .message("데이터를 임시 저장 했습니다.")
                .build();
    }
}