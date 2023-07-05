package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.client.dto.*;
import co.dalicious.domain.client.dto.corporation.CorporationResponseDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.mapper.OpenGroupMapper;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.MySpotZoneRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.client.repository.SpotRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.UserSpot;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.entity.enums.SpotStatus;
import co.dalicious.domain.user.repository.UserGroupRepository;
import co.dalicious.domain.user.repository.UserSpotRepository;
import co.dalicious.domain.user.mapper.UserGroupMapper;
import co.dalicious.domain.user.mapper.UserSpotDetailResMapper;
import co.dalicious.domain.user.mapper.UserSpotMapper;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DistanceUtil;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserClientService;
import co.kurrant.app.public_api.service.UserUtil;
import exception.ApiException;
import exception.CustomException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
    private final UserGroupMapper userGroupMapper;
    private final MySpotZoneRepository mySpotZoneRepository;

    @Override
    @Transactional
    public ClientSpotDetailResDto getSpotDetail(SecurityUser securityUser, BigInteger spotId) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);
        Spot spot = spotRepository.findById(spotId).orElseThrow(() -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND));
        Group group = (Group) Hibernate.unproxy(spot.getGroup());
        isGroupMember(user, group);

        UserSpot userSpot = getUserSpot(spotId, user);
      
        if(userSpot == null) throw new ApiException(ExceptionEnum.NOT_SET_SPOT);
        return userSpotDetailResMapper.toDto(userSpot);
    }

    @Override
    @Transactional
    public GroupDetailDto getGroupDetail(SecurityUser securityUser, BigInteger groupId) {
        User user = userUtil.getUser(securityUser);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));
        isGroupMember(user, group);
        return userGroupMapper.toGroupDetailDto(group, user);
    }

    @Override
    @Transactional
    public BigInteger selectUserSpot(SecurityUser securityUser, BigInteger spotId) {
        // 유저를 조회한다.
        User user = userUtil.getUser(securityUser);

        // default spot update to false - 기존 디폴트 스팟을 false로 변경
        resetDefaultSpot(user);

        // 유저가 가진 스팟 중에 해당 스팟이 있는지 확인
        UserSpot userSpot = getUserSpot(spotId, user);

        Spot spot = spotRepository.findById(spotId).orElseThrow(() -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND));
        // MySpot의 userId 와 일치하는지 검증
        if (spot instanceof MySpot mySpot && !user.getId().equals(mySpot.getUserId())) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }
        Group group = (Group) Hibernate.unproxy(spot.getGroup());
        isGroupMember(user, group);

        // 유저 스팟에 등록되지 않은 경우
        if (userSpot == null) {
            userSpot = registerNewUserSpot(spot, user);
        }
        userSpot.updateDefault(true);
        return spot.getId();

    }

    //TODO :  추후 마이 스팟 이용 갯수가 늘어나면 spotId 삭제 방식으로 변경 필요
    @Override
    @Transactional
    public Integer withdrawClient(SecurityUser securityUser, BigInteger clientId) {
        // 유저를 조회한다.
        User user = userUtil.getUser(securityUser);
        List<UserGroup> groups = userGroupRepository.findAllByUserAndClientStatus(user, ClientStatus.BELONG);
        // 유저가 해당 아파트 스팟 그룹에 등록되었는지 검사한다.
        Group group = groupRepository.findById(clientId).orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));
        Group unproxiedGroup = (Group) Hibernate.unproxy(group);
        UserGroup userGroup = isGroupMember(user, unproxiedGroup);
        // 유저 그룹 상태를 탈퇴로 만든다.
        userGroup.updateStatus(ClientStatus.WITHDRAWAL);
        List<UserSpot> userSpots = user.getUserSpots();
        Optional<UserSpot> userSpot = userSpots.stream().filter(v -> v.getSpot().getGroup().equals(userGroup.getGroup()))
                .findAny();
        userSpot.ifPresent(v -> {
            if(unproxiedGroup instanceof MySpotZone mySpotZone) {
                List<MySpot> mySpots = mySpotZone.getSpots().stream()
                        .filter(s -> s instanceof MySpot mySpot && mySpot.getUserId().equals(user.getId()) && !mySpot.getIsDelete())
                        .map(s -> ((MySpot) s))
                        .toList();
                mySpots.forEach(MySpot::updateMySpotForDelete);
            }
            userSpotRepository.delete(v);
        });
        // 다른 그룹이 존재하는지 여부에 따라 Return값 결정(스팟 선택 화면 || 그룹 신청 화면)
        return (groups.size() - 1 > 0) ? SpotStatus.NO_SPOT_BUT_HAS_CLIENT.getCode() : SpotStatus.NO_SPOT_AND_CLIENT.getCode();

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
    public List<OpenGroupListForKeywordDto> getOpenGroupsForKeyword(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);

        List<Group> groups = qGroupRepository.findAllOpenGroup();
        List<OpenGroupListForKeywordDto> openGroupListForKeywordDtos = new ArrayList<>();
        if(groups.isEmpty()) return  openGroupListForKeywordDtos;

        return groups.stream().map(openGroupMapper::toOpenGroupListForKeywordDto).toList();
    }

    @Override
    @Transactional
    public List<CorporationResponseDto> getUserCorporation(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        List<UserGroup> userGroups = user.getActiveUserGroups().stream()
                .filter(g -> Hibernate.unproxy(g.getGroup()) instanceof Corporation)
                .toList();

        List<CorporationResponseDto> corporationResponseDtos = new ArrayList<>();
        if(userGroups.isEmpty()) return corporationResponseDtos;

        return userGroupMapper.toCorporationResponseDtoList(userGroups);
    }

    @Override
    @Transactional
    public void updateMySpotInformation(SecurityUser securityUser, BigInteger mySpotZoneId, String target, String value) {
        User user = userUtil.getUser(securityUser);
        MySpotZone mySpotZone = mySpotZoneRepository.findById(mySpotZoneId).orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));
        MySpot mySpot = mySpotZone.getMySpot(user.getId());
        if(mySpot == null) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }
        if(target.equals("name")) {
            mySpot.updateName(value);
            return;
        }
        if(target.equals("phone")) {
            mySpot.updatePhone(value);
            return;
        }
        throw new CustomException(HttpStatus.BAD_REQUEST, "CE4000016", "파라미터(target)의 명칭이 일치하지 않습니다.");
    }

    @Override
    @Transactional
    public ListItemResponseDto<OpenGroupResponseDto> getOpenGroups(SecurityUser securityUser, Map<String, Object> location, Map<String, Object> parameters, OffsetBasedPageRequest pageable) {
        Boolean isRestriction = parameters.get("isRestriction") == null || !parameters.containsKey("isRestriction") ? null : Boolean.valueOf(String.valueOf(parameters.get("isRestriction")));
        List<DiningType> diningType = parameters.get("diningType") == null || !parameters.containsKey("diningType") ? null : StringUtils.parseIntegerList(String.valueOf(parameters.get("diningType"))).stream().map(DiningType::ofCode).toList();
        double latitude = Double.parseDouble(String.valueOf(location.get("lat")));
        double longitude = Double.parseDouble(String.valueOf(location.get("long")));

        Page<Group> groups = qGroupRepository.findOPenGroupByFilter(isRestriction, diningType, pageable, latitude, longitude);
        List<OpenGroupResponseDto> openGroupResponseDtos = new ArrayList<>();
        if (groups.isEmpty()) {
            return ListItemResponseDto.<OpenGroupResponseDto>builder().items(openGroupResponseDtos).limit(pageable.getPageSize()).total(0L).count(0).offset(0L).isLast(true).build();
        }

        Map<BigInteger, List<Double>> locationMap = new HashMap<>();
        groups.forEach(group -> {
            List<Double> locationArr = DistanceUtil.parsLocationToDouble(group.getAddress().locationToString());
            locationMap.put(group.getId(), locationArr);
        });
        List<Map.Entry<BigInteger, Double>> sortedDataList = DistanceUtil.sortByDistance(locationMap, latitude, longitude);

        //  결과 출력
        for (Map.Entry<BigInteger, Double> entry : sortedDataList) {
            Group group = groups.stream().filter(g -> g.getId().equals(entry.getKey())).findAny().orElse(null);
            if (group == null) continue;
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

    private UserGroup isGroupMember(User user, Group group) {
        List<UserGroup> groups = userGroupRepository.findAllByUserAndClientStatus(user, ClientStatus.BELONG);
        return groups.stream()
                .filter(v -> Hibernate.unproxy(v.getGroup()).equals(group))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));
    }

    private UserSpot getUserSpot(BigInteger spotId, User user) {
        return user.getUserSpots().stream()
                .filter(s -> s.getSpot() != null && s.getSpot().getId().equals(spotId))
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
