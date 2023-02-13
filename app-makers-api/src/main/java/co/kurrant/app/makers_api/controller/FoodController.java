package co.kurrant.app.makers_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.FoodService;
import co.kurrant.app.makers_api.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/makers/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @Operation(summary = "상품 전체 조회", description = "존재하는 상품을 모두 조회합니다.")
    @GetMapping("/all")
    public ResponseMessage getAllFoodList() {
        return ResponseMessage.builder()
                .message("모든 상품을 조회했습니다.")
                .data(foodService.getAllFoodList())
                .build();
    }

    @Operation(summary = "메이커스 별 상품 조회", description = "메이커스 별 상품을 모두 조회합니다.")
    @GetMapping("")
    public ResponseMessage getAllFoodListByMakers(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("모든 상품을 조회했습니다.")
                .data(foodService.getAllFoodListByMakers(securityUser))
                .build();
    }
}
