package co.kurrant.app.admin_api.service;

import co.dalicious.domain.user.dto.PointPolicyReqDto;
import co.dalicious.domain.user.dto.PointPolicyResDto;

import java.math.BigInteger;
import java.util.List;

public interface PointService {
    List<PointPolicyResDto.ReviewPointPolicy> findReviewPointPolicy();
    PointPolicyResDto findEventPointPolicy();
    void createReviewPointPolicy(PointPolicyReqDto.EventPointPolicy reviewPointPolicy);
    void updateEventPointPolicy(BigInteger policyId, PointPolicyReqDto.EventPointPolicy reviewPointPolicy);
    void deleteEventPointPolicy(BigInteger policyId);
}
