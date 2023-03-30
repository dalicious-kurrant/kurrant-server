package co.dalicious.domain.user.util;

import co.dalicious.domain.user.dto.PointPolicyResDto;
import co.dalicious.domain.user.entity.PointHistory;
import co.dalicious.domain.user.entity.PointPolicy;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.ReviewPointPolicy;
import co.dalicious.domain.user.mapper.PointMapper;
import co.dalicious.domain.user.repository.PointHistoryRepository;
import co.dalicious.domain.user.repository.QPointHistoryRepository;
import co.dalicious.domain.user.repository.QPointPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class PointUtil {
    private final QPointHistoryRepository qPointHistoryRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PointMapper pointMapper;

    public BigDecimal findReviewPoint(Boolean isReviewImage, BigDecimal itemPrice, int count) {
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
            reviewPoint = reviewPoint.add(rewardPointForImage).add(rewardPointForContent).multiply(BigDecimal.valueOf(count));
        }
        else {
            reviewPoint = reviewPoint.add(rewardPointForContent).multiply(BigDecimal.valueOf(count));
        }

        return reviewPoint;
    }

    public List<PointPolicyResDto.ReviewPointPolicy> findReviewPointRange() {
        List<ReviewPointPolicy> reviewPointPolicyList = List.of(ReviewPointPolicy.class.getEnumConstants());
        return reviewPointPolicyList.stream().map(pointMapper::toReviewPointPolicyResponseDto).collect(Collectors.toList());
    }

    public void deletePointHistoryByPointPolicy(PointPolicy pointPolicy) {
        // point 가 0 인 로그만 가져와서 다 지움
        List<PointHistory> pointHistoryList = qPointHistoryRepository.findAllByPointPolicy(pointPolicy);
        pointHistoryRepository.deleteAll(pointHistoryList);
    }

    public void createPointHistoryByPointPolicy(User user, PointPolicy pointPolicy, Map<String, BigInteger> ids) {
        Integer completeCount = pointPolicy.getCompletedConditionCount();
        Integer accountLimit = pointPolicy.getAccountCompletionLimit();

        List<PointHistory> pointHistoryList = qPointHistoryRepository.findAllByUserAndPointPolicy(user, pointPolicy);
        if(pointHistoryList.isEmpty()) {
            PointHistory pointHistory = pointMapper.createPointHistoryForCount(user, pointPolicy, ids, 0);
            pointHistoryRepository.save(pointHistory);
            return;
        }

        List<PointHistory> rewardPointHistoryCount = pointHistoryList.stream().filter(pointHistory -> pointHistory.getPoint().equals(BigDecimal.valueOf(0))).toList();
        PointHistory pointHistory = pointMapper.createPointHistoryForCount(user, pointPolicy, ids, rewardPointHistoryCount.size());

        pointHistoryRepository.save(pointHistory);
    }

    public void createPointHistoryByReview(User user, BigInteger id, BigDecimal point) {
        pointHistoryRepository.save(pointMapper.createPointHistoryForReview(user, id, point));
    }
}
