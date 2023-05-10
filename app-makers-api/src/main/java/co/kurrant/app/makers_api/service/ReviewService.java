package co.kurrant.app.makers_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.review.dto.CommentReqDto;
import co.dalicious.domain.review.dto.ReviewMakersResDto;
import co.kurrant.app.makers_api.model.SecurityUser;

import java.math.BigInteger;


public interface ReviewService {
    // 댓글 작성
    void createMakersComment(BigInteger reviewId, CommentReqDto reqDto);
    // 리뷰 조회 - 미답변 리뷰 조회, 삭제 제외
    ItemPageableResponseDto<ReviewMakersResDto> getUnansweredReview(SecurityUser securityUser, String foodName, Integer limit, Integer page, OffsetBasedPageRequest pageable);
    //리뷰 조히 - 전체 조회, 삭제 제외
    ItemPageableResponseDto<ReviewMakersResDto> getAllReview(SecurityUser securityUser, String foodName, Integer limit, Integer page, OffsetBasedPageRequest pageable);
    // 리뷰 상세 조회
    ReviewMakersResDto.ReviewDetail getReviewDetail(BigInteger reviewId);
    // 댓글 수정
    void updateMakersComment(BigInteger commentId, CommentReqDto reqDto);
    // 댓글 삭제
//    void deleteMakersComment(BigInteger commentId);
    // 리뷰 신고
    void reportReviews(BigInteger reviewId);
}
