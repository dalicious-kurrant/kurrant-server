package co.kurrant.app.public_api.controller.review;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.review.dto.ReviewReqDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.ReviewService;
import co.kurrant.app.public_api.service.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "v1/users/me/reviews")
public class ReviewController {

    public final ReviewService reviewService;

    // 수정 필요. dailyfood id로 검색할 경우 product가 들어욌을 때 애매
    @Operation(summary = "리뷰 작성", description = "리뷰 작성 하기")
    @PostMapping("")
    public ResponseMessage createReview(Authentication authentication, @RequestBody ReviewReqDto reviewDto, @RequestParam BigInteger itemId) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("리뷰 작성을 완료했습니다.")
                .data(reviewService.createReview(securityUser, reviewDto, itemId))
                .build();
    }

    @Operation(summary = "리뷰 작성 상품 조회", description = "리뷰 작성 가능 상품 조회")
    @GetMapping("/items")
    public ResponseMessage getOrderItemForReview(Authentication authentication) throws ParseException {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("리뷰 작성이 가능한 상품을 조회했습니다.")
                .data(reviewService.getOrderItemForReview(securityUser))
                .build();
    }

    @Operation(summary = "리뷰 조회", description = "작성한 리뷰 조회")
    @GetMapping("")
    public ResponseMessage getReviewsForUser(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("작성한 리뷰를 불러왔습니다.")
                .data(reviewService.getReviewsForUser(securityUser))
                .build();
    }
}