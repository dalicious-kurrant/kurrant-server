package co.kurrant.app.makers_api.service.impl;

import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.review.dto.CommentReqDto;
import co.dalicious.domain.review.dto.ReviewMakersResDto;
import co.dalicious.domain.review.entity.Comments;
import co.dalicious.domain.review.entity.MakersComments;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.review.mapper.ReviewMapper;
import co.dalicious.domain.review.repository.CommentsRepository;
import co.dalicious.domain.review.repository.QReviewRepository;
import co.dalicious.domain.review.repository.ReviewRepository;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.ReviewService;
import co.kurrant.app.makers_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final QReviewRepository qReviewRepository;
    private final ReviewMapper reviewMapper;
    private final CommentsRepository commentsRepository;
    private final UserUtil userUtil;
    @Override
    @Transactional
    public void createMakersComment(BigInteger reviewId, CommentReqDto reqDto) {
        Reviews reviews = qReviewRepository.findById(reviewId);
        if(reviews == null) throw new ApiException(ExceptionEnum.REVIEW_NOT_FOUND);

        MakersComments comments = reviewMapper.toMakersComment(reqDto, reviews);
        commentsRepository.save(comments);
    }

    @Override
    @Transactional(readOnly = true)
    public ListItemResponseDto<ReviewMakersResDto.ReviewListDto> getUnansweredReview(SecurityUser securityUser) {
        Makers makers = userUtil.getMakers(securityUser);

        List<Reviews> reviewsList = qReviewRepository.findAllByMakers(makers);


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

    @Override
    public void updateMakersComment(BigInteger commentId, CommentReqDto reqDto) {

    }

    @Override
    public void deleteMakersComment(BigInteger commentId) {

    }

    @Override
    public void reportReviews(BigInteger reviewId) {

    }
}
