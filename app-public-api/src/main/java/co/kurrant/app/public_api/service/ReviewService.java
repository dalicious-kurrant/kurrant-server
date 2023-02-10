package co.kurrant.app.public_api.service;


import co.dalicious.domain.review.dto.ReviewReqDto;
import co.dalicious.domain.review.dto.ReviewableItemResDto;
import co.dalicious.domain.review.dto.ReviewsForUserResDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.math.BigInteger;
import java.text.ParseException;

public interface ReviewService {
    //리뷰 작성
    BigInteger createReview(SecurityUser securityUser, ReviewReqDto reviewDto, BigInteger itemId);
    //리뷰 작성 가능 상품 조회
    ReviewableItemResDto getOrderItemForReview(SecurityUser securityUser) throws ParseException;
    //유저가 작성한 리뷰 조회
    ReviewsForUserResDto getReviewsForUser(SecurityUser securityUser);
}
