package co.kurrant.app.public_api.controller.order;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.order.dto.CartDto;
import co.kurrant.app.public_api.dto.order.UpdateCartDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.OrderService;
import co.kurrant.app.public_api.service.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "3. Order")
@RequestMapping(value = "/v1/users")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "유저 주문 정보 가져오기", description = "유저의 주문 정보를 가져온다.")
    @GetMapping("/me/order")
    public Object userOrderbyDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ResponseMessage.builder()
                .data(orderService.findOrderByServiceDate(startDate, endDate))
                .message("주문 불러오기에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "장바구니 담기", description = "장바구니 생성 및 담기, 중복될 경우 수량+1")
    @PostMapping("/me/order/cart")
    public ResponseMessage saveOrderCart(Authentication authentication,
                                         @RequestBody CartDto cartDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        Integer result = orderService.saveOrderCart(securityUser, cartDto);
        if (result.equals(1)) return ResponseMessage.builder().message("장바구니에 상품을 추가했습니다.").build();
        if (result.equals(2)) return ResponseMessage.builder().message("장바구니에 같은 상품이 있어 수량을 추가했습니다.").build();
        return ResponseMessage.builder().message("아무일도 일어나지 않았습니다.").build();
    }

    @Operation(summary = "장바구니 조회", description = "장바구니를 조회한다.")
    @GetMapping("/me/order/cart")
    public Object getCartItem(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(orderService.findUserCart(securityUser))
                .message("장바구니 불러오기에 성공하였습니다.")
                .build();
    }

    @DeleteMapping("/me/order/cart/{cartDailyFoodId}")
    public ResponseMessage deleteById(Authentication authentication, @PathVariable BigInteger cartDailyFoodId) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        orderService.deleteById(securityUser, cartDailyFoodId);
        return ResponseMessage.builder()
                .message("장바구니의 상품을 삭제했습니다.")
                .build();
    }

    @Operation(summary = "장바구니 전체 삭제", description = "장바구니에 담긴 모든 항목을 삭제한다.")
    @DeleteMapping("/me/order/cart/all")
    public ResponseMessage deleteByUserId(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        orderService.deleteByUserId(securityUser);
        return ResponseMessage.builder()
                .message("장바구니의 모든 상품을 삭제했습니다.")
                .build();
    }

    @Operation(summary = "장바구니 수량 수정", description = "장바구니 수량을 수정한다.")
    @PatchMapping("/me/order/cart")
    public ResponseMessage updateByFoodId(Authentication authentication, @RequestBody UpdateCartDto updateCartDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        orderService.updateByFoodId(securityUser, updateCartDto);
        return ResponseMessage.builder()
                .message("장바구니의 상품 수량이 수정됐습니다.")
                .build();
    }

}
