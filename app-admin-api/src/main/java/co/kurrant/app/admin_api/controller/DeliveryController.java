package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.core.enums.ControllerType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import co.kurrant.app.admin_api.dto.delivery.DeliveryDto;
import co.kurrant.app.admin_api.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @ControllerMarker(ControllerType.DELIVERY)
    @Operation(summary = "배송 일정 조회", description = "배송 날짜를 기준으로 배송 일정을 조회한다.")
    @GetMapping("")
    public ResponseMessage getDeliverySchedule(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
                                               @RequestParam(required = false) List<BigInteger> groupIds, @RequestParam(required = false) List<BigInteger> spotIds,
                                               @RequestParam(required = false) Integer isAll) {
        return ResponseMessage.builder()
                .message(startDate + " ~ " + endDate + "사이의 배송 현황을 조회했습니다.")
                .data(deliveryService.getDeliverySchedule(startDate, endDate, groupIds, spotIds, isAll))
                .build();
    }

    @ControllerMarker(ControllerType.DELIVERY)
    @Operation(summary = "배송 업체 일정 조회", description = "배송 업체 일정을 조회한다.")
    @GetMapping("/drivers")
    public ResponseMessage getDeliveryForDriver(@RequestParam Map<String, Object> parameters) {
        return ResponseMessage.builder()
                .message("배송 업체 배송일정 조회에 성공하였습니다.")
                .data(deliveryService.getDeliveryManifest(parameters))
                .build();
    }
}
