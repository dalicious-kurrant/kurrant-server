package co.kurrant.app.public_api.service;

import co.dalicious.domain.order.dto.OrderCartDto;
import co.kurrant.app.public_api.dto.order.UpdateCartDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

public interface AppOrderService {
    Object findOrderByServiceDate(LocalDate startDate, LocalDate endDate);

    void saveOrderCart(HttpServletRequest httpServletRequest, OrderCartDto orderCartDto);

    Object findCartById(HttpServletRequest httpServletRequest);

    void deleteByUserId(HttpServletRequest httpServletRequest);

    void deleteById(HttpServletRequest httpServletRequest, Integer dailyFoodId);

    void updateByFoodId(HttpServletRequest httpServletRequest, UpdateCartDto updateCartDto);
}
