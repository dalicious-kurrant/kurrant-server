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

@Tag(name = "6. Group")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/users/me/orders")
@RestController
public class OrderDailyFoodController {
    private final OrderDailyFoodService orderDailyFoodService;
    @Operation(summary = "유저 주문 정보 가져오기", description = "유저의 주문 정보를 가져온다.")
    @GetMapping("")
    public ResponseMessage userOrderByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ResponseMessage.builder()
                .data(orderDailyFoodService.findOrderByServiceDate(startDate, endDate))
                .message("주문 불러오기에 성공하였습니다.")
                .build();
    }

    @PostMapping("/{spotId}")
    public ResponseMessage userOrderByDate(Authentication authentication, @PathVariable BigInteger spotId, @RequestBody OrderItemDailyFoodReqDto orderItemDailyFoodReqDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        orderDailyFoodService.orderDailyFoods(securityUser, orderItemDailyFoodReqDto, spotId);
        return ResponseMessage.builder()
                .message("주문 불러오기에 성공하였습니다.")
                .build();
    }
}
