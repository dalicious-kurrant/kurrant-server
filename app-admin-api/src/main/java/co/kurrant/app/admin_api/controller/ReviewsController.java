package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.review.dto.CommentReqDto;
import co.kurrant.app.admin_api.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Map;

@Tag(name = "5. Review")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/reviews")
@RestController
public class ReviewsController {

    public final ReviewService reviewService;

    @Operation(summary = "리뷰 조회", description = "리뷰를 조회 합니다.")
    @GetMapping("/all")
    public ResponseMessage getAllReviews(@RequestParam Map<String, Object> parameters,
                                         @RequestParam Integer limit, @RequestParam Integer page, OffsetBasedPageRequest pageable) {
        return ResponseMessage.builder()
                .message("리뷰를 조회했습니다.")
                .data(reviewService.getAllReviews(parameters, limit, page, pageable))
                .build();
    }

    @Operation(summary = "리뷰 상세 조회", description = "리뷰를 조회 합니다.")
    @GetMapping("")
    public ResponseMessage getReviewsDetail(@RequestParam BigInteger reviewId) {
        return ResponseMessage.builder()
                .message("리뷰의 상세 내용을 조회했습니다.")
                .data(reviewService.getReviewsDetail(reviewId))
                .build();
    }

    @Operation(summary = "관리자 댓글 생성", description = "리뷰에 관리자 댓글을 작성합니다.")
    @PostMapping("")
    public ResponseMessage createAdminComment(@RequestBody CommentReqDto reqDto, @RequestParam BigInteger reviewId) {
        reviewService.createAdminComment(reqDto, reviewId);
        return ResponseMessage.builder()
                .message("관리자 댓글 작성에 성공했습니다.")
                .build();
    }

    @Operation(summary = "관리자 댓글 수정", description = "리뷰에 관리자 댓글을 수정합니다.")
    @PatchMapping("")
    public ResponseMessage updateAdminComment(@RequestBody CommentReqDto reqDto, @RequestParam BigInteger commentId) {
        reviewService.updateAdminComment(reqDto, commentId);
        return ResponseMessage.builder()
                .message("관리자 댓글 수정에 성공했습니다.")
                .build();
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제 상태로 변경합니다.")
    @PatchMapping("/delete")
    public ResponseMessage deleteReview(@RequestParam BigInteger reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseMessage.builder()
                .message("리뷰 삭제에 성공했습니다.")
                .build();
    }

    @Operation(summary = "리뷰 신고", description = "리뷰를 신고 상태로 변경합니다.")
    @PatchMapping("/report")
    public ResponseMessage reportReview(@RequestParam BigInteger reviewId) {
        reviewService.reportReview(reviewId);
        return ResponseMessage.builder()
                .message("리뷰 신고에 성공했습니다.")
                .build();
    }
}
