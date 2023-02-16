package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.admin_api.service.OrderDailyFoodService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Map;

@CrossOrigin(origins="*", allowedHeaders = "*")
@Tag(name = "2. Order")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/orders")
@RestController
public class OrderDailyFoodController {
    private final OrderDailyFoodService orderDailyFoodService;
    @GetMapping("")
    public ResponseMessage retrieveOrder(@RequestParam Map<String, Object> parameter) {
        return ResponseMessage.builder()
                .data(orderDailyFoodService.retrieveOrder(parameter))
                .message("주문 조회에 성공하였습니다.")
                .build();
    }

    @GetMapping("/{orderItemDailyFoodId}")
    public ResponseMessage getOrderDetail(@RequestParam BigInteger orderItemDailyFoodId) {
        return ResponseMessage.builder()
//                .data(orderDailyFoodService.getOrderDetail(orderItemDailyFoodId))
                .message("주문 조회에 성공하였습니다.")
                .build();
    }

    @GetMapping("/group")
    public ResponseMessage getGroup(@RequestParam(required = false) Integer clientType) {
        return ResponseMessage.builder()
                .data(orderDailyFoodService.getGroup(clientType))
                .message("고객사 조회에 성공하였습니다.")
                .build();
    }

    @GetMapping("/group/{groupId}")
    public ResponseMessage getGroupInfo(@PathVariable BigInteger groupId) {
        return ResponseMessage.builder()
                .data(orderDailyFoodService.getGroupInfo(groupId))
                .message("고객사 조회에 성공하였습니다.")
                .build();
    }
}
