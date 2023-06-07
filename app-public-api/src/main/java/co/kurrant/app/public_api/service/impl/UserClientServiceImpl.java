package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.address.dto.LocationDto;
import co.dalicious.domain.client.dto.OpenGroupResponseDto;
import co.dalicious.domain.client.entity.*;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.mapper.GroupResponseMapper;
import co.dalicious.domain.client.repository.*;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.entity.enums.ClientType;
import co.dalicious.domain.user.entity.enums.SpotStatus;
import co.dalicious.integration.client.user.entity.MySpot;
import co.dalicious.integration.client.user.entity.MySpotZone;
import co.dalicious.integration.client.user.mapper.UserSpotDetailResMapper;
import co.dalicious.domain.user.repository.UserGroupRepository;
import co.dalicious.domain.user.repository.UserSpotRepository;
import co.dalicious.domain.client.dto.ClientSpotDetailReqDto;
import co.dalicious.integration.client.user.dto.ClientSpotDetailResDto;
import co.dalicious.integration.client.user.mapper.UserSpotMapper;
import co.dalicious.integration.client.user.reposiitory.MySpotRepository;
import co.kurrant.app.public_api.service.UserUtil;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserClientService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserClientServiceImpl implements UserClientService {
    private final GroupResponseMapper groupResponseMapper;
    private final UserUtil userUtil;
    private final UserGroupRepository userGroupRepository;
    private final SpotRepository spotRepository;
    private final UserSpotRepository userSpotRepository;
    private final GroupRepository groupRepository;
    private final UserSpotDetailResMapper userSpotDetailResMapper;
    private final UserSpotMapper userSpotMapper;
    private final QGroupRepository qGroupRepository;

    private static final double EARTH_RADIUS = 6371.0;

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
    public List<OpenGroupResponseDto> getOpenGroupsAndApartments(SecurityUser securityUser, Map<String, Object> location) {
        Double latitude = Double.valueOf(String.valueOf(location.get("lat")));
        Double longitude = Double.valueOf(String.valueOf(location.get("long")));

        List<? extends Group> groups = qGroupRepository.findGroupByType(GroupDataType.OPEN_GROUP);
        List<OpenGroupResponseDto> openGroupResponseDtos = new ArrayList<>();

        Map<Group, List<Double>> locationMap = new HashMap<>();
        groups.forEach(group -> {
            String groupLocation = String.valueOf(group.getAddress().getLocation());

            List<Double> locationArr = new ArrayList<>();
            locationArr.add(Double.parseDouble(groupLocation.split(" ")[0]));
            locationArr.add(Double.parseDouble(groupLocation.split(" ")[1]));

            locationMap.put(group, locationArr);
        });
        List<Map.Entry<Group, Double>> sortedDataList = sortByDistance(locationMap, latitude, longitude);

        // 결과 출력
        System.out.println("가장 가까운 거리 순으로 데이터를 나열:");
        for (Map.Entry<Group, Double> entry : sortedDataList) {
            Group group = entry.getKey();
            double distance = entry.getValue();
            openGroupResponseDtos.add(groupResponseMapper.toOpenGroupDto((OpenGroup) group, distance));
            System.out.println(group + ": " + distance + "km");
        }

        return openGroupResponseDtos;
    }


    // 거리순으로 데이터 정렬
    private static List<Map.Entry<Group, Double>> sortByDistance(Map<Group, List<Double>> locationMap, double myLatitude, double myLongitude) {
        List<Map.Entry<Group, Double>> sortedDataList = new ArrayList<>(locationMap.size());

        for (Map.Entry<Group, List<Double>> entry : locationMap.entrySet()) {
            Group group = entry.getKey();
            List<Double> location = entry.getValue();
            double distance = calculateDistance(myLatitude, myLongitude, location.get(0), location.get(1));

            sortedDataList.add(new AbstractMap.SimpleEntry<>(group, distance));
        }

        sortedDataList.sort(Comparator.comparingDouble(Map.Entry::getValue)); // 거리순으로 정렬

        return sortedDataList;
    }

    // 거리 계산 (하버사인 공식 사용)
    private static double calculateDistance(double myLatitude, double myLongitude, double groupLat, double groupLon) {
        double myLatitudeRad = Math.toRadians(myLatitude);
        double myLongitudeRad = Math.toRadians(myLongitude);
        double groupLatRad = Math.toRadians(groupLat);
        double groupLonRad = Math.toRadians(groupLon);

        double dlon = groupLonRad - myLongitudeRad;
        double dlat = groupLatRad - myLatitudeRad;

        double x = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(myLatitudeRad) * Math.cos(groupLatRad) * Math.pow(Math.sin(dlon / 2), 2);
        double y = 2 * Math.atan2(Math.sqrt(x), Math.sqrt(1 - x));

        return EARTH_RADIUS * y;
    }

}
