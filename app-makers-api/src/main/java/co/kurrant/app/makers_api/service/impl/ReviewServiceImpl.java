package co.kurrant.app.makers_api.service.impl;

import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.review.dto.CommentReqDto;
import co.dalicious.domain.review.dto.ReviewMakersResDto;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    @Override
    public void createMakersComment(BigInteger reviewId, CommentReqDto reqDto) {

    }

    @Override
    public ListItemResponseDto<ReviewMakersResDto.ReviewListDto> getUnansweredReview(SecurityUser securityUser) {
        return null;
    }

    @Override
    public ListItemResponseDto<ReviewMakersResDto.ReviewListDto> getAllReview() {
        return null;
    }

    @Override
    public ReviewMakersResDto.ReviewListDto getReviewDetail(SecurityUser securityUser, BigInteger reviewId) {
        return null;
    }

    @Override
    public ReviewMakersResDto getAverageReviewScore(SecurityUser securityUser, BigInteger reviewId) {
        return null;
    }
}
