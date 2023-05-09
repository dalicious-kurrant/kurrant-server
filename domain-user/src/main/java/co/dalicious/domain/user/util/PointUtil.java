package co.dalicious.domain.user.util;

import co.dalicious.domain.user.dto.pointPolicyResponse.FoundersPointPolicyDto;
import co.dalicious.domain.user.dto.pointPolicyResponse.PointPolicyResDto;
import co.dalicious.domain.user.entity.PointHistory;
import co.dalicious.domain.user.entity.PointPolicy;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.FoundersPointPolicy;
import co.dalicious.domain.user.entity.enums.PointStatus;
import co.dalicious.domain.user.entity.enums.ReviewPointPolicy;
import co.dalicious.domain.user.mapper.PointMapper;
import co.dalicious.domain.user.repository.PointHistoryRepository;
import co.dalicious.domain.user.repository.QPointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class PointUtil {
    private final QPointHistoryRepository qPointHistoryRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PointMapper pointMapper;

    // 리뷰 작성 시 포인트 산정
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

            if(min == null && max != null && price <= max) {
                rewardPointForImage = rewardPointForImage.add(reviewPointPolicy.getImagePoint());
                rewardPointForContent = rewardPointForContent.add(reviewPointPolicy.getContentPoint());
            }
            else if(min != null && max == null && min <= price) {
                rewardPointForImage = rewardPointForImage.add(reviewPointPolicy.getImagePoint());
                rewardPointForContent = rewardPointForContent.add(reviewPointPolicy.getContentPoint());
            }
            else if(min != null && max != null && min <= price && max >= price){
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

    // 리뷰 포인트 정책 전제 조회
    public List<PointPolicyResDto.ReviewPointPolicy> findReviewPointRange() {
        List<ReviewPointPolicy> reviewPointPolicyList = List.of(ReviewPointPolicy.class.getEnumConstants());
        return reviewPointPolicyList.stream().map(pointMapper::toReviewPointPolicyResponseDto).collect(Collectors.toList());
    }

    // 이벤트 삭제
    @Transactional
    public void deletePointHistoryByPointPolicy(PointPolicy pointPolicy) {
        // point 가 0 인 로그만 가져와서 다 지움
        List<PointHistory> pointHistoryList = qPointHistoryRepository.findAllByPointPolicy(pointPolicy);
        if(pointHistoryList.isEmpty()) return;
        pointHistoryRepository.deleteAll(pointHistoryList);
    }

    @Transactional
    public void createPointHistoryByPointPolicy(User user, PointPolicy pointPolicy, BigInteger noticeId, PointStatus pointStatus) {
        // 정책에 해당하는 조건 가져오기
        Integer completeCount = pointPolicy.getCompletedConditionCount();
        Integer accountLimit = pointPolicy.getAccountCompletionLimit();

        // 정책 id를 가졌고, 유저가 동일한 포인트 내역 조회
        List<PointHistory> pointHistoryList = qPointHistoryRepository.findAllByUserAndPointPolicy(user, pointPolicy);
        // 이벤트 참여가 처음이면
        if(pointHistoryList.isEmpty()) {
            PointHistory pointHistory = pointMapper.createPointHistoryForCount(user, pointPolicy, noticeId, 0, pointStatus);
            pointHistoryRepository.save(pointHistory);
            return;
        }

        // 이벤트 참여 횟수
        List<PointHistory> rewardPointHistoryCount = pointHistoryList.stream().filter(pointHistory -> pointHistory.getPoint().equals(BigDecimal.valueOf(0))).toList();
        PointHistory pointHistory = pointMapper.createPointHistoryForCount(user, pointPolicy, noticeId, rewardPointHistoryCount.size(), pointStatus);

        pointHistoryRepository.save(pointHistory);
    }

    @Transactional
    public void createPointHistoryByOthers(User user, BigInteger id, PointStatus pointStatus, BigDecimal point) {
        PointHistory pointHistory = pointMapper.createPointHistoryByOthers(user, id, pointStatus, point);
        pointHistoryRepository.save(pointHistory);
    }

    // 리뷰 수정 시 포인트 산정
    public BigDecimal findReviewPointWhenUpdate(User user, BigInteger reviewId) {
        List<PointHistory> pointHistory = qPointHistoryRepository.findByContentId(user, reviewId, PointStatus.REVIEW_REWARD);

        BigDecimal point = BigDecimal.ZERO;

        //이미 사진 포인트를 받은 경우
        if(pointHistory.size() > 1) {
            return point;
        }

        BigDecimal contentPoint = pointHistory.stream().sorted(Comparator.comparing(PointHistory::getPoint)).map(PointHistory::getPoint).toList().get(0);

        List<PointPolicyResDto.ReviewPointPolicy> reviewPointPolicyList = findReviewPointRange();
        for(PointPolicyResDto.ReviewPointPolicy reviewPointPolicy : reviewPointPolicyList) {
            if(reviewPointPolicy.getContentPoint().compareTo(contentPoint) == 0) {
                BigDecimal imagePoint = reviewPointPolicy.getImagePoint();
                point = point.add(imagePoint);
            }
        }

        return point;
    }

    // 파운더스 포인트 정책 전제 조회
    public List<FoundersPointPolicyDto> findFoundersPointPolicyDto() {
        List<FoundersPointPolicy> foundersPointPolicyList = List.of(FoundersPointPolicy.class.getEnumConstants());
        return foundersPointPolicyList.stream().map(pointMapper::toReviewPointPolicyResponseDto).collect(Collectors.toList());
    }

    // 파운더스 적립 포인트
    public BigDecimal findFoundersPoint() {
        List<FoundersPointPolicyDto> foundersPointPolicyDtos = findFoundersPointPolicyDto();
        return foundersPointPolicyDtos.get(0).getMaxPoint();
    }
}
