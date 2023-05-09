package co.dalicious.domain.user.mapper;

import co.dalicious.domain.user.dto.PointPolicyReqDto;
import co.dalicious.domain.user.dto.pointDto.AccumulatedFoundersPointDto;
import co.dalicious.domain.user.dto.pointPolicyResponse.FoundersPointPolicyDto;
import co.dalicious.domain.user.dto.pointPolicyResponse.PointPolicyResDto;
import co.dalicious.domain.user.entity.Founders;
import co.dalicious.domain.user.entity.PointHistory;
import co.dalicious.domain.user.entity.PointPolicy;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.FoundersPointPolicy;
import co.dalicious.domain.user.entity.enums.PointCondition;
import co.dalicious.domain.user.entity.enums.PointStatus;
import co.dalicious.domain.user.entity.enums.ReviewPointPolicy;
import co.dalicious.system.util.DateUtils;
import exception.ApiException;
import exception.ExceptionEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

import static java.math.BigDecimal.valueOf;

@Mapper(componentModel = "spring", imports = DateUtils.class)
public interface PointMapper {

    default PointPolicyResDto.ReviewPointPolicy toReviewPointPolicyResponseDto(ReviewPointPolicy reviewPointPolicy) {
        PointPolicyResDto.ReviewPointPolicy dto = new PointPolicyResDto.ReviewPointPolicy();

        dto.setMaxPrice(reviewPointPolicy.getMaxPrice().equals("~") ? null : Integer.valueOf(reviewPointPolicy.getMaxPrice()));
        dto.setMinPrice(reviewPointPolicy.getMinPrice().equals("~") ? null : Integer.valueOf(reviewPointPolicy.getMinPrice()));
        dto.setImagePoint(valueOf(Integer.parseInt(reviewPointPolicy.getImagePrice())));
        dto.setContentPoint(valueOf(Integer.parseInt(reviewPointPolicy.getContentPoint())));

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
        dto.setBoardId(pointPolicy.getBoardId());

        return dto;
    }

    default PointPolicy createPointPolicy(PointPolicyReqDto.EventPointPolicy eventPointPolicy) {
        return PointPolicy.builder()
                .pointCondition(PointCondition.ofCode(eventPointPolicy.getPointCondition()))
                .completedConditionCount(eventPointPolicy.getCompletedConditionCount())
                .accountCompletionLimit(eventPointPolicy.getAccountCompletionLimit())
                .rewardPoint(valueOf(eventPointPolicy.getRewardPoint()))
                .eventStartDate(eventPointPolicy.getEventStartDate() == null ? null : DateUtils.stringToDate(eventPointPolicy.getEventStartDate()))
                .eventEndDate(eventPointPolicy.getEventEndDate() == null ? null : DateUtils.stringToDate(eventPointPolicy.getEventEndDate()))
                .boardId(eventPointPolicy.getBoardId())
                .build();
    }

    default PointHistory createPointHistoryForCount(User user, PointPolicy pointPolicy, BigInteger noticeId, Integer count, PointStatus pointStatus) {
        count = count + 1;
        BigDecimal point = BigDecimal.ZERO;
        // 이미 이벤트 적립을 받지 않았고, 적립 횟수에 도달했으면 포인트 지급.
        if((count / pointPolicy.getCompletedConditionCount()) > pointPolicy.getAccountCompletionLimit()) {
            throw new ApiException(ExceptionEnum.EVENT_COUNT_OVER);
        } else if(count.equals(pointPolicy.getCompletedConditionCount())) {
            point = point.add(pointPolicy.getRewardPoint());
        }

        // 아니면 0 포인트로 로그 생성
        return PointHistory.builder()
                .point(point)
                .user(user)
                .leftPoint(user.getPoint())
                .pointStatus(pointStatus)
                .boardId(noticeId)
                .pointPolicyId(pointPolicy.getId())
                .build();
    }

    @Mapping(source = "user", target = "user")
    @Mapping(target = "reviewId", expression = "java(pointStatus.equals(PointStatus.REVIEW_REWARD) ? id : null)")
    @Mapping(target = "orderId", expression = "java(pointStatus.equals(PointStatus.USED) ? id : null)")
    @Mapping(target = "boardId", expression = "java(pointStatus.equals(PointStatus.EVENT_REWARD) ? id : null)")
    @Mapping(target = "paymentCancelHistoryId", expression = "java(pointStatus.equals(PointStatus.CANCEL) ? id : null)")
    @Mapping(source = "point", target = "point")
    @Mapping(source = "pointStatus", target = "pointStatus")
    @Mapping(target = "leftPoint", expression = "java(PointStatus.rewardStatus().contains(pointStatus) ? user.getPoint().add(point) : user.getPoint().subtract(point))")
    PointHistory createPointHistoryByOthers(User user, BigInteger id, PointStatus pointStatus, BigDecimal point);

    default FoundersPointPolicyDto toReviewPointPolicyResponseDto(FoundersPointPolicy foundersPointPolicy) {
        FoundersPointPolicyDto dto = new FoundersPointPolicyDto();

        dto.setMaxPoint(valueOf(Long.parseLong(foundersPointPolicy.getMaxPoint())));
        dto.setMinPoint(valueOf(Long.parseLong(foundersPointPolicy.getMinPoint())));
        dto.setValue(foundersPointPolicy.getValue());

        return dto;
    }

    default AccumulatedFoundersPointDto toAccumulatedFoundersPointDto(User user, LocalDate foundersStartDate, int count, BigDecimal totalPoint) {
        return AccumulatedFoundersPointDto.builder()
                .userId(user.getId())
                .userName(user.getName())
                .foundersStartDate(DateUtils.format(foundersStartDate))
                .count(count)
                .point(BigDecimal.valueOf(80))
                .totalPoint(totalPoint)
                .build();
    }
}
