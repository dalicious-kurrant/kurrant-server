package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.domain.review.dto.CommentReqDto;
import co.dalicious.domain.review.dto.ReviewAdminResDto;
import co.dalicious.domain.review.dto.ReviewKeywordSaveReqDto;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface ReviewService {

    // 리뷰 목록 조회 - 삭제 포함
    ItemPageableResponseDto<ReviewAdminResDto> getAllReviews(@RequestParam Map<String, Object> parameters, Integer limit, Integer page, OffsetBasedPageRequest pageable);
    // 리뷰 상세 조회 - 삭제 포함
    ReviewAdminResDto.ReviewDetail getReviewsDetail(BigInteger reviewId);
    // 관리자 댓글 작성 - 삭제 제외
    void createAdminComment(CommentReqDto reqDto, BigInteger reviewId);
    // 관리자 댓글 수정
    void updateAdminComment(CommentReqDto reqDto, BigInteger commentId);
    // 리뷰 삭제 - 삭제 제외
    void deleteReview(BigInteger reviewId);
    // 리뷰 신고 - 삭제 제외
    void reportReview(BigInteger reviewId);
    // 댓글 삭제
    void deleteComment(BigInteger commentId);

    void reviewKeywordSave(ReviewKeywordSaveReqDto keywordDto);

    List<String> foodReviewKeyword(BigInteger foodId);
}
