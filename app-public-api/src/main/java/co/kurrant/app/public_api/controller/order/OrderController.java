package co.kurrant.app.public_api.controller.order;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.order.dto.OrderCartDto;
import co.kurrant.app.public_api.dto.order.UpdateCartDto;
import co.kurrant.app.public_api.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public ResponseMessage saveOrderCart(HttpServletRequest httpServletRequest,
                                         @RequestBody OrderCartDto orderCartDto){
        orderService.saveOrderCart(httpServletRequest, orderCartDto);
        return ResponseMessage.builder()
                .message("장바구니에 상품을 추가했습니다.")
                .build();
    }

    @GetMapping("/me/order/cart")
    public Object getCartItem(HttpServletRequest httpServletRequest){
        return ResponseMessage.builder()
                .data(orderService.findCartById(httpServletRequest))
                .message("장바구니 불러오기에 성공하였습니다.")
                .build();
    }

    @DeleteMapping("/me/order/cart/{dailyFoodId}")
    public ResponseMessage deleteById(HttpServletRequest httpServletRequest,@PathVariable Integer dailyFoodId) {
        orderService.deleteById(httpServletRequest, dailyFoodId);
        return ResponseMessage.builder()
                .message("장바구니의 상품을 삭제했습니다.")
                .build();
    }

    @DeleteMapping("/me/order/cart/all")
    public ResponseMessage deleteByUserId(HttpServletRequest httpServletRequest) {
        orderService.deleteByUserId(httpServletRequest);
        return ResponseMessage.builder()
                .message("장바구니의 모든 상품을 삭제했습니다.")
                .build();
    }

    @PatchMapping("/me/order/cart")
    public ResponseMessage updateByFoodId(
            HttpServletRequest httpServletRequest,
            @RequestBody UpdateCartDto updateCartDto){
        orderService.updateByFoodId(httpServletRequest, updateCartDto);
        return ResponseMessage.builder()
                .message("장바구니의 상품 수량이 수정됐습니다.")
                .build();
    }

}
