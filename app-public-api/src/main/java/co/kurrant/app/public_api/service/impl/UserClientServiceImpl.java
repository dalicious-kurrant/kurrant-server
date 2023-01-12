package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.client.dto.ApartmentResponseDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.mapper.ApartmentListMapper;
import co.dalicious.domain.client.mapper.SpotDetailResponseMapper;
import co.dalicious.domain.client.repository.*;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.entity.enums.ClientType;
import co.dalicious.domain.user.entity.enums.SpotStatus;
import co.dalicious.domain.user.repository.UserGroupRepository;
import co.dalicious.domain.user.repository.UserSpotRepository;
import co.dalicious.system.util.DiningType;
import co.dalicious.domain.client.dto.ClientSpotDetailReqDto;
import co.dalicious.domain.client.dto.ClientSpotDetailResDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserClientService;
import co.kurrant.app.public_api.service.CommonService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserClientServiceImpl implements UserClientService {
    private final ApartmentListMapper apartmentMapper;
    private final CommonService commonService;
    private final ApartmentRepository apartmentRepository;
    private final UserGroupRepository userGroupRepository;
    private final SpotRepository spotRepository;
    private final UserSpotRepository userSpotRepository;
    private final GroupRepository groupRepository;
    private final SpotDetailResponseMapper spotDetailResponseMapper;

    @Override
    @Transactional
    public ClientSpotDetailResDto getSpotDetail(SecurityUser securityUser, BigInteger spotId) {
        // 유저 정보 가져오기
        User user = commonService.getUser(securityUser);
        // 스팟 정보 가져오기
        Spot spot = spotRepository.findById(spotId).orElseThrow(
                () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
        );
        // 스팟에 속한 그룹 가져오기
        Group group = spot.getGroup();
        UserGroup userGroup = user.getGroups().stream()
                .filter(g -> g.getGroup().equals(group) && g.getClientStatus().equals(ClientStatus.BELONG))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.UNAUTHORIZED));
        // 식사 정보 가져오기
        return spotDetailResponseMapper.toDto(spot);
    }

    @Override
    @Transactional
    public BigInteger saveUserSpot(SecurityUser securityUser, ClientSpotDetailReqDto clientSpotDetailReqDto, BigInteger spotId) {
        // 유저를 조회한다.
        User user = commonService.getUser(securityUser);
        // 스팟을 가져온다.
        Spot spot = spotRepository.findById(spotId).orElseThrow(
                () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
        );
        // 유저가 스팟 그룹에 등록되었는지 검사한다.
        Group group = spot.getGroup();
        List<UserGroup> groups = userGroupRepository.findAllByUserAndClientStatus(user, ClientStatus.BELONG);
        UserGroup userGroup = groups.stream().filter(v -> v.getGroup().equals(group))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.CLIENT_NOT_FOUND));
        // 유저 Default 스팟을 저장한다.
        UserSpot userSpot = userSpotRepository.save(UserSpot.builder()
                .user(user)
                .clientType(ClientType.APARTMENT)
                .spot(spot)
                .build());
        // 상세 주소 저장 및 업데이트
        userGroup.updateHo(clientSpotDetailReqDto.getHo());
        user.updateUserSpot(userSpot);
        return userSpot.getSpot().getId();

    }

    @Override
    @Transactional
    public Integer withdrawClient(SecurityUser securityUser, BigInteger clientId) {
        // 유저를 조회한다.
        User user = commonService.getUser(securityUser);
        List<UserGroup> groups = userGroupRepository.findAllByUserAndClientStatus(user, ClientStatus.BELONG);
        // 유저가 해당 아파트 스팟 그룹에 등록되었는지 검사한다.
        Group group = groupRepository.findById(clientId).orElseThrow(
                () -> new ApiException(ExceptionEnum.CLIENT_NOT_FOUND)
        );
        UserGroup userGroup = groups.stream().filter(v -> v.getGroup().equals(group))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.CLIENT_NOT_FOUND));
        // 유저 그룹 상태를 탈퇴로 만든다.
        userGroup.updateStatus(ClientStatus.WITHDRAWAL);

        // 다른 그룹이 존재하는지 여부에 따라 Return값 결정(스팟 선택 화면 || 그룹 신청 화면)
        return (groups.size() - 1 > 0) ? SpotStatus.NO_SPOT_BUT_HAS_CLIENT.getCode() : SpotStatus.NO_SPOT_AND_CLIENT.getCode();
    }

    @Override
    @Transactional
    public List<ApartmentResponseDto> getApartments(SecurityUser securityUser) {
        List<Apartment> apartments = apartmentRepository.findAll();
        List<ApartmentResponseDto> apartmentResponseDtos = new ArrayList<>();
        for (Apartment apartment : apartments) {
            apartmentResponseDtos.add(apartmentMapper.toDto(apartment));
        }
        return apartmentResponseDtos;
    }
}
