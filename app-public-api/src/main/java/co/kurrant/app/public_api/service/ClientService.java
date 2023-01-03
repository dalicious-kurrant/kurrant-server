package co.kurrant.app.public_api.service;

import co.kurrant.app.public_api.dto.client.SpotListResponseDto;

import javax.servlet.http.HttpServletRequest;

public interface ClientService {
    // 유저가 속한 그룹 정보 리스트
    SpotListResponseDto getClients(HttpServletRequest httpServletRequest);
    // 그룹별 스팟 상세조회
    void getSpotDetail(HttpServletRequest httpServletRequest, Integer clientType, Integer clientId, Integer spotId);
    // 유저의 Default 스팟을 등록한다
    void saveUserSpot(HttpServletRequest httpServletRequest, Integer clientType, Integer clientId, Integer spotId);
    // 유저가 속한 그룹을 탈퇴한다.
    void withdrawClient(HttpServletRequest httpServletRequest, Integer clientType, Integer clientId);
}
