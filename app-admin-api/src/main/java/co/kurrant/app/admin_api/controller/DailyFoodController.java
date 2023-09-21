package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.core.enums.ControllerType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import co.dalicious.domain.food.dto.FoodDto;
import co.kurrant.app.admin_api.dto.UpdateStatusAndIdListDto;
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

    @ControllerMarker(ControllerType.DAILY_FOOD)
    @Operation(summary = "식단 승인", description = "식사 예정 날짜를 기준으로 식단은 승인한다.")
    @PostMapping("/approval")
    public ResponseMessage approveSchedule(@RequestBody PeriodDto.PeriodStringDto periodDto) {
        dailyFoodService.approveSchedule(periodDto);
        return ResponseMessage.builder()
                .message(periodDto.getStartDate() + " ~ " + periodDto.getEndDate() + "사이의 식단을 승인했습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.DAILY_FOOD)
    @Operation(summary = "그룹과 메이커스 조회", description = "그룹과 메이커스 리스트를 조회한다.")
    @GetMapping("/groupsAndMakers")
    public ResponseMessage getGroupAndMakers() {
        return ResponseMessage.builder()
                .data(dailyFoodService.getGroupAndMakers())
                .message("식단 조회에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.DAILY_FOOD)
    @Operation(summary = "식단 조회", description = "")
    @GetMapping("")
    public ResponseMessage getDailyFoods(@RequestParam Map<String, Object> parameters) {
        return ResponseMessage.builder()
                .data(dailyFoodService.getDailyFoods(parameters))
                .message("식단 조회에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.DAILY_FOOD)
    @Operation(summary = "식단 엑셀 저장 및 수정", description = "식단을 엑셀 파일을 통해 수정하거나 저장한다.")
    @PostMapping("/excel")
    public ResponseMessage excelDailyFood(@RequestBody List<FoodDto.DailyFood> dailyFoodList) {
        dailyFoodService.excelDailyFoods(dailyFoodList);
        return ResponseMessage.builder()
                .message("식단 엑셀 저장 및 수정에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.DAILY_FOOD)
    @Operation(summary = "매장 식사 식단 생성", description = "매장 식사 타입에 포함된 모든 스팟에 식단을 추가한다.")
    @PostMapping("/eat-in")
    public ResponseMessage generateEatInDailyFood(@RequestParam String startDate,
                                                  @RequestParam String endDate) {
        dailyFoodService.generateEatInDailyFood(DateUtils.stringToDate(startDate), DateUtils.stringToDate(endDate));
        return ResponseMessage.builder()
                .message("식단 엑셀 저장 및 수정에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.DAILY_FOOD)
    @Operation(summary = "식단 상태 일괄 변경", description = "식단 상태를 요청한 상태로 일괄로 변경합니다.")
    @PatchMapping("/status")
    public ResponseMessage updateAllDailyFoodStatus(@RequestBody UpdateStatusAndIdListDto requestDto) {
        dailyFoodService.updateAllDailyFoodStatus(requestDto);
        return ResponseMessage.builder()
                .message("식단의 상태 일괄 변경에 성공하였습니다.")
                .build();
    }
}
