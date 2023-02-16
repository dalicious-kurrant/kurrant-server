package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.client.dto.ApartmentResponseDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.mapper.ApartmentListMapper;
import co.dalicious.domain.client.repository.*;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.entity.enums.ClientType;
import co.dalicious.domain.user.entity.enums.SpotStatus;
import co.dalicious.domain.user.mapper.UserSpotDetailResMapper;
import co.dalicious.domain.user.repository.UserGroupRepository;
import co.dalicious.domain.user.repository.UserSpotRepository;
import co.dalicious.domain.client.dto.ClientSpotDetailReqDto;
import co.dalicious.domain.client.dto.ClientSpotDetailResDto;
import co.kurrant.app.public_api.service.UserUtil;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserClientService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserClientServiceImpl implements UserClientService {
    private final ApartmentListMapper apartmentMapper;
    private final UserUtil userUtil;
    private final ApartmentRepository apartmentRepository;
    private final UserGroupRepository userGroupRepository;
    private final SpotRepository spotRepository;
    private final UserSpotRepository userSpotRepository;
    private final GroupRepository groupRepository;
    private final UserSpotDetailResMapper userSpotDetailResMapper;

    @Override
    @Transactional
    public ClientSpotDetailResDto getSpotDetail(SecurityUser securityUser, BigInteger spotId) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);
        // 유저가 가지고 있는 유저 스팟 가져오기
        List<UserSpot> userSpots = user.getUserSpots();
        // 스팟 정보 가져오기
        Spot spot = spotRepository.findById(spotId).orElseThrow(
                () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
        );
        // 유저가 등록한 스팟인지 검증
        UserSpot userSpot = userSpots.stream()
                .filter(v -> v.getSpot().equals(spot))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_SET_SPOT));
        // 스팟에 속한 그룹 가져오기
        Group group = spot.getGroup();
        // 유저가 그룹에 속하지 않는다면 예외처리
        user.getGroups().stream()
                .filter(g -> g.getGroup().equals(group) && g.getClientStatus().equals(ClientStatus.BELONG))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.UNAUTHORIZED));
        // 식사 정보 가져오기
        return userSpotDetailResMapper.toDto(userSpot);
    }

    @Override
    @Transactional
    public BigInteger selectUserSpot(SecurityUser securityUser, BigInteger spotId) {
        // 유저를 조회한다.
        User user = userUtil.getUser(securityUser);
        // 스팟을 가져온다.
        Spot spot = spotRepository.findById(spotId).orElseThrow(
                () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
        );
        ClientType spotClientType = (spot instanceof ApartmentSpot) ? ClientType.APARTMENT : ClientType.CORPORATION;
        // 유저가 스팟 그룹에 등록되었는지 검사한다.
        Group group = spot.getGroup();
        List<UserGroup> groups = userGroupRepository.findAllByUserAndClientStatus(user, ClientStatus.BELONG);
        groups.stream().filter(v -> v.getGroup().equals(group))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));
        List<UserSpot> userSpots = user.getUserSpots();

        // 등록하려는 스팟이 아파트일 경우
        if (spotClientType.equals(ClientType.APARTMENT)) {
            // 아파트 스팟이 존재할 경우, 이전에 등록된 스팟과 일치하는지 확인한다.
            Optional<UserSpot> userSpot = userSpots.stream().filter(v -> v.getSpot().equals(spot) && v.getClientType().equals(ClientType.APARTMENT)).findAny();
            if (userSpot.isPresent()) {
                userSpots.forEach(us -> us.updateDefault(false));
                userSpot.get().updateDefault(true);
                return spot.getId();
            }
            return null;
        }
        // 등록하려는 스팟이 기업일 경우
        // 유저 스팟에서 기업 스팟이 존재하는지 확인한다.
        Optional<UserSpot> corporationUser = userSpots.stream().filter(v -> v.getClientType().equals(ClientType.CORPORATION)).findAny();
        // 기업이 존재할 경우 업데이트 한다.
        if (corporationUser.isPresent()) {
            UserSpot corporationUserSpot = corporationUser.get();
            userSpots.forEach(us -> us.updateDefault(false));
            if (!corporationUserSpot.getSpot().equals(spot)) {
                corporationUserSpot.updateSpot(spot);
            }
            corporationUser.get().updateDefault(true);
            return spot.getId();
        }
        // 기업 스팟이 존재하지 않을 경우 유저 스팟을 저장한다.
        else {
            UserSpot newUserSpot = UserSpot.builder()
                    .spot(spot)
                    .user(user)
                    .clientType(ClientType.CORPORATION)
                    .build();
            newUserSpot.updateDefault(true);
            userSpotRepository.save(newUserSpot);
            return spot.getId();
        }
    }

    @Override
    @Transactional
    public BigInteger saveUserDefaultSpot(SecurityUser securityUser, ClientSpotDetailReqDto clientSpotDetailReqDto, BigInteger spotId) {
        // 유저 정보를 가져온다.
        User user = userUtil.getUser(securityUser);
        // 스팟을 가져온다.
        Spot spot = spotRepository.findById(spotId).orElseThrow(
                () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
        );
        // 기존에 존재하는 아파트 스팟이 있는지 확인한다.
        Optional<UserSpot> userSpot = user.getUserSpots().stream()
                .filter(v -> v.getClientType().equals(ClientType.APARTMENT))
                .findAny();
        // 존재하는 아파트 스팟이 있다면, 업데이트 시켜준다.
        if(userSpot.isPresent()) {
            UserSpot selectedUserSpot = userSpot.get();
            user.getUserSpots().stream().filter(v -> !v.equals(selectedUserSpot)).forEach(us -> us.updateDefault(false));
            selectedUserSpot.updateSpot(spot);
            selectedUserSpot.updateHo(clientSpotDetailReqDto.getHo());
            selectedUserSpot.updateDefault(true);
            return selectedUserSpot.getId();
        }
        // 존재하는 아파트가 없다면 저장한다.
        UserSpot newUserSpot = UserSpot.builder()
                .clientType(ClientType.APARTMENT)
                .isDefault(true)
                .user(user)
                .spot(spot)
                .build();
        newUserSpot.updateHo(clientSpotDetailReqDto.getHo());

        return userSpotRepository.save(newUserSpot).getId();

    }

    @Override
    @Transactional
    public BigInteger updateUserHo(SecurityUser securityUser, ClientSpotDetailReqDto spotDetailReqDto, BigInteger spotId) {
        // 유저 정보를 가져온다.
        User user = userUtil.getUser(securityUser);
        // 스팟을 가져온다.
        Spot spot = spotRepository.findById(spotId).orElseThrow(
                () -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND)
        );
        // 기존에 존재하는 아파트 스팟이 있는지 확인한다.
        Optional<UserSpot> userSpot = user.getUserSpots().stream()
                .filter(v -> v.getSpot().equals(spot))
                .findAny();
        if(userSpot.isEmpty()) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }
        userSpot.get().updateHo(spotDetailReqDto.getHo());
        return userSpot.get().getId();
    }

    @Override
    @Transactional
    public Integer withdrawClient(SecurityUser securityUser, BigInteger clientId) {
        // 유저를 조회한다.
        User user = userUtil.getUser(securityUser);
        List<UserGroup> groups = userGroupRepository.findAllByUserAndClientStatus(user, ClientStatus.BELONG);
        // 유저가 해당 아파트 스팟 그룹에 등록되었는지 검사한다.
        Group group = groupRepository.findById(clientId).orElseThrow(
                () -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND)
        );
        UserGroup userGroup = groups.stream().filter(v -> v.getGroup().equals(group))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));
        // 유저 그룹 상태를 탈퇴로 만든다.
        userGroup.updateStatus(ClientStatus.WITHDRAWAL);
        List<UserSpot> userSpots = user.getUserSpots();
        Optional<UserSpot> userSpot = userSpots.stream().filter(v -> v.getSpot().getGroup().equals(userGroup.getGroup()))
                .findAny();
        userSpot.ifPresent(userSpotRepository::delete);
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
