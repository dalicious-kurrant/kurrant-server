package co.kurrant.app.public_api.service;

import co.dalicious.domain.order.dto.CartDto;
import co.dalicious.domain.order.dto.CartResDto;
import co.kurrant.app.public_api.dto.order.UpdateCartDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.math.BigInteger;
import java.time.LocalDate;

public interface CartService {

    Integer saveOrderCart(SecurityUser securityUser, CartDto cartDto);

    CartResDto findUserCart(SecurityUser securityUser);

    void deleteByCartItemId(SecurityUser securityUser, BigInteger cartItemId);

    void deleteAllCartItemByUserId(SecurityUser securityUser);

    void updateByDailyFoodId(SecurityUser securityUser, UpdateCartDto updateCartDto);
}
