package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.client.dto.OpenGroupDetailDto;
import co.dalicious.domain.client.dto.OpenGroupResponseDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.mapper.GroupResponseMapper;
import co.dalicious.domain.client.mapper.OpenGroupMapper;
import co.dalicious.domain.client.repository.*;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.entity.enums.ClientType;
import co.dalicious.domain.user.entity.enums.SpotStatus;
import co.dalicious.integration.client.user.entity.MySpot;
import co.dalicious.integration.client.user.mapper.UserSpotDetailResMapper;
import co.dalicious.domain.user.repository.UserGroupRepository;
import co.dalicious.domain.user.repository.UserSpotRepository;
import co.dalicious.integration.client.user.dto.ClientSpotDetailResDto;
import co.dalicious.integration.client.user.mapper.UserSpotMapper;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DistanceUtil;
import co.kurrant.app.public_api.service.UserUtil;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserClientService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserClientServiceImpl implements UserClientService {
    private final UserUtil userUtil;
    private final UserGroupRepository userGroupRepository;
    private final SpotRepository spotRepository;
    private final UserSpotRepository userSpotRepository;
    private final GroupRepository groupRepository;
    private final UserSpotDetailResMapper userSpotDetailResMapper;
    private final UserSpotMapper userSpotMapper;
    private final QGroupRepository qGroupRepository;
    private final OpenGroupMapper openGroupMapper;

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
    //TODO: 오픈 스팟 수정
    public BigInteger selectUserSpot(SecurityUser securityUser, BigInteger spotId) {
        // 유저를 조회한다.
        User user = userUtil.getUser(securityUser);

        // default spot update to false - 기존 디폴트 스팟을 false로 변경
        UserSpot defaultSpot = user.getDefaultUserSpot();
        if (defaultSpot != null) defaultSpot.updateDefault(false);

        // 스팟 조회
        Spot spot = spotRepository.findById(spotId).orElseThrow(() -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND));
        // 해당 스팟의 그룹이 유저 그룹에서 활성 상태인지 확인
        Group group = spot.getGroup();
        List<UserGroup> groups = userGroupRepository.findAllByUserAndClientStatus(user, ClientStatus.BELONG);
        groups.stream().filter(v -> v.getGroup().equals(group))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));

        // 유저가 가진 스팟 중에 해당 스팟이 있는지 확인
        UserSpot userSpot = user.getUserSpots().stream()
                .filter(s -> (s instanceof MySpot mySpot && mySpot.getId().equals(spotId) && mySpot.getIsActive()) ||
                        (s.getSpot() != null && s.getSpot().equals(spot)))
                .findAny()
                .orElse(null);

        ClientType spotClientType = (spot instanceof CorporationSpot) ? ClientType.CORPORATION : ClientType.OPEN_GROUP;

        // 유저 스팟에 등록되지 않은 경우
        if(userSpot == null) {
            UserSpot newUserSpot = userSpotMapper.toUserSpot(spot, user, true, spotClientType);
            userSpotRepository.save(newUserSpot);
            return spot.getId();
        }
        // 마이 스팟의 경우
        if(userSpot instanceof MySpot mySpot) {
            mySpot.updateDefault(true);
            return mySpot.getId();
        }
        // 이미 유저 스팟으로 등록된 경우 - corporation & open spot
        userSpot.updateDefault(true);
        return spot.getId();
    }

    @Override
    @Transactional
    public Integer withdrawClient(SecurityUser securityUser, BigInteger spotId) {
        // 유저를 조회한다.
        User user = userUtil.getUser(securityUser);
        // 유저의 스팟리스트에서 해당하는 spot을 찾는다.
        UserSpot userSpot = user.getUserSpots().stream()
                .filter(spot -> (spot instanceof MySpot mySpot && mySpot.getId().equals(spotId)) || (spot.getSpot() != null && spot.getSpot().getId().equals(spotId)))
                .findFirst()
                .orElseThrow(() -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND));

        // 마이스팟이면
        if(userSpot instanceof MySpot mySpot) {
            // 관련 마이스팟존 탈퇴처리
            Optional<UserGroup> userGroup = user.getGroups().stream()
                    .filter(group -> group.getGroup().equals(mySpot.getMySpotZone()))
                    .findAny();
            userGroup.ifPresent(g -> g.updateStatus(ClientStatus.WITHDRAWAL));

            mySpot.updateMySpotForDelete();
            return (user.getGroups().size() - 1 > 0) ? SpotStatus.NO_SPOT_BUT_HAS_CLIENT.getCode() : SpotStatus.NO_SPOT_AND_CLIENT.getCode();
        }

        // 유저 그룹 비활성으로 변경하고 유저스팟 삭제
        Optional<UserGroup> userGroup = user.getGroups().stream()
                .filter(group -> group.getGroup().equals(userSpot.getSpot().getGroup()))
                .findAny();
        userGroup.ifPresent(g -> g.updateStatus(ClientStatus.WITHDRAWAL));
        userSpotRepository.delete(userSpot);

        // 다른 그룹이 존재하는지 여부에 따라 Return값 결정(스팟 선택 화면 || 그룹 신청 화면)
        return (user.getGroups().size() - 1 > 0) ? SpotStatus.NO_SPOT_BUT_HAS_CLIENT.getCode() : SpotStatus.NO_SPOT_AND_CLIENT.getCode();
    }

    @Override
    @Transactional
    public OpenGroupDetailDto getOpenSpotDetail(SecurityUser securityUser, BigInteger groupId) {
        User user = userUtil.getUser(securityUser);
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));

        return openGroupMapper.toOpenGroupDetailDto((OpenGroup) group);
    }

    @Override
    @Transactional
    public ListItemResponseDto<OpenGroupResponseDto> getOpenGroupsAndApartments(SecurityUser securityUser, Map<String, Object> location, Map<String, Object> parameters, OffsetBasedPageRequest pageable) {
        Boolean isRestriction = parameters.get("isRestriction") == null || !parameters.containsKey("isRestriction") ? null : Boolean.valueOf(String.valueOf(parameters.get("isRestriction")));
        DiningType diningType = parameters.get("diningType") == null || !parameters.containsKey("diningType") ? null : DiningType.ofCode(Integer.parseInt(String.valueOf(parameters.get("diningType"))));
        Double latitude = Double.valueOf(String.valueOf(location.get("lat")));
        Double longitude = Double.valueOf(String.valueOf(location.get("long")));

        Page<Group> groups = qGroupRepository.findOPenGroupByFilter(isRestriction, diningType, pageable);
        List<OpenGroupResponseDto> openGroupResponseDtos = new ArrayList<>();
        if(groups.isEmpty() || groups == null) {
            return ListItemResponseDto.<OpenGroupResponseDto>builder().items(openGroupResponseDtos).limit(pageable.getPageSize()).total(0L).count(0).offset(0L).isLast(true).build();
        }

        Map<BigInteger, List<Double>> locationMap = new HashMap<>();
        groups.forEach(group -> {
            List<Double> locationArr = DistanceUtil.parsLocationToDouble(group.getAddress().locationToString());
            locationMap.put(group.getId(), locationArr);
        });
        List<Map.Entry<BigInteger, Double>> sortedDataList = DistanceUtil.sortByDistance(locationMap, latitude, longitude);

        // 결과 출력
        for (Map.Entry<BigInteger, Double> entry : sortedDataList) {
            Group group = groups.stream().filter(g -> g.getId().equals(entry.getKey())).findAny().orElse(null);
            if(group == null) continue;
            double distance = entry.getValue();
            openGroupResponseDtos.add(openGroupMapper.toOpenGroupDto((OpenGroup) group, distance));
        }

        return ListItemResponseDto.<OpenGroupResponseDto>builder().items(openGroupResponseDtos).limit(pageable.getPageSize()).total((long) groups.getTotalPages())
                .count(groups.getNumberOfElements()).offset(pageable.getOffset()).isLast(groups.isLast()).build();
    }

}
