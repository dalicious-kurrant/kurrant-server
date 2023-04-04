package co.kurrant.app.client_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.order.dto.ExtraOrderDto;
import co.dalicious.domain.order.dto.OrderDto;
import co.kurrant.app.client_api.model.SecurityUser;
import co.kurrant.app.client_api.service.ClientOrderService;
import co.kurrant.app.client_api.service.GroupService;
import co.kurrant.app.client_api.util.UserUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "5. Order")
@RequestMapping(value = "/v1/orders")
@RestController
@RequiredArgsConstructor
public class ClientOrderController {
    private final ClientOrderService clientOrderService;

    @GetMapping("/info")
    public ResponseMessage getGroupInfo(Authentication authentication, @RequestParam Map<String, Object> parameters) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(clientOrderService.getGroupInfo(securityUser, parameters))
                .message("고객사 정보 조회에 성공하였습니다.")
                .build();
    }

    @GetMapping("")
    public ResponseMessage getOrders(Authentication authentication, @RequestParam Map<String, Object> parameters) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(clientOrderService.getOrder(securityUser, parameters))
                .message("주문 조회에 성공하였습니다.")
                .build();
    }

    @GetMapping("/{orderCode}")
    public ResponseMessage getOrders(Authentication authentication, @PathVariable String orderCode) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(clientOrderService.getOrderDetail(securityUser, orderCode))
                .message("주문 상세 조회에 성공하였습니다.")
                .build();
    }

    @GetMapping("/statistic")
    public ResponseMessage getOrderStatistic(Authentication authentication, @RequestParam Map<String, Object> parameters) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(clientOrderService.getOrderStatistic(securityUser, parameters))
                .message("주문 통계 조회에 성공하였습니다.")
                .build();
    }

    @GetMapping("/extra")
    public ResponseMessage getExtraOrders(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(clientOrderService.getExtraOrders(securityUser))
                .message("추가 주문 조회에 성공하였습니다.")
                .build();
    }

    @GetMapping("/extra/dailyFoods")
    public ResponseMessage getExtraDailyFoods(Authentication authentication,
                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(clientOrderService.getExtraDailyFoods(securityUser, startDate, endDate))
                .message("추가 주문 식단 조회에 성공하였습니다.")
                .build();
    }

    @PostMapping("/extra")
    public ResponseMessage postExtraOrderItems(Authentication authentication,
                                               @RequestBody List<ExtraOrderDto.Request> orderDtos) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        clientOrderService.postExtraOrderItems(securityUser, orderDtos);
        return ResponseMessage.builder()
                .message("추가 주문에 성공하였습니다.")
                .build();
    }

    @PostMapping("/extra/refund")
    public ResponseMessage refundExtraOrderItems(Authentication authentication,
                                               @RequestBody BigInteger orderId) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        clientOrderService.refundExtraOrderItems(securityUser, orderId);
        return ResponseMessage.builder()
                .message("추가 주문에 성공하였습니다.")
                .build();
    }
}
