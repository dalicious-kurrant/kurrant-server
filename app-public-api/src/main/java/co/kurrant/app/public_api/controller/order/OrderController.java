package co.kurrant.app.public_api.controller.order;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.order.dto.OrderCartDto;
import co.kurrant.app.public_api.dto.order.UpdateCartDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.time.LocalDate;

@Tag(name = "3. Order")
@RequestMapping(value = "/v1/users")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/me/order")
    public Object userOrderbyDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate){
        return ResponseMessage.builder()
                .data(orderService.findOrderByServiceDate(startDate, endDate))
                .message("주문 불러오기에 성공하였습니다.")
                .build();
    }

    @PostMapping("/me/order/cart")
    public ResponseMessage saveOrderCart(Authentication authentication,
                                         @RequestBody OrderCartDto orderCartDto){
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        Integer result = orderService.saveOrderCart(securityUser, orderCartDto);
        if (result.equals(1)) return ResponseMessage.builder().message("장바구니에 상품을 추가했습니다.").build();
        if (result.equals(2)) return ResponseMessage.builder().message("장바구니에 같은 상품이 있어 수량을 추가했습니다.").build();
        return ResponseMessage.builder().message("아무일도 일어나지 않았습니다.").build();
    }

    @GetMapping("/me/order/cart")
    public Object getCartItem(Authentication authentication){
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return ResponseMessage.builder()
                .data(orderService.findCartById(securityUser))
                .message("장바구니 불러오기에 성공하였습니다.")
                .build();
    }

    @DeleteMapping("/me/order/cart/{dailyFoodId}")
    public ResponseMessage deleteById(Authentication authentication,@PathVariable Integer dailyFoodId) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        orderService.deleteById(securityUser, dailyFoodId);
        return ResponseMessage.builder()
                .message("장바구니의 상품을 삭제했습니다.")
                .build();
    }

    @DeleteMapping("/me/order/cart/all")
    public ResponseMessage deleteByUserId(SecurityUser securityUser) {
        orderService.deleteByUserId(securityUser);
        return ResponseMessage.builder()
                .message("장바구니의 모든 상품을 삭제했습니다.")
                .build();
    }

    @PatchMapping("/me/order/cart")
    public ResponseMessage updateByFoodId(
            Authentication authentication,
            @RequestBody UpdateCartDto updateCartDto){
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        orderService.updateByFoodId(securityUser, updateCartDto);
        return ResponseMessage.builder()
                .message("장바구니의 상품 수량이 수정됐습니다.")
                .build();
    }

}
