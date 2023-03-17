package co.kurrant.app.makers_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.review.dto.CommentReqDto;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.ReviewService;
import co.kurrant.app.makers_api.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@Tag(name = "Review")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/makers/reviews")
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "댓글 작성", description = "메이커스 댓글을 작성합니다.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("")
    public ResponseMessage createMakersComment(@RequestParam BigInteger reviewId, @RequestBody CommentReqDto reqDto) {
        reviewService.createMakersComment(reviewId, reqDto);
        return ResponseMessage.builder()
                .message("댓글 작성을 완료했습니다.")
                .build();
    }

    @Operation(summary = "미답변 리뷰 조회", description = "아직 답변하지 않은 리뷰를 조회합니다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/pending")
    public ResponseMessage getUnansweredReview(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("미답변 리뷰 조회를 완료했습니다.")
                .data(reviewService.getUnansweredReview(securityUser))
                .build();
    }

    @Operation(summary = "리뷰 전체 조회", description = "리뷰를 전체 조회합니다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/all")
    public ResponseMessage getAllReview() {
        return ResponseMessage.builder()
                .message("리뷰 조회를 완료했습니다.")
                .data(reviewService.getAllReview())
                .build();
    }

    @Operation(summary = "리뷰 상세 조회", description = "리뷰를 상세 조회 합니다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/detail")
    public ResponseMessage getReviewDetail(Authentication authentication, @RequestParam BigInteger reviewId) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("리뷰 상세 조회를 완료했습니다.")
                .data(reviewService.getReviewDetail(securityUser, reviewId))
                .build();
    }

    @Operation(summary = "상품 평균 리뷰 점수", description = "상품 평균 리뷰 점수를 조회 합니다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/score")
    public ResponseMessage getAverageReviewScore(Authentication authentication, @RequestParam BigInteger reviewId) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("리뷰 상세 조회를 완료했습니다.")
                .data(reviewService.getAverageReviewScore(securityUser, reviewId))
                .build();
    }
}
