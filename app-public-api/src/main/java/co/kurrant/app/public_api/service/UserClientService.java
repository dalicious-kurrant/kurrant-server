package co.kurrant.app.public_api.service;

import co.dalicious.domain.address.dto.LocationDto;
import co.dalicious.domain.client.dto.OpenGroupResponseDto;
import co.dalicious.domain.client.dto.ClientSpotDetailReqDto;
import co.dalicious.integration.client.user.dto.ClientSpotDetailResDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface UserClientService {
    // 고객사로 등록된 아파트 전체 리스트를 불러온다.
    List<OpenGroupResponseDto> getOpenGroupsAndApartments(SecurityUser securityUser, Map<String, Object> location);
    // 그룹별 스팟 상세조회
    ClientSpotDetailResDto getSpotDetail(SecurityUser securityUser, BigInteger spotId);
    // 유저 스팟을 선택한다
    BigInteger selectUserSpot(SecurityUser securityUser, BigInteger spotId);
    // 유저가 속한 그룹을 탈퇴한다.
    Integer withdrawClient(SecurityUser securityUser, BigInteger groupId);
}
