package co.kurrant.app.makers_api.controller;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.review.dto.CommentReqDto;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.ReviewService;
import co.kurrant.app.makers_api.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@Tag(name = "Review")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/makers/reviews")
@RestController
public class ReviewsController {
    private final ReviewService reviewService;

    @Operation(summary = "댓글 작성", description = "메이커스 댓글을 작성합니다.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/comment")
    public ResponseMessage createMakersComment(@RequestParam BigInteger reviewId, @RequestBody CommentReqDto reqDto) {
        reviewService.createMakersComment(reviewId, reqDto);
        return ResponseMessage.builder()
                .message("댓글 작성을 완료했습니다.")
                .build();
    }

    @Operation(summary = "미답변 리뷰 조회", description = "아직 답변하지 않은 리뷰를 조회합니다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/pending")
    public ResponseMessage getUnansweredReview(Authentication authentication, @RequestParam Integer limit, @RequestParam Integer page, OffsetBasedPageRequest pageable) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("미답변 리뷰 조회를 완료했습니다.")
                .data(reviewService.getUnansweredReview(securityUser, limit, page, pageable))
                .build();
    }

    @Operation(summary = "리뷰 전체 조회", description = "리뷰를 전체 조회합니다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/all")
    public ResponseMessage getAllReview(Authentication authentication, @RequestParam Integer limit, @RequestParam Integer page, OffsetBasedPageRequest pageable) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("리뷰 조회를 완료했습니다.")
                .data(reviewService.getAllReview(securityUser, limit, page, pageable))
                .build();
    }

    @Operation(summary = "리뷰 상세 조회", description = "리뷰를 상세 조회 합니다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/detail")
    public ResponseMessage getReviewDetail(@RequestParam BigInteger reviewId) {
        return ResponseMessage.builder()
                .message("리뷰 상세 조회를 완료했습니다.")
                .data(reviewService.getReviewDetail(reviewId))
                .build();
    }

    @Operation(summary = "댓글 수정", description = "메이커스 댓글을 수정합니다.")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/comment/update")
    public ResponseMessage updateMakersComment(@RequestParam BigInteger commentId, @RequestBody CommentReqDto reqDto) {
        reviewService.updateMakersComment(commentId, reqDto);
        return ResponseMessage.builder()
                .message("댓글 수정을 완료했습니다.")
                .build();
    }

//    @Operation(summary = "댓글 삭제", description = "메이커스 댓글을 삭제합니다.")
//    @ResponseStatus(HttpStatus.OK)
//    @PatchMapping("/comment/delete")
//    public ResponseMessage deleteMakersComment(@RequestParam BigInteger commentId) {
//        reviewService.deleteMakersComment(commentId);
//        return ResponseMessage.builder()
//                .message("댓글 삭제를 완료했습니다.")
//                .build();
//    }

    @Operation(summary = "리뷰 신고", description = "리뷰를 신고합니다.")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/report")
    public ResponseMessage reportReviews(@RequestParam BigInteger reviewId) {
        reviewService.reportReviews(reviewId);
        return ResponseMessage.builder()
                .message("신고를 완료했습니다.")
                .build();
    }

}
