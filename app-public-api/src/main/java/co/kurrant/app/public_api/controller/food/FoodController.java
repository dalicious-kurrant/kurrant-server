package co.kurrant.app.public_api.controller.food;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.controller.food.service.FoodService;
import co.kurrant.app.public_api.controller.food.service.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDate;

@Tag(name = "4. Food")
@RequestMapping(value = "/v1/dailyfoods")
@RestController
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @Operation(summary = "식단 불러오기", description = "특정스팟의 원하는 날짜의 식단을 조회한다.")
    @GetMapping("")
    public ResponseMessage getDailyFood(Authentication authentication,
                                        @RequestParam BigInteger spotId,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                        .data(foodService.getDailyFood(securityUser, spotId, selectedDate))
                        .message("식단 불러오기에 성공하였습니다.")
                        .build();
    }

    @Operation(summary = "메뉴 상세정보 불러오기", description = "특정 메뉴의 상세정보를 불러온다.")
    @GetMapping("/{dailyfoodId}")
    public ResponseMessage getFoodDetail(Authentication authentication, @PathVariable BigInteger dailyfoodId){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(foodService.getFoodDetail(dailyfoodId, securityUser))
                .message("상품 상세정보 조회 성공!")
                .build();
    }
}
