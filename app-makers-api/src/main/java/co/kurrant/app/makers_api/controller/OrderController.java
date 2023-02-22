package co.kurrant.app.makers_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.OrderDailyFoodService;
import co.kurrant.app.makers_api.util.UserUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Order")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/makers/orders")
@RestController
public class OrderController {
    private final OrderDailyFoodService orderDailyFoodService;
    @GetMapping("")
    public ResponseMessage getOrder(Authentication authentication, @RequestParam Map<String, Object> parameter) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(orderDailyFoodService.getOrder(securityUser, parameter))
                .message("음식별 판매 수량 조회에 성공하였습니다.")
                .build();
    }
}
