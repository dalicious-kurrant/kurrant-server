package co.dalicious.domain.user.util;

import co.dalicious.domain.user.dto.PointPolicyResDto;
import co.dalicious.domain.user.entity.PointPolicy;
import co.dalicious.domain.user.entity.enums.PointCondition;
import co.dalicious.domain.user.entity.enums.ReviewPointPolicy;
import co.dalicious.domain.user.mapper.PointMapper;
import co.dalicious.domain.user.repository.QPointPolicyRepository;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class PointUtil {

    private final QPointPolicyRepository qPointPolicyRepository;
    private final PointMapper pointMapper;

    public BigDecimal findReviewPoint(Boolean isReviewImage, BigDecimal itemPrice) {
        BigDecimal reviewPoint = BigDecimal.ZERO;

        // 어떤 조건에 부합하는지 찾기
        List<PointPolicyResDto.ReviewPointPolicy> reviewPointPolicyList = findReviewPointRange();
        BigDecimal rewardPointForImage = BigDecimal.ZERO;
        BigDecimal rewardPointForContent = BigDecimal.ZERO;

        for(PointPolicyResDto.ReviewPointPolicy reviewPointPolicy : reviewPointPolicyList) {

            Integer min = reviewPointPolicy.getMinPrice() == null ? null : reviewPointPolicy.getMinPrice();
            Integer max = reviewPointPolicy.getMaxPrice() == null ? null : reviewPointPolicy.getMaxPrice();
            int price = itemPrice.intValue();

            if(min == null && max != null && price < max) {
                rewardPointForImage = rewardPointForImage.add(reviewPointPolicy.getImagePoint());
                rewardPointForContent = rewardPointForContent.add(reviewPointPolicy.getContentPoint());
            }
            else if(min != null && max == null && min < price) {
                rewardPointForImage = rewardPointForImage.add(reviewPointPolicy.getImagePoint());
                rewardPointForContent = rewardPointForContent.add(reviewPointPolicy.getContentPoint());
            }
            else {
                rewardPointForImage = rewardPointForImage.add(reviewPointPolicy.getImagePoint());
                rewardPointForContent = rewardPointForContent.add(reviewPointPolicy.getContentPoint());
            }
        }
        if(isReviewImage) {
            reviewPoint = reviewPoint.add(rewardPointForImage).add(rewardPointForContent);
        }
        else {
            reviewPoint = reviewPoint.add(rewardPointForContent);
        }

        return reviewPoint;
    }

    public List<PointPolicyResDto.ReviewPointPolicy> findReviewPointRange() {
        List<ReviewPointPolicy> reviewPointPolicyList = new ArrayList<>();
        reviewPointPolicyList.add(ReviewPointPolicy.REVIEW_RANGE_1);
        reviewPointPolicyList.add(ReviewPointPolicy.REVIEW_RANGE_2);
        reviewPointPolicyList.add(ReviewPointPolicy.REVIEW_RANGE_3);
        reviewPointPolicyList.add(ReviewPointPolicy.REVIEW_RANGE_4);

        return reviewPointPolicyList.stream().map(pointMapper::toReviewPointPolicyResponseDto).collect(Collectors.toList());
    }
}
