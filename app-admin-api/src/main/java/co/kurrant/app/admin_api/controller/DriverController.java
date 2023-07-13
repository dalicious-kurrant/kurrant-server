package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.core.enums.ControllerType;
import co.dalicious.domain.order.dto.OrderDto;
import co.kurrant.app.admin_api.dto.delivery.DriverDto;
import co.kurrant.app.admin_api.dto.delivery.ScheduleDto;
import co.kurrant.app.admin_api.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/driver")
public class DriverController {
    private final DriverService driverService;
    @ControllerMarker(ControllerType.DRIVER)
    @Operation(summary = "배송 기사 조회", description = "배송 기사를 조회한다.")
    @GetMapping("")
    public ResponseMessage getDrivers() {
        return ResponseMessage.builder()
                .data(driverService.getDrivers())
                .message("배송 기사 조회에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.DRIVER)
    @Operation(summary = "배송 기사 추가", description = "배송 기사를 추가한다.")
    @PostMapping("")
    public ResponseMessage postDrivers(@RequestBody List<DriverDto> driverDtos) {
        driverService.postDrivers(driverDtos);
        return ResponseMessage.builder()
                .message("배송 기사 추가에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.DRIVER)
    @Operation(summary = "배송 기사 삭제", description = "배송 기사를 삭제한다.")
    @DeleteMapping("")
    public ResponseMessage deleteDrivers(@RequestBody OrderDto.IdList idList) {
        return ResponseMessage.builder()
                .message("배송 기사 삭제에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.DRIVER)
    @Operation(summary = "배송 기사 일정 조회", description = "배송 기사 일정을 조회한다.")
    @GetMapping("/schedules")
    public ResponseMessage getDriverSchedule(@RequestParam Map<String, Object> parameters) {
        return ResponseMessage.builder()
                .message("배송 기사 일정을 조회에 성공하였습니다.")
                .build();
    }

    @ControllerMarker(ControllerType.DRIVER)
    @Operation(summary = "배송 기사 일정 엑셀 추가", description = "배송 기사 일정을 엑셀 추가한다.")
    @PostMapping("/schedules")
    public ResponseMessage excelDriverSchedule(@RequestBody List<ScheduleDto> scheduleDtos) {
        return ResponseMessage.builder()
                .message("배송 기사 일정을 엑셀 추가에 성공하였습니다.")
                .build();
    }
}
