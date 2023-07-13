package co.kurrant.app.public_api.controller.order;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.order.dto.CartDto;
import co.kurrant.app.public_api.dto.order.UpdateCartDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.CartService;
import co.kurrant.app.public_api.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@Tag(name = "장바구니")
@RequestMapping(value = "/v1/users/me/carts")
@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 담기", description = "장바구니 생성 및 담기")
    @PostMapping("")
    public ResponseMessage saveOrderCart(Authentication authentication,
                                         @RequestBody List<CartDto> cartDtoList) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(cartService.saveOrderCart(securityUser, cartDtoList))
                .message("장바구니 담기에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "장바구니 조회", description = "장바구니를 조회한다.")
    @GetMapping("")
    public ResponseMessage getCartItem(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(cartService.findUserCart(securityUser))
                .message("장바구니 불러오기에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "장바구니 상품 삭제", description = "장바구니의 특정 상품을 삭제한다.")
    @DeleteMapping("/{cartDailyFoodId}")
    public ResponseMessage deleteById(Authentication authentication, @PathVariable BigInteger cartDailyFoodId) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        cartService.deleteByCartItemId(securityUser, cartDailyFoodId);
        return ResponseMessage.builder()
                .message("장바구니의 상품을 삭제했습니다.")
                .build();
    }

    @Operation(summary = "장바구니 전체 삭제", description = "장바구니에 담긴 모든 항목을 삭제한다.")
    @DeleteMapping("/spots/{spotId}")
    public ResponseMessage deleteByUserId(Authentication authentication, @PathVariable BigInteger spotId) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        cartService.deleteAllSpotCartItemByUserId(securityUser, spotId);
        return ResponseMessage.builder()
                .message("해당 스팟 장바구니의 모든 상품을 삭제했습니다.")
                .build();
    }

    @Operation(summary = "장바구니 수량 수정", description = "장바구니 수량을 수정한다.")
    @PatchMapping("")
    public ResponseMessage updateByFoodId(Authentication authentication, @RequestBody UpdateCartDto updateCartDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        cartService.updateByDailyFoodId(securityUser, updateCartDto);
        return ResponseMessage.builder()
                .message("장바구니의 상품 수량이 수정됐습니다.")
                .build();
    }

}
