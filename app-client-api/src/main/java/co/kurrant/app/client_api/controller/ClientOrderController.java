package co.kurrant.app.client_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.client_api.model.SecurityUser;
import co.kurrant.app.client_api.service.ClientOrderService;
import co.kurrant.app.client_api.util.UserUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
}
