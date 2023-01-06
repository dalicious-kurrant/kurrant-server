package co.kurrant.app.public_api.service;

import co.kurrant.app.public_api.dto.client.ClientSpotDetailReqDto;
import co.kurrant.app.public_api.dto.client.ClientSpotDetailResDto;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;

public interface ClientService {
    // 그룹별 스팟 상세조회
    void getSpotDetail(HttpServletRequest httpServletRequest, Integer clientType, Integer clientId, Integer spotId);
    // 유저의 Default 스팟을 등록한다
    void saveUserSpot(HttpServletRequest httpServletRequest, Integer clientType, Integer clientId, Integer spotId);
    // 유저가 속한 그룹을 탈퇴한다.
    void withdrawClient(HttpServletRequest httpServletRequest, Integer clientType, Integer clientId);
    // 유저에게 그룹을 할당한다.
    void setUserGroup(HttpServletRequest httpServletRequest, Integer clientType, Integer clientId);
}
