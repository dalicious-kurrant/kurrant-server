package co.kurrant.app.admin_api.controller.delivery;

import co.dalicious.client.core.dto.response.ResponseMessage;
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
    @PostMapping("")
    public ResponseMessage getDeliverySchedule(@RequestBody PeriodDto.PeriodStringDto periodDto, @RequestParam(required = false) List<BigInteger> groupIds) {
        return ResponseMessage.builder()
                .message(periodDto.getStartDate() + " ~ " + periodDto.getEndDate() + "사이의 식단을 승인했습니다.")
                .data(deliveryService.getDeliverySchedule(periodDto, groupIds))
                .build();
    }
}
