package co.dalicious.domain.payment.util;

import co.dalicious.domain.payment.entity.PointPolicy;
import co.dalicious.domain.payment.entity.enums.PointCondition;
import co.dalicious.domain.payment.repository.QPointPolicyRepository;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.*;


@Component
@RequiredArgsConstructor
public class PointUtil {

    private final QPointPolicyRepository qPointPolicyRepository;

    public BigDecimal findReviewPoint(Boolean isReviewImage, BigDecimal itemPrice) {
        BigDecimal reviewPoint = BigDecimal.ZERO;

        // 어떤 조건에 부합하는지 찾기
        MultiValueMap<PointCondition, Integer> reviewPointRangeMap = findReviewPointRange();
        List<PointCondition> rewardConditionList = new ArrayList<>();

        for(PointCondition pointCondition : reviewPointRangeMap.keySet()) {
            List<Integer> pointRangeList = reviewPointRangeMap.get(pointCondition);

            BigDecimal min = BigDecimal.valueOf(Collections.min(pointRangeList));
            BigDecimal max = BigDecimal.valueOf(Collections.max(pointRangeList));

            // min < itemPrice -> result < 0, min = itemPrice -> result = 0, min > itemPrice -> result > 0
            int minCompareResult = min.compareTo(itemPrice);
            // max < itemPrice -> result < 0, max = itemPrice -> result = 0, max > itemPrice -> result > 0
            int maxCompareResult = max.compareTo(itemPrice);

            if((minCompareResult == 0 || minCompareResult < 0) || (maxCompareResult > 0 || maxCompareResult == 0)) {
                rewardConditionList.add(pointCondition);
            }
        }

        List<PointPolicy> pointPolicyList = qPointPolicyRepository.findAllPointPolicyByCondition(rewardConditionList);
        BigDecimal rewardPointForImage = pointPolicyList.stream()
                .filter(pointPolicy -> pointPolicy.getPointCondition().name().startsWith("PHOTO_"))
                .findFirst()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND))
                .getRewardPoint();
        BigDecimal rewardPointForContent = pointPolicyList.stream()
                .filter(pointPolicy -> pointPolicy.getPointCondition().name().startsWith("CONTENT_"))
                .findFirst()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND))
                .getRewardPoint();

        if(isReviewImage) {
            reviewPoint = reviewPoint.add(rewardPointForImage).add(rewardPointForContent);
        }
        else {
            reviewPoint = reviewPoint.add(rewardPointForContent);
        }

        return reviewPoint;
    }

    public static MultiValueMap<PointCondition, Integer> findReviewPointRange() {
        MultiValueMap<PointCondition, Integer> reviewPointRangeList = new LinkedMultiValueMap<>();

        List<PointCondition> pointConditionList = new ArrayList<>();
        pointConditionList.add(PointCondition.PHOTO_REVIEW_RANGE_1);
        pointConditionList.add(PointCondition.PHOTO_REVIEW_RANGE_2);
        pointConditionList.add(PointCondition.PHOTO_REVIEW_RANGE_3);
        pointConditionList.add(PointCondition.PHOTO_REVIEW_RANGE_4);
        pointConditionList.add(PointCondition.CONTENT_REVIEW_RANGE_1);
        pointConditionList.add(PointCondition.CONTENT_REVIEW_RANGE_2);
        pointConditionList.add(PointCondition.CONTENT_REVIEW_RANGE_3);
        pointConditionList.add(PointCondition.CONTENT_REVIEW_RANGE_4);

        for(PointCondition pointCondition : pointConditionList) {
            String condition = pointCondition.getCondition();

            List<String> conditionList = List.of(condition.split(" ~ "));
            List<Integer> conditionIntList = conditionList.stream().map(Integer::valueOf).toList();

            Integer min = Collections.min(conditionIntList);
            Integer max = Collections.max(conditionIntList);

            reviewPointRangeList.add(pointCondition, min);
            reviewPointRangeList.add(pointCondition, max);
        }

        return reviewPointRangeList;
    }
}
