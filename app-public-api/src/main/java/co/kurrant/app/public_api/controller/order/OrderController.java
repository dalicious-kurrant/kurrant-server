package co.kurrant.app.public_api.controller.order;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.order.dto.OrderCartDto;
import co.dalicious.domain.user.dto.OrderDetailDto;
import co.kurrant.app.public_api.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Tag(name = "4. Order")
@RequestMapping(value = "/v1/users")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/me/order")
    public OrderDetailDto userOrderbyDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate){
        return orderService.findOrderByServiceDate(startDate, endDate);
    }

    @PostMapping("/me/order/cart")
    public ResponseMessage saveOrderCart(HttpServletRequest httpServletRequest,
                                         @RequestBody OrderCartDto orderCartDto){
        orderService.saveOrderCart(httpServletRequest, orderCartDto);
        return ResponseMessage.builder()
                .message("장바구니에 상품을 추가했습니다.")
                .build();
    }
}
