package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.annotation.ControllerMarker;
import co.dalicious.client.core.dto.request.LoginTokenDto;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.client.core.enums.ControllerType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.PeriodDto;
import co.kurrant.app.admin_api.dto.Code;
import co.kurrant.app.admin_api.dto.delivery.DeliveryDto;
import co.kurrant.app.admin_api.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    @PostMapping("/login")
    public ResponseMessage login(@RequestBody Code code) throws IOException {
        return ResponseMessage.builder()
                .data(deliveryService.login(code))
                .message("인증에 성공하였습니다")
                .build();
    }


    @ControllerMarker(ControllerType.DELIVERY)
    @Operation(summary = "배송 일정 조회", description = "배송 날짜를 기준으로 배송 일정을 조회한다.")
    @GetMapping("")
    public ResponseMessage getDelivery(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
                                               @RequestParam(required = false) List<BigInteger> groupIds, @RequestParam(required = false) List<BigInteger> spotIds,
                                               @RequestParam(required = false) Integer isAll) {
        return ResponseMessage.builder()
                .message(startDate + " ~ " + endDate + "사이의 배송 현황을 조회했습니다.")
                .data(deliveryService.getDelivery(startDate, endDate, groupIds, spotIds, isAll))
                .build();
    }

    @ControllerMarker(ControllerType.DELIVERY)
    @Operation(summary = "배송 일정 조회", description = "배송 날짜를 기준으로 배송 일정을 조회한다.")
    @GetMapping("/schedules")
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

    @ControllerMarker(ControllerType.DELIVERY)
    @Operation(summary = "기간별 배송 메이커스 조회", description = "기간별 배송하는 메이커스 조회한다")
    @GetMapping("/makers")
    public ResponseMessage getDeliverMakesByDate(@RequestParam Map<String, Object> parameters) {
        return ResponseMessage.builder()
                .message("기간별 배송 메이커스 조회에 성공했습니다.")
                .data(deliveryService.getDeliverMakersByDate(parameters))
                .build();
    }

    @ControllerMarker(ControllerType.DELIVERY)
    @Operation(summary = "기간별 배송 시간 조회", description = "기간별 배송하는 시간 조회한다")
    @GetMapping("/times")
    public ResponseMessage getDeliveryTimesByDate(@RequestParam Map<String, Object> parameters) {
        return ResponseMessage.builder()
                .message("기간별 배송 시간 조회에 성공했습니다.")
                .data(deliveryService.getDeliveryTimesByDate(parameters))
                .build();
    }

    @ControllerMarker(ControllerType.DELIVERY)
    @Operation(summary = "기간별 배송 번호 조회", description = "기간별 배송하는 번호 조회한다")
    @GetMapping("/codes")
    public ResponseMessage getDeliveryCodesByDate(@RequestParam Map<String, Object> parameters) {
        return ResponseMessage.builder()
                .message("기간별 배송 번호 조회에 성공했습니다.")
                .data(deliveryService.getDeliveryCodesByDate(parameters))
                .build();
    }

    @ControllerMarker(ControllerType.DELIVERY)
    @Operation(summary = "기간별 배송 유저 조회", description = "기간별 배송하는 유저 조회한다")
    @GetMapping("/users")
    public ResponseMessage getDeliverUsersByDate(@RequestParam Map<String, Object> parameters) {
        return ResponseMessage.builder()
                .message("기간별 배송 유저 조회에 성공했습니다.")
                .data(deliveryService.getDeliverUsersByDate(parameters))
                .build();
    }
}
