package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.client.dto.OpenGroupDetailDto;
import co.dalicious.domain.client.dto.OpenGroupResponseDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.mapper.GroupResponseMapper;
import co.dalicious.domain.client.mapper.OpenGroupMapper;
import co.dalicious.domain.client.repository.*;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.entity.enums.SpotStatus;
import co.dalicious.integration.client.user.entity.MySpot;
import co.dalicious.integration.client.user.mapper.UserSpotDetailResMapper;
import co.dalicious.domain.user.repository.UserGroupRepository;
import co.dalicious.domain.user.repository.UserSpotRepository;
import co.dalicious.integration.client.user.dto.ClientSpotDetailResDto;
import co.dalicious.integration.client.user.mapper.UserSpotMapper;
import co.dalicious.integration.client.user.reposiitory.MySpotRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DistanceUtil;
import co.dalicious.system.util.StringUtils;
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
    private final MySpotRepository mySpotRepository;

    @Override
    @Transactional
    public ClientSpotDetailResDto getSpotDetail(SecurityUser securityUser, BigInteger spotId, Integer clientType) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        GroupDataType groupDataType = GroupDataType.ofCode(clientType);

        UserSpot userSpot = getUserSpot(spotId, user);

        if(!groupDataType.equals(GroupDataType.MY_SPOT)) {
            // 스팟 정보 가져오기
            Spot spot = spotRepository.findById(spotId).orElseThrow(() -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND));
            Group group = spot.getGroup();
            isGroupMember(user, group);

            return userSpotDetailResMapper.toDto(userSpot);
        }
        else if (userSpot != null) {
            return userSpotDetailResMapper.toDto(userSpot);
        }

        throw new ApiException(ExceptionEnum.NOT_SET_SPOT);
    }

    @Override
    @Transactional
    public BigInteger selectUserSpot(SecurityUser securityUser, Integer groupType, BigInteger spotId) {
        // 유저를 조회한다.
        User user = userUtil.getUser(securityUser);

        // default spot update to false - 기존 디폴트 스팟을 false로 변경
        resetDefaultSpot(user);

        GroupDataType groupDataType = GroupDataType.ofCode(groupType);

        // 유저가 가진 스팟 중에 해당 스팟이 있는지 확인
        UserSpot userSpot = getUserSpot(spotId, user);

        if(!groupDataType.equals(GroupDataType.MY_SPOT)) {
            Spot spot = spotRepository.findById(spotId).orElseThrow(() -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND));
            Group group = spot.getGroup();
            isGroupMember(user, group);

            // 유저 스팟에 등록되지 않은 경우
            if(userSpot == null) {
                userSpot = registerNewUserSpot(spot, user);
            }
            userSpot.updateDefault(true);
            return spot.getId();
        }
        if(userSpot != null){
            MySpot mySpot = mySpotRepository.findById(spotId).orElseThrow(() -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND));
            mySpot.updateDefault(true);
            return mySpot.getId();
        }
        throw new ApiException(ExceptionEnum.SPOT_NOT_FOUND);
    }

    @Override
    @Transactional
    public Integer withdrawClient(SecurityUser securityUser, BigInteger spotId) {
        // 유저를 조회한다.
        User user = userUtil.getUser(securityUser);
        // 유저의 스팟리스트에서 해당하는 spot을 찾는다.
        UserSpot userSpot = getUserSpot(spotId, user);

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
    public ListItemResponseDto<OpenGroupResponseDto> getOpenGroups(SecurityUser securityUser, Map<String, Object> location, Map<String, Object> parameters, OffsetBasedPageRequest pageable) {
        Boolean isRestriction = parameters.get("isRestriction") == null || !parameters.containsKey("isRestriction") ? null : Boolean.valueOf(String.valueOf(parameters.get("isRestriction")));
        List<DiningType> diningType = parameters.get("diningType") == null || !parameters.containsKey("diningType") ? null : StringUtils.parseIntegerList(String.valueOf(parameters.get("diningType"))).stream().map(DiningType::ofCode).toList();
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

    private void resetDefaultSpot(User user) {
        UserSpot defaultSpot = user.getDefaultUserSpot();
        if (defaultSpot != null) defaultSpot.updateDefault(false);
    }

    private Group retrieveSpotGroup(BigInteger spotId, GroupDataType groupDataType) {
        Group group;

        if(!groupDataType.equals(GroupDataType.MY_SPOT)) {
            Spot spot = spotRepository.findById(spotId).orElseThrow(() -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND));
            group = spot.getGroup();
        } else {
            MySpot mySpot = mySpotRepository.findById(spotId).orElseThrow(() -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND));
            group = mySpot.getMySpotZone();
        }

        return group;
    }

    private void isGroupMember(User user, Group group) {
        List<UserGroup> groups = userGroupRepository.findAllByUserAndClientStatus(user, ClientStatus.BELONG);
        groups.stream()
                .filter(v -> v.getGroup().equals(group))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));
    }

    private UserSpot getUserSpot(BigInteger spotId, User user) {
        return user.getUserSpots().stream()
                .filter(s -> (s instanceof MySpot mySpot && mySpot.getId().equals(spotId) && mySpot.getIsActive()) ||
                        (s.getSpot() != null && s.getSpot().getId().equals(spotId)))
                .findAny()
                .orElse(null);
    }

    private UserSpot registerNewUserSpot(Spot spot, User user) {
        GroupDataType spotGroupDataType = (spot instanceof CorporationSpot) ? GroupDataType.CORPORATION : GroupDataType.OPEN_GROUP;
        UserSpot newUserSpot = userSpotMapper.toUserSpot(spot, user, true, spotGroupDataType);
        userSpotRepository.save(newUserSpot);

        return newUserSpot;
    }

}
