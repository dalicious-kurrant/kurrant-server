package co.kurrant.app.public_api.service;

import co.dalicious.domain.order.dto.CartDto;
import co.dalicious.domain.order.dto.CartResDto;
import co.kurrant.app.public_api.dto.order.UpdateCartDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public interface CartService {

    void saveOrderCart(SecurityUser securityUser, List<CartDto> cartDtoList);

    CartResDto findUserCart(SecurityUser securityUser);

    void deleteByCartItemId(SecurityUser securityUser, BigInteger cartItemId);

    void deleteAllCartItemByUserId(SecurityUser securityUser);

    void updateByDailyFoodId(SecurityUser securityUser, UpdateCartDto updateCartDto);
}
