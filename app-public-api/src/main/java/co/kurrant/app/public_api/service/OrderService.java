package co.kurrant.app.public_api.service;

import co.dalicious.domain.order.dto.CartDto;
import co.kurrant.app.public_api.dto.order.UpdateCartDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.time.LocalDate;

public interface OrderService {
    Object findOrderByServiceDate(LocalDate startDate, LocalDate endDate);

    Integer saveOrderCart(SecurityUser securityUser, CartDto cartDto);

    Object findCartById(SecurityUser securityUser);

    void deleteByUserId(SecurityUser securityUser);

    void deleteById(SecurityUser securityUser, Integer cartItemId);

    void updateByFoodId(UpdateCartDto updateCartDto);
}
