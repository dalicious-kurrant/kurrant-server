package co.kurrant.app.public_api.service;

import co.dalicious.domain.order.dto.OrderCartDto;
import co.dalicious.domain.user.dto.OrderDetailDto;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public interface OrderService {
    OrderDetailDto findOrderByServiceDate(Date startDate, Date endDate);

    void saveOrderCart(HttpServletRequest httpServletRequest, OrderCartDto orderCartDto);
}
