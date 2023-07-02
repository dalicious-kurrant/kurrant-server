package co.kurrant.app.public_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.client.dto.OpenGroupDetailDto;
import co.dalicious.domain.client.dto.OpenGroupListForKeywordDto;
import co.dalicious.domain.client.dto.OpenGroupResponseDto;
import co.dalicious.domain.client.dto.ClientSpotDetailResDto;
import co.dalicious.domain.client.dto.corporation.CorporationResponseDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface UserClientService {
    // 고객사로 등록된 아파트 전체 리스트를 불러온다.
    ListItemResponseDto<OpenGroupResponseDto> getOpenGroups(SecurityUser securityUser, Map<String, Object> location, Map<String, Object> parameters, OffsetBasedPageRequest pageable);
    // 그룹별 스팟 상세조회
    ClientSpotDetailResDto getSpotDetail(SecurityUser securityUser, BigInteger spotId);
    // 유저 스팟을 선택한다
    BigInteger selectUserSpot(SecurityUser securityUser, BigInteger spotId);
    // 유저가 속한 그룹을 탈퇴한다.
    Integer withdrawClient(SecurityUser securityUser, BigInteger groupId);
    // 공유 스팟의 상세조회
    OpenGroupDetailDto getOpenSpotDetail(SecurityUser securityUser, BigInteger groupId);
    List<OpenGroupListForKeywordDto> getOpenGroupsForKeyword(SecurityUser securityUser);
    // 유저가 초대된 기업 조회
    List<CorporationResponseDto> getUserCorporation(SecurityUser securityUser);
}
