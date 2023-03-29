package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.user.dto.PointPolicyReqDto;
import co.dalicious.domain.user.dto.PointPolicyResDto;
import co.dalicious.domain.user.entity.PointPolicy;
import co.dalicious.domain.user.mapper.PointMapper;
import co.dalicious.domain.user.repository.PointPolicyRepository;
import co.dalicious.domain.user.repository.QPointPolicyRepository;
import co.dalicious.domain.user.util.PointUtil;
import co.kurrant.app.admin_api.service.PointService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final QPointPolicyRepository qPointPolicyRepository;
    @Override
    public List<PointPolicyResDto.ReviewPointPolicy> findReviewPointPolicy() {
        return pointUtil.findReviewPointRange();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PointPolicyResDto.EventPointPolicy> findEventPointPolicy() {
        List<PointPolicy> pointPolicyList = pointPolicyRepository.findAll();
        List<PointPolicyResDto.EventPointPolicy> eventPointPolicyList = new ArrayList<>();
        if(pointPolicyList.isEmpty()) return eventPointPolicyList;

        eventPointPolicyList = pointPolicyList.stream().map(pointMapper::toEventPointPolicyResponseDto).collect(Collectors.toList());

        return eventPointPolicyList;
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

        pointPolicy.updatePointPolicy(reviewPointPolicy);
    }

    @Override
    @Transactional
    public void deleteEventPointPolicy(BigInteger policyId) {
        PointPolicy pointPolicy = pointPolicyRepository.findById(policyId).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        pointUtil.deletePointHistoryByPointPolicy(pointPolicy);
        pointPolicyRepository.delete(pointPolicy);
    }
}
