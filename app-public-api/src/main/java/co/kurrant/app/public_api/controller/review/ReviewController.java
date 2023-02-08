package co.kurrant.app.public_api.controller.review;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.public_api.dto.review.ReviewDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.ReviewService;
import co.kurrant.app.public_api.service.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "v1/users/me/reviews")
public class ReviewController {

    public final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "리뷰 작성 하기")
    @PostMapping("")
    public ResponseMessage createReview(Authentication authentication, ReviewDto reviewDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("리뷰 작성을 완료했습니다.")
                .data(reviewService.createReview(securityUser, reviewDto))
                .build();
    }
}
