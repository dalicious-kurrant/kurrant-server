package co.kurrant.app.public_api.service;


import co.kurrant.app.public_api.dto.review.ReviewDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.math.BigInteger;

public interface ReviewService {
    BigInteger createReview(SecurityUser securityUser, ReviewDto reviewDto);
}
