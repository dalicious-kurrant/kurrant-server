package co.kurrant.app.makers_api.service;

import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.review.dto.CommentReqDto;
import co.dalicious.domain.review.dto.ReviewMakersResDto;
import co.kurrant.app.makers_api.model.SecurityUser;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;


public interface ReviewService {
    // 댓글 작성
    void createMakersComment(BigInteger reviewId, CommentReqDto reqDto);
    // 리뷰 조회 - 미답변 리뷰 조회, 삭제 제외
    ListItemResponseDto<ReviewMakersResDto.ReviewListDto> getUnansweredReview(SecurityUser securityUser);
    //리뷰 조히 - 전체 조회, 삭제 제외
    ListItemResponseDto<ReviewMakersResDto.ReviewListDto> getAllReview();
    // 리뷰 상세 조회
    ReviewMakersResDto.ReviewListDto getReviewDetail(SecurityUser securityUser, BigInteger reviewId);
    // 상품 평규 리뷰 점수 조회 - 삭제와 신고 리뷰 점수 포함
    ReviewMakersResDto getAverageReviewScore(SecurityUser securityUser, BigInteger reviewId);
    // 댓글 수정
    void updateMakersComment(BigInteger commentId, CommentReqDto reqDto);
    // 댓글 삭제
    void deleteMakersComment(BigInteger commentId);
    // 리뷰 신고
    void reportReviews(BigInteger reviewId);
}
