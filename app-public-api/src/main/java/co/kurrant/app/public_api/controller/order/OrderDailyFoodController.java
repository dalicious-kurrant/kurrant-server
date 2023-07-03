package co.kurrant.app.public_api.controller.order;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.payment.dto.OrderCreateBillingKeySecondReqDto;
import co.dalicious.domain.order.dto.OrderItemDailyFoodByNiceReqDto;
import co.dalicious.domain.order.dto.OrderItemDailyFoodReqDto;
import co.kurrant.app.public_api.dto.order.IdDto;
import co.kurrant.app.public_api.dto.order.OrderCardQuotaDto;
import co.kurrant.app.public_api.service.OrderDailyFoodService;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "주문")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/users/me/orders")
@RestController
public class OrderDailyFoodController {
    private final OrderDailyFoodService orderDailyFoodService;

    @Operation(summary = "유저 식사 일정 가져오기", description = "유저의 주문 정보를 가져온다.")
    @GetMapping("")
    public ResponseMessage userOrderByDate(Authentication authentication,
                                           @RequestParam(required = false) BigInteger spotId,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(orderDailyFoodService.findOrderByServiceDate(securityUser, spotId, startDate, endDate))
                .message("식사 일정 불러오기에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "정기식사 구매내역", description = "정기 식사 구매내역을 조회한다.")
    @GetMapping("/histories")
    public ResponseMessage userOrderDailyFoodHistory(Authentication authentication,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                     @RequestParam(required = false) Integer orderType) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(orderDailyFoodService.findUserOrderDailyFoodHistory(securityUser, startDate, endDate, orderType))
                .message("정기식사 구매 내역 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "정기식사 구매 상세 내역조회", description = "정기식사 구매 내역 상세를 가져온다..")
    @GetMapping("/{orderId}")
    public ResponseMessage userOrderDailyFoodRefund(Authentication authentication, @PathVariable BigInteger orderId) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(orderDailyFoodService.getOrderDailyFoodDetail(securityUser, orderId))
                .message("정기식사 구매 상세 내역 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "정기식사 전체 환불", description = "정기 식사 구매내역 상세를 가져온다.")
    @PostMapping("/refund")
    public ResponseMessage userOrderDailyFoodDetail(Authentication authentication, @RequestBody IdDto idDto) throws IOException, ParseException {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        orderDailyFoodService.cancelOrderDailyFood(securityUser, idDto.getId());
        return ResponseMessage.builder()
                .message("전체 주문 환불에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "정기식사 부분 환불", description = "주문한 한 정기식사 상품을 환불한다.")
    @PostMapping("/dailyFoods/refund")
    public ResponseMessage userOrderItemDailyFoodRefund(Authentication authentication, @RequestBody IdDto idDto) throws IOException, ParseException {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        orderDailyFoodService.cancelOrderItemDailyFood(securityUser, idDto.getId());
        return ResponseMessage.builder()
                .message("정기식사 부분 환불에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "정기 식사 주문하기(나이스빌링)", description = "정기 식사를 구매한다.")
    @PostMapping("/nice")
    public ResponseMessage userOrderByDateNice(Authentication authentication, @RequestBody OrderItemDailyFoodByNiceReqDto orderItemDailyFoodReqDto) throws IOException, ParseException {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(orderDailyFoodService.orderDailyFoodsNice(securityUser, orderItemDailyFoodReqDto))
                .message("식사 주문에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "정기식사 전체 환불 (나이스)", description = "정기 식사 구매내역 상세를 가져온다.")
    @PostMapping("/refund/nice")
    public ResponseMessage userOrderDailyFoodDetailNice(Authentication authentication, @RequestBody IdDto idDto) throws IOException, ParseException {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        orderDailyFoodService.cancelOrderDailyFoodNice(securityUser, idDto.getId());
        return ResponseMessage.builder()
                .message("전체 주문 환불에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "정기식사 부분 환불 (나이스)", description = "주문한 한 정기식사 상품을 환불한다.")
    @PostMapping("/dailyFoods/refund/nice")
    public ResponseMessage userOrderItemDailyFoodRefundNice(Authentication authentication, @RequestBody IdDto idDto) throws IOException, ParseException {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        orderDailyFoodService.cancelOrderItemDailyFoodNice(securityUser, idDto.getId());
        return ResponseMessage.builder()
                .message("정기식사 부분 환불에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "수령 완료 상태 변경", description = "주문의 상태를 수령 완료 상태로 변경한다.")
    @PatchMapping("/update/status")
    public ResponseMessage changingOrderItemOrderStatus(Authentication authentication, @RequestBody IdDto orderItemId) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        orderDailyFoodService.changingOrderItemOrderStatus(securityUser, orderItemId.getId());
        return ResponseMessage.builder()
                .message("주문의 상태를 수령 완료로 변경했습니다.")
                .build();
    }

    @Operation(summary = "카드할부 처리를 위한 기능(로컬전용)", description = "카드 할부로 결제한다")
    @PostMapping("/card/quota")
    public ResponseMessage OrderCardQuota(Authentication authentication, @RequestBody OrderCardQuotaDto orderCardQuotaDto) throws IOException, ParseException {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(orderDailyFoodService.orderCardQuota(securityUser, orderCardQuotaDto))
                .message("식사 주문에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "정기 식사 주문하기", description = "정기 식사를 구매한다.")
    @PostMapping("")
    public ResponseMessage userOrderByDate(Authentication authentication, @RequestBody OrderItemDailyFoodReqDto orderItemDailyFoodReqDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(orderDailyFoodService.orderDailyFoods(securityUser, orderItemDailyFoodReqDto))
                .message("식사 주문에 성공하였습니다.")
                .build();
    }

}
