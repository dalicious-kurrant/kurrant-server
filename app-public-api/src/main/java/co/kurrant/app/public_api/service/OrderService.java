package co.kurrant.app.public_api.service;

import co.dalicious.domain.order.dto.CartDto;
import co.kurrant.app.public_api.dto.order.UpdateCartDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.math.BigInteger;
import java.time.LocalDate;

public interface OrderService {
    Object findOrderByServiceDate(LocalDate startDate, LocalDate endDate);

    Integer saveOrderCart(SecurityUser securityUser, CartDto cartDto);

    Object findUserCart(SecurityUser securityUser);

    void deleteById(SecurityUser securityUser, BigInteger cartItemId);

    void deleteByUserId(SecurityUser securityUser);

    void updateByFoodId(UpdateCartDto updateCartDto);
}
