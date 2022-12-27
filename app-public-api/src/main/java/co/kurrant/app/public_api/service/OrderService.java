package co.kurrant.app.public_api.service;

import co.dalicious.domain.order.dto.CartItemDto;
import co.dalicious.domain.order.dto.OrderCartDto;
import co.dalicious.domain.order.dto.OrderDetailDto;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

public interface OrderService {
    OrderDetailDto findOrderByServiceDate(Date startDate, Date endDate);

    void saveOrderCart(HttpServletRequest httpServletRequest, OrderCartDto orderCartDto);

    List<CartItemDto> findCartById(HttpServletRequest httpServletRequest);
}
