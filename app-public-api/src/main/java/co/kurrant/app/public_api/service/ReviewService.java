package co.kurrant.app.public_api.service;


import co.dalicious.domain.review.dto.ReviewReqDto;
import co.dalicious.domain.review.dto.ReviewableItemResDto;
import co.dalicious.domain.review.dto.ReviewsForUserResDto;
import co.kurrant.app.public_api.model.SecurityUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;

public interface ReviewService {
    //리뷰 작성
    void createReview(SecurityUser securityUser, ReviewReqDto reviewDto, BigInteger itemId, List<MultipartFile> fileList) throws IOException;
    //리뷰 작성 가능 상품 조회
    ReviewableItemResDto getOrderItemForReview(SecurityUser securityUser) throws ParseException;
    //유저가 작성한 리뷰 조회
    ReviewsForUserResDto getReviewsForUser(SecurityUser securityUser);
}
