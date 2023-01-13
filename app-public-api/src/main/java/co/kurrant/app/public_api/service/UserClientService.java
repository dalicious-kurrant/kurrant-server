package co.kurrant.app.public_api.service;

import co.dalicious.domain.client.dto.ApartmentResponseDto;
import co.dalicious.domain.client.dto.ClientSpotDetailReqDto;
import co.dalicious.domain.client.dto.ClientSpotDetailResDto;
import co.dalicious.domain.user.dto.SelectUserSpotResDto;
import co.kurrant.app.public_api.model.SecurityUser;

import java.math.BigInteger;
import java.util.List;

public interface UserClientService {
    // 고객사로 등록된 아파트 전체 리스트를 불러온다.
    List<ApartmentResponseDto> getApartments(SecurityUser securityUser);
    // 그룹별 스팟 상세조회
    ClientSpotDetailResDto getSpotDetail(SecurityUser securityUser, BigInteger spotId);
    // 유저 스팟을 선택한다
    BigInteger selectUserSpot(SecurityUser securityUser, BigInteger spotId);
    // 유저의 Default 스팟을 등록한다
    BigInteger saveUserDefaultSpot(SecurityUser securityUser, ClientSpotDetailReqDto spotDetailReqDto, BigInteger spotId);
    // 유저의 Default 스팟을 등록한다
    BigInteger updateUserHo(SecurityUser securityUser, ClientSpotDetailReqDto spotDetailReqDto, BigInteger spotId);
    // 유저가 속한 그룹을 탈퇴한다.
    Integer withdrawClient(SecurityUser securityUser, BigInteger groupId);
}
