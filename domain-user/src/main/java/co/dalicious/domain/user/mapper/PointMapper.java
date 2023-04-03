package co.dalicious.domain.user.mapper;

import co.dalicious.domain.user.dto.PointPolicyReqDto;
import co.dalicious.domain.user.dto.PointPolicyResDto;
import co.dalicious.domain.user.entity.PointHistory;
import co.dalicious.domain.user.entity.PointPolicy;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PointCondition;
import co.dalicious.domain.user.entity.enums.PointStatus;
import co.dalicious.domain.user.entity.enums.ReviewPointPolicy;
import co.dalicious.system.util.DateUtils;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface PointMapper {

    default PointPolicyResDto.ReviewPointPolicy toReviewPointPolicyResponseDto(ReviewPointPolicy reviewPointPolicy) {
        PointPolicyResDto.ReviewPointPolicy dto = new PointPolicyResDto.ReviewPointPolicy();

        dto.setMaxPrice(reviewPointPolicy.getMaxPrice().equals("~") ? null : Integer.valueOf(reviewPointPolicy.getMaxPrice()));
        dto.setMinPrice(reviewPointPolicy.getMinPrice().equals("~") ? null : Integer.valueOf(reviewPointPolicy.getMinPrice()));
        dto.setImagePoint(BigDecimal.valueOf(Integer.parseInt(reviewPointPolicy.getImagePrice())));
        dto.setContentPoint(BigDecimal.valueOf(Integer.parseInt(reviewPointPolicy.getContentPoint())));

        return dto;
    }

    default PointPolicyResDto.EventPointPolicy toEventPointPolicyResponseDto(PointPolicy pointPolicy) {
        PointPolicyResDto.EventPointPolicy dto = new PointPolicyResDto.EventPointPolicy();

        dto.setPointPolicyId(pointPolicy.getId());
        dto.setPointConditionCode(pointPolicy.getPointCondition().getCode());
        dto.setPointConditionValue(pointPolicy.getPointCondition().getCondition());
        dto.setRewardPoint(pointPolicy.getRewardPoint());
        dto.setEventStartDate(pointPolicy.getEventStartDate() == null ? null : DateUtils.localDateToString(pointPolicy.getEventStartDate()));
        dto.setEventEndDate(pointPolicy.getEventEndDate() == null ? null : DateUtils.localDateToString(pointPolicy.getEventEndDate()));
        dto.setCompletedConditionCount(pointPolicy.getCompletedConditionCount());
        dto.setAccountCompletionLimit(pointPolicy.getAccountCompletionLimit());

        return dto;
    }

    default PointPolicy createPointPolicy(PointPolicyReqDto.EventPointPolicy eventPointPolicy) {
        return PointPolicy.builder()
                .pointCondition(PointCondition.ofCode(eventPointPolicy.getPointCondition()))
                .completedConditionCount(eventPointPolicy.getCompletedConditionCount())
                .accountCompletionLimit(eventPointPolicy.getAccountCompletionLimit())
                .rewardPoint(BigDecimal.valueOf(eventPointPolicy.getRewardPoint()))
                .eventStartDate(eventPointPolicy.getEventStartDate() == null ? null : DateUtils.stringToDate(eventPointPolicy.getEventStartDate()))
                .eventEndDate(eventPointPolicy.getEventEndDate() == null ? null : DateUtils.stringToDate(eventPointPolicy.getEventEndDate()))
                .build();
    }

    default PointHistory createPointHistoryForCount(User user, PointPolicy pointPolicy, BigInteger noticeId, Integer count, PointStatus pointStatus) {

        BigDecimal point = BigDecimal.ZERO;
        if((count / pointPolicy.getCompletedConditionCount()) > pointPolicy.getAccountCompletionLimit()) {
            return null;
        } else if(count.equals(pointPolicy.getCompletedConditionCount())) {
            point = point.add(pointPolicy.getRewardPoint());
        }

        return PointHistory.builder()
                .point(point)
                .user(user)
                .pointStatus(pointStatus)
                .boardId(noticeId)
                .pointPolicyId(pointPolicy.getId())
                .build();
    }

    default PointHistory createPointHistoryForReview(User user, BigInteger reviewId, BigDecimal point) {
        return PointHistory.builder()
                .point(point)
                .user(user)
                .pointStatus(PointStatus.REVIEW_REWARD)
                .reviewId(reviewId)
                .build();
    }

    default PointHistory createPointHistoryForOrder(User user, BigInteger orderId,  BigInteger cancelId, PointStatus pointStatus, BigDecimal point) {
        return PointHistory.builder()
                .point(point)
                .user(user)
                .pointStatus(pointStatus)
                .orderId(orderId)
                .paymentCancelHistoryId(cancelId)
                .build();
    }
}
