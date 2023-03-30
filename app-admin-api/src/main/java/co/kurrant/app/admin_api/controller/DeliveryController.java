package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import co.kurrant.app.admin_api.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Operation(summary = "배송 일정 조회", description = "배송 날짜를 기준으로 배송 일정을 조회한다.")
    @GetMapping("")
    public ResponseMessage getDeliverySchedule(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
                                               @RequestParam(required = false) List<BigInteger> groupIds, @RequestParam(required = false) List<BigInteger> spotIds,
                                               @RequestParam(required = false) String isDefault) {
        return ResponseMessage.builder()
                .message(startDate + " ~ " + endDate + "사이의 배송 현황을 조회했습니다.")
                .data(deliveryService.getDeliverySchedule(startDate, endDate, groupIds, spotIds, isDefault))
                .build();
    }
}
