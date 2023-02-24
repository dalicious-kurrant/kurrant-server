package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.admin_api.dto.OrderDto;
import co.kurrant.app.admin_api.service.OrderDailyFoodService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

@Tag(name = "2. Order")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/orders")
@RestController
public class OrderDailyFoodController {
    private final OrderDailyFoodService orderDailyFoodService;
    @GetMapping("")
    public ResponseMessage retrieveOrder(@RequestParam Map<String, Object> parameters) {
        return ResponseMessage.builder()
                .data(orderDailyFoodService.retrieveOrder(parameters))
                .message("주문 조회에 성공하였습니다.")
                .build();
    }

    @GetMapping("/by/makers")
    public ResponseMessage retrieveOrderCountByMakers(@RequestParam Map<String, Object> parameters) {
        return ResponseMessage.builder()
                .data(orderDailyFoodService.retrieveOrderByMakers(parameters))
                .message("메이커스별 식수 조회에 성공하였습니다.")
                .build();
    }

    @GetMapping("/{orderCode}")
    public ResponseMessage getOrderDetail(@PathVariable String orderCode) {
        return ResponseMessage.builder()
                .data(orderDailyFoodService.getOrderDetail(orderCode))
                .message("주문 상세 조회에 성공하였습니다.")
                .build();
    }

    @GetMapping("/group")
    public ResponseMessage getGroup(@RequestParam(required = false) Integer clientType) {
        return ResponseMessage.builder()
                .data(orderDailyFoodService.getGroup(clientType))
                .message("고객사 조회에 성공하였습니다.")
                .build();
    }

    @GetMapping("/makers")
    public ResponseMessage getMakers() {
        return ResponseMessage.builder()
                .data(orderDailyFoodService.getMakers())
                .message("메이커스 조회에 성공하였습니다.")
                .build();
    }

    @GetMapping("/groupInfo")
    public ResponseMessage getGroupInfo(@RequestParam (required = false) BigInteger groupId) {
        return ResponseMessage.builder()
                .data(orderDailyFoodService.getGroupInfo(groupId))
                .message("고객사 정보 조회에 성공하였습니다.")
                .build();
    }

    @PostMapping("/cancel")
    public ResponseMessage cancelOrder(@RequestBody OrderDto.Id id) throws IOException, ParseException {
        orderDailyFoodService.cancelOrder(id.getId());
        return ResponseMessage.builder()
                .message("부분 주문 취소를 성공했습니다.")
                .build();
    }

    @PostMapping("/orderItems/cancel")
    public ResponseMessage cancelOrderItem(@RequestBody OrderDto.IdList idList) throws IOException, ParseException {
        orderDailyFoodService.cancelOrderItems(idList.getIdList());
        return ResponseMessage.builder()
                .message("주문 전체 취소를 성공했습니다.")
                .build();
    }
}


