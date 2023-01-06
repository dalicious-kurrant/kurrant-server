package co.kurrant.app.public_api.service;

import co.kurrant.app.public_api.dto.client.ClientSpotDetailReqDto;
import co.kurrant.app.public_api.dto.client.ClientSpotDetailResDto;
import co.kurrant.app.public_api.dto.client.SpotListResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.List;

public interface ClientService {
    // 유저가 속한 그룹 정보 리스트
    List<SpotListResponseDto> getClients(HttpServletRequest httpServletRequest);
    // 그룹별 스팟 상세조회
    ClientSpotDetailResDto getSpotDetail(HttpServletRequest httpServletRequest, Integer clientType, BigInteger clientId, BigInteger spotId);
    // 유저의 Default 스팟을 등록한다
    BigInteger saveUserSpot(HttpServletRequest httpServletRequest, ClientSpotDetailReqDto spotDetailReqDto, Integer clientType, BigInteger clientId, BigInteger spotId);
    // 유저가 속한 그룹을 탈퇴한다.
    Integer withdrawClient(HttpServletRequest httpServletRequest, Integer clientType, BigInteger clientId);
}
