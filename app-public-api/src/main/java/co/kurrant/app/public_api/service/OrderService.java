package co.kurrant.app.public_api.service;

import co.dalicious.domain.order.dto.CartItemDto;
import co.dalicious.domain.order.dto.OrderCartDto;
import co.dalicious.domain.order.dto.OrderDetailDto;
import co.kurrant.app.public_api.dto.order.UpdateCartDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface OrderService {
    List<OrderDetailDto> findOrderByServiceDate(LocalDate startDate, LocalDate endDate);

    void saveOrderCart(HttpServletRequest httpServletRequest, OrderCartDto orderCartDto);

    List<CartItemDto> findCartById(HttpServletRequest httpServletRequest);

    void deleteByUserId(HttpServletRequest httpServletRequest);

    void deleteById(HttpServletRequest httpServletRequest, Integer dailyFoodId);

    void updateByFoodId(HttpServletRequest httpServletRequest, UpdateCartDto updateCartDto);
}
