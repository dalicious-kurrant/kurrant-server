package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.user.dto.PointPolicyReqDto;
import co.dalicious.domain.user.dto.PointPolicyResDto;
import co.dalicious.domain.user.entity.PointHistory;
import co.dalicious.domain.user.entity.PointPolicy;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PointCondition;
import co.dalicious.domain.user.entity.enums.PointStatus;
import co.dalicious.domain.user.mapper.PointMapper;
import co.dalicious.domain.user.repository.PointPolicyRepository;
import co.dalicious.domain.user.repository.QPointPolicyRepository;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.domain.user.util.PointUtil;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.service.PointService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
    private final PointUtil pointUtil;
    private final PointPolicyRepository pointPolicyRepository;
    private final PointMapper pointMapper;
    private final QUserRepository qUserRepository;
    @Override
    public List<PointPolicyResDto.ReviewPointPolicy> findReviewPointPolicy() {
        return pointUtil.findReviewPointRange();
    }

    @Override
    @Transactional(readOnly = true)
    public PointPolicyResDto findEventPointPolicy() {
        List<PointPolicy> pointPolicyList = pointPolicyRepository.findAll();
        List<PointCondition> pointConditionList = List.of(PointCondition.class.getEnumConstants());

        List<PointPolicyResDto.EventPointPolicy> eventPointPolicyList = new ArrayList<>();
        List<PointPolicyResDto.PointConditionSelectBox> pointConditionSelectBoxList = new ArrayList<>();
        if(pointPolicyList.isEmpty() && pointConditionList.isEmpty()) return PointPolicyResDto.create(eventPointPolicyList, pointConditionSelectBoxList);

        pointConditionSelectBoxList = pointConditionList.stream().map(PointPolicyResDto.PointConditionSelectBox::create).toList();
        if(pointPolicyList.isEmpty()) return PointPolicyResDto.create(eventPointPolicyList, pointConditionSelectBoxList);

        eventPointPolicyList = pointPolicyList.stream().map(pointMapper::toEventPointPolicyResponseDto).collect(Collectors.toList());

        return PointPolicyResDto.create(eventPointPolicyList, pointConditionSelectBoxList);
    }

    @Override
    @Transactional
    public void createReviewPointPolicy(PointPolicyReqDto.EventPointPolicy reviewPointPolicy) {
        PointPolicy pointPolicy = pointMapper.createPointPolicy(reviewPointPolicy);
        pointPolicyRepository.save(pointPolicy);
    }

    @Override
    @Transactional
    public void updateEventPointPolicy(BigInteger policyId, PointPolicyReqDto.EventPointPolicy reviewPointPolicy) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(policyId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        if(pointPolicy.getEventEndDate() != null && pointPolicy.getEventEndDate().isBefore(LocalDate.now(ZoneId.of("Asia/Seoul")))) {
            throw new ApiException(ExceptionEnum.EVENT_END_DATE_IS_OVER);
        }

        if(reviewPointPolicy.getPointCondition() != null) {
            pointPolicy.updatePointCondition(PointCondition.ofCode(reviewPointPolicy.getPointCondition()));
        }
        if(reviewPointPolicy.getCompletedConditionCount() != null) {
            pointPolicy.updateCompletedConditionCount(reviewPointPolicy.getCompletedConditionCount());
        }
        if(reviewPointPolicy.getAccountCompletionLimit() != null) {
            pointPolicy.updateAccountCompletionLimit(reviewPointPolicy.getAccountCompletionLimit());
        }
        if(reviewPointPolicy.getRewardPoint() != null) {
            pointPolicy.updateRewardPoint(BigDecimal.valueOf(reviewPointPolicy.getRewardPoint()));
        }
        if(reviewPointPolicy.getEventStartDate() != null) {
            pointPolicy.updateEventStartDate(DateUtils.stringToDate(reviewPointPolicy.getEventStartDate()));
        }
        if(reviewPointPolicy.getEventEndDate() != null) {
            pointPolicy.updateEventEndDate(DateUtils.stringToDate(reviewPointPolicy.getEventEndDate()));
        }
        if(reviewPointPolicy.getBoardId() != null) {
            pointPolicy.updateBoardId(reviewPointPolicy.getBoardId());
        }
    }

    @Override
    @Transactional
    public void deleteEventPointPolicy(BigInteger policyId) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(policyId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        pointUtil.deletePointHistoryByPointPolicy(pointPolicy);
        pointPolicyRepository.delete(pointPolicy);
    }

    @Override
    @Transactional
    public void addPointsToUser(PointPolicyReqDto.AddPointToUser requestDto) {
        List<User> userList = qUserRepository.getUserAllById(requestDto.getUserIdList());

        for(User user : userList) {
            qUserRepository.updateUserPoint(user.getId(), BigDecimal.valueOf(requestDto.getRewardPoint()));
            pointUtil.createPointHistoryByOthers(user, null, PointStatus.ADMIN_REWARD, BigDecimal.valueOf(requestDto.getRewardPoint()));
        }
    }
}
