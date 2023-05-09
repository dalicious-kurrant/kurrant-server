package co.kurrant.app.admin_api.service;

import co.dalicious.domain.user.dto.PointPolicyReqDto;
import co.dalicious.domain.user.dto.pointPolicyResponse.FoundersPointPolicyDto;
import co.dalicious.domain.user.dto.pointPolicyResponse.PointPolicyResDto;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public interface PointService {
    List<PointPolicyResDto.ReviewPointPolicy> findReviewPointPolicy();
    PointPolicyResDto findEventPointPolicy();
    void createReviewPointPolicy(PointPolicyReqDto.EventPointPolicy reviewPointPolicy);
    void updateEventPointPolicy(BigInteger policyId, PointPolicyReqDto.EventPointPolicy reviewPointPolicy);
    void deleteEventPointPolicy(BigInteger policyId);
    void addPointsToUser(PointPolicyReqDto.AddPointToUser requestDto);
    List<FoundersPointPolicyDto> findFoundersPointPolicy();
    void AccumulatedFoundersPointSave(LocalDate selectDate);
}
