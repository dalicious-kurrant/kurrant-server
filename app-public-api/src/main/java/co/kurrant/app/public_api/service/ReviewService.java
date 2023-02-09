package co.kurrant.app.public_api.service;


import co.dalicious.domain.order.entity.OrderItem;
import co.kurrant.app.public_api.dto.review.ReviewDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.math.BigInteger;
import java.util.List;

public interface ReviewService {
    //리뷰 작성
    BigInteger createReview(SecurityUser securityUser, ReviewDto reviewDto, BigInteger foodId);
    //리뷰 작성 가능 상품 조회
    List<OrderItem> getOrderItemForReview(SecurityUser securityUser);
}
