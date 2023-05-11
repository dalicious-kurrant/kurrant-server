package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.review.dto.CommentReqDto;
import co.dalicious.domain.review.dto.ReviewKeywordSaveReqDto;
import co.kurrant.app.admin_api.dto.IdDto;
import co.kurrant.app.admin_api.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @PostMapping("/comment")
    public ResponseMessage createAdminComment(@RequestBody CommentReqDto reqDto, @RequestParam BigInteger reviewId) {
        reviewService.createAdminComment(reqDto, reviewId);
        return ResponseMessage.builder()
                .message("관리자 댓글 작성에 성공했습니다.")
                .build();
    }

    @Operation(summary = "관리자 댓글 수정", description = "리뷰에 관리자 댓글을 수정합니다.")
    @PatchMapping("/comment")
    public ResponseMessage updateAdminComment(@RequestBody CommentReqDto reqDto, @RequestParam BigInteger commentId) {
        reviewService.updateAdminComment(reqDto, commentId);
        return ResponseMessage.builder()
                .message("관리자 댓글 수정에 성공했습니다.")
                .build();
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제 상태로 변경합니다.")
    @PatchMapping("/delete")
    public ResponseMessage deleteReview(@RequestBody IdDto idDto) {
        reviewService.deleteReview(idDto.getId());
        return ResponseMessage.builder()
                .message("리뷰 삭제에 성공했습니다.")
                .build();
    }

    @Operation(summary = "리뷰 신고", description = "리뷰를 신고 상태로 변경합니다.")
    @PatchMapping("/report")
    public ResponseMessage reportReview(@RequestBody IdDto idDto) {
        reviewService.reportReview(idDto.getId());
        return ResponseMessage.builder()
                .message("리뷰 신고에 성공했습니다.")
                .build();
    }

    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/comment/delete")
    public ResponseMessage deleteComment(@RequestBody IdDto idDto) {
        reviewService.deleteComment(idDto.getId());
        return ResponseMessage.builder()
                .message("댓글 삭제를 완료했습니다.")
                .build();
    }

    @Operation(summary = "상품 상세 리뷰 키워드 추가", description = "상품 상세 리뷰에 키워드를 추가합니다.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/keyword")
    public ResponseMessage reviewKeywordSave(@RequestBody ReviewKeywordSaveReqDto keywordDto) {
        reviewService.reviewKeywordSave(keywordDto);
        return ResponseMessage.builder()
                .message("키워드를 추가했습니다.")
                .build();
    }

}
