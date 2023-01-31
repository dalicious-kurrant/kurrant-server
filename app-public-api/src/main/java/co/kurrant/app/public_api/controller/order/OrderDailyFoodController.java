package co.kurrant.app.public_api.controller.order;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.order.dto.OrderItemDailyFoodReqDto;
import co.kurrant.app.public_api.service.OrderDailyFoodService;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDate;

@Tag(name = "3. Order")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/users/me/orders")
@RestController
public class OrderDailyFoodController {
    private final OrderDailyFoodService orderDailyFoodService;
    @Operation(summary = "유저 주문 정보 가져오기", description = "유저의 주문 정보를 가져온다.")
    @GetMapping("")
    public ResponseMessage userOrderByDate(Authentication authentication,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(orderDailyFoodService.findOrderByServiceDate(securityUser, startDate, endDate))
                .message("주문 불러오기에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "정기 식사 주문하기", description = "정기 식사를 구매한다.")
    @PostMapping("/{spotId}")
    public ResponseMessage userOrderByDate(Authentication authentication, @PathVariable BigInteger spotId, @RequestBody OrderItemDailyFoodReqDto orderItemDailyFoodReqDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        orderDailyFoodService.orderDailyFoods(securityUser, orderItemDailyFoodReqDto, spotId);
        return ResponseMessage.builder()
                .message("식사 주문에 성공하였습니다.")
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

    @Operation(summary = "정기식사 구매내역 상세", description = "정기 식사 구매내역 상세를 가져온다.")
    @GetMapping("/{orderId}")
    public ResponseMessage userOrderDailyFoodDetail(Authentication authentication, @PathVariable BigInteger orderId) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(orderDailyFoodService.getOrderDailyFoodDetail(securityUser, orderId))
                .message("정기식사 구매 내역 조회에 성공하였습니다.")
                .build();
    }
}
