package co.kurrant.app.public_api.controller.food;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.public_api.dto.food.FoodReviewLikeDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.FoodService;
import co.kurrant.app.public_api.service.UserUtil;
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
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate,
                                        @RequestParam(required = false) Integer diningType) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                        .data(foodService.getDailyFood(securityUser, spotId, selectedDate, diningType))
                        .message("식단 불러오기에 성공하였습니다.")
                        .build();
    }

    @Operation(summary = "메뉴 상세정보 불러오기", description = "특정 메뉴의 상세정보를 불러온다.")
    @GetMapping("/{dailyFoodId}")
    public ResponseMessage getFoodDetail(Authentication authentication, @PathVariable BigInteger dailyFoodId){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(foodService.getFoodDetail(dailyFoodId, securityUser))
                .message("상품 상세정보 조회 성공!")
                .build();
    }

    @Operation(summary = "메뉴 할인 정보 불러오기", description = "특정 메뉴의 할인 정보를 불러온다.")
    @GetMapping("/{dailyFoodId}/discount")
    public ResponseMessage getFoodDiscount(Authentication authentication, @PathVariable BigInteger dailyFoodId){
        return ResponseMessage.builder()
                .data(foodService.getFoodDiscount(dailyFoodId))
                .message("상품 상세정보 조회 성공!")
                .build();
    }

    @Operation(summary = "메뉴 상세 리뷰 불러오기", description = "특정 메뉴의 리뷰를 불러온다.")
    @GetMapping("/{dailyFoodId}/review")
    public ResponseMessage getFoodReview(Authentication authentication, @PathVariable BigInteger dailyFoodId,
                                         @RequestParam (required = false) Integer sort,
                                         @RequestParam (required = false) Integer photo,
                                         @RequestParam (required = false) Integer starFilter
                                         ){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        Object foodReview = foodService.getFoodReview(dailyFoodId, securityUser, sort, photo, starFilter);
        String message = "상품 리뷰 조회 성공!";
            //리뷰없을경우 message 내용변경
            if (foodReview.equals("리뷰없음")) message = "등록된 리뷰가 없습니다.";

        return ResponseMessage.builder()
                .data(foodReview)
                .message(message)
                .build();
    }


    @Operation(summary = "메뉴 상세 리뷰 도움이 돼요", description = "리뷰에 도움이 돼요 누르기")
    @PostMapping("/review/like")
    public ResponseMessage foodReviewLike(Authentication authentication, @RequestBody FoodReviewLikeDto foodReviewLikeDto){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        String message = foodService.foodReviewLike(securityUser, foodReviewLikeDto);
        return ResponseMessage.builder()
                .message(message)
                .build();
    }

    @Operation(summary = "도움이 돼요 확인", description = "도움이 돼요 눌렀는지 확인용")
    @GetMapping("/review/like")
    public ResponseMessage foodReviewLikeCheck(Authentication authentication,@RequestParam BigInteger reviewId){
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(foodService.foodReviewLikeCheck(securityUser, reviewId))
                .message("도움이 돼요 눌렀는지 여부 확인")
                .build();
    }

}
