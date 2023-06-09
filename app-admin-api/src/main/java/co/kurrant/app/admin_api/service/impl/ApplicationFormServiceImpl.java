package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.address.repository.QRegionRepository;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.CreateRequestDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.ListResponseDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.RequestedMySpotDetailDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.filter.FilterDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.filter.FilterInfo;
import co.dalicious.domain.application_form.dto.share.ShareSpotDto;
import co.dalicious.domain.application_form.entity.RequestedMySpotZones;
import co.dalicious.domain.application_form.entity.RequestedShareSpot;
import co.dalicious.domain.application_form.mapper.RequestedMySpotZonesMapper;
import co.dalicious.domain.application_form.mapper.RequestedShareSpotMapper;
import co.dalicious.domain.application_form.repository.QRequestedMySpotZonesRepository;
import co.dalicious.domain.application_form.repository.QRequestedShareSpotRepository;
import co.dalicious.domain.application_form.repository.RequestedMySpotZonesRepository;
import co.dalicious.domain.application_form.repository.RequestedShareSpotRepository;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.MealInfoRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.repository.UserGroupRepository;
import co.dalicious.integration.client.user.entity.MySpot;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.integration.client.user.entity.Region;
import co.dalicious.integration.client.user.mapper.MySpotZoneMapper;
import co.dalicious.integration.client.user.mapper.MySpotZoneMealInfoMapper;
import co.dalicious.integration.client.user.mapper.UserGroupMapper;
import co.dalicious.integration.client.user.reposiitory.QMySpotRepository;
import co.dalicious.domain.client.repository.QMySpotZoneRepository;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.admin_api.service.ApplicationFormService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationFormServiceImpl implements ApplicationFormService {

    private final QRequestedMySpotZonesRepository qRequestedMySpotZonesRepository;
    private final RequestedMySpotZonesMapper requestedMySpotZonesMapper;
    private final RequestedMySpotZonesRepository requestedMySpotZonesRepository;
    private final QMySpotRepository qMySpotRepository;
    private final MealInfoRepository mealInfoRepository;
    private final QMySpotZoneRepository qMySpotZoneRepository;
    private final QRegionRepository qRegionRepository;
    private final MySpotZoneMapper mySpotZoneMapper;
    private final GroupRepository groupRepository;
    private final MySpotZoneMealInfoMapper mySpotZoneMealInfoMapper;
    private final UserGroupMapper userGroupMapper;
    private final UserGroupRepository userGroupRepository;
    private final RequestedShareSpotMapper requestedShareSpotMapper;
    private final RequestedShareSpotRepository requestedShareSpotRepository;
    private final QRequestedShareSpotRepository qRequestedShareSpotRepository;

    @Override
    @Transactional(readOnly = true)
    public FilterDto getAllListForFilter(Map<String, Object> parameters) {
        // 시/도, 군/구, 동/읍/리 별로 필터. - 군/구, 동/읍/리는 다중 필터 가능
        String city = parameters.get("city") == null || !parameters.containsKey("city") ? null : qRegionRepository.findCityNameById(BigInteger.valueOf(Integer.parseInt((String) parameters.get("city"))));
        String county = parameters.get("county") == null || !parameters.containsKey("county") ? null : qRegionRepository.findCountyNameById(BigInteger.valueOf(Integer.parseInt((String) parameters.get("county"))));
        List<String> villages = parameters.get("villages") == null || !parameters.containsKey("villages") ? null : qRegionRepository.findVillageNameById(StringUtils.parseBigIntegerList((String) parameters.get("villages")));

        List<FilterInfo> cityList = qRequestedMySpotZonesRepository.findAllCity();
        List<FilterInfo> countyList = qRequestedMySpotZonesRepository.findAllCountyByCity(city);
        List<FilterInfo> villageList = qRequestedMySpotZonesRepository.findAllVillageByCounty(city, county);
        List<FilterInfo> zipcodeList = qRequestedMySpotZonesRepository.findAllZipcodeByCityAndCountyAndVillage(city, county, villages);

        return requestedMySpotZonesMapper.toFilterDto(cityList, countyList, villageList, zipcodeList);
    }

    @Override
    @Transactional(readOnly = true)
    public ListItemResponseDto<ListResponseDto> getAllMySpotRequestList(Map<String, Object> parameters, Integer limit, Integer page, OffsetBasedPageRequest pageable) {
        String city = parameters.get("city") == null || !parameters.containsKey("city") ? null : qRegionRepository.findCityNameById(BigInteger.valueOf(Integer.parseInt((String) parameters.get("city"))));
        String county = parameters.get("county") == null || !parameters.containsKey("county") ? null : qRegionRepository.findCountyNameById(BigInteger.valueOf(Integer.parseInt((String) parameters.get("county"))));
        List<String> villages = parameters.get("villages") == null || !parameters.containsKey("villages") ? null : qRegionRepository.findVillageNameById(StringUtils.parseBigIntegerList((String) parameters.get("villages")));
        List<String> zipcodes = parameters.get("zipcode") == null || !parameters.containsKey("zipcode") ? null : qRegionRepository.findZipcodeById(StringUtils.parseBigIntegerList((String) parameters.get("zipcode")));
        Integer minUserCount = parameters.get("min") == null || !parameters.containsKey("min") ? null : Integer.valueOf(String.valueOf(parameters.get("min")));
        Integer maxUserCount = parameters.get("max") == null || !parameters.containsKey("max") ? null : Integer.valueOf(String.valueOf(parameters.get("max")));

        Page<RequestedMySpotZones> requestedMySpotZonesList = qRequestedMySpotZonesRepository.findAllRequestedMySpotZonesByFilter(city, county, villages, zipcodes, minUserCount, maxUserCount, limit, page, pageable);
        List<ListResponseDto> listResponseDtos = new ArrayList<>();
        if(requestedMySpotZonesList == null || requestedMySpotZonesList.isEmpty()) {
            return ListItemResponseDto.<ListResponseDto>builder().items(listResponseDtos).limit(pageable.getPageSize()).total(0L).count(0).offset(pageable.getOffset()).build();
        }

        listResponseDtos = requestedMySpotZonesList.stream().map(requestedMySpotZonesMapper::toListResponseDto).toList();
        listResponseDtos = listResponseDtos.stream()
                .sorted(Comparator.comparing(ListResponseDto::getRequestUserCount)
                        .thenComparing(ListResponseDto::getZipcode))
                .collect(Collectors.toList());

        return ListItemResponseDto.<ListResponseDto>builder().items(listResponseDtos)
                .limit(pageable.getPageSize()).total((long) requestedMySpotZonesList.getTotalPages())
                .count(requestedMySpotZonesList.getNumberOfElements()).offset(pageable.getOffset()).build();
    }


    @Override
    @Transactional
    public void createMySpotRequest(CreateRequestDto createRequestDto) {
        // 이미 동일한 우편번호가 이미 존재하는지 체크
        RequestedMySpotZones existRequestedMySpotZones = qRequestedMySpotZonesRepository.findRequestedMySpotZoneByZipcode(createRequestDto.getZipcode());
        MySpotZone existMySpotZone = qMySpotZoneRepository.findExistMySpotZoneByZipcode(createRequestDto.getZipcode());
        if(existRequestedMySpotZones != null || existMySpotZone != null ) throw new ApiException(ExceptionEnum.ALREADY_EXIST_REQUEST);

        // 동일한 우편번호가 없으면? -> 생성
        Region region = qRegionRepository.findRegionByZipcodeAndCountyAndVillage(createRequestDto.getZipcode(), createRequestDto.getCounty(), createRequestDto.getVillage());
        if(region == null) throw new ApiException(ExceptionEnum.NOT_FOUND_REGION);
        RequestedMySpotZones requestedMySpotZones = requestedMySpotZonesMapper.toRequestedMySpotZones(createRequestDto.getWaitingUserCount(), createRequestDto.getMemo(), region, null);
        requestedMySpotZonesRepository.save(requestedMySpotZones);
    }

    @Override
    @Transactional
    public void updateMySpotRequest(RequestedMySpotDetailDto requestedMySpotDetailDto) {
        RequestedMySpotZones existRequestedMySpotZones = requestedMySpotZonesRepository.findById(requestedMySpotDetailDto.getId())
                .orElseThrow(() -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND));

        Region region = qRegionRepository.findRegionByZipcodeAndCountyAndVillage(requestedMySpotDetailDto.getZipcode(), requestedMySpotDetailDto.getCounty(), requestedMySpotDetailDto.getVillage());
        if(region == null) throw new ApiException(ExceptionEnum.NOT_FOUND_REGION);
        existRequestedMySpotZones.updateRequestedMySpotZones(requestedMySpotDetailDto, region);
    }

    @Override
    @Transactional
    public void deleteMySpotRequest(List<BigInteger> ids) {
        List<RequestedMySpotZones> existRequestedMySpotZones = qRequestedMySpotZonesRepository.findRequestedMySpotZonesByIds(ids);
        requestedMySpotZonesRepository.deleteAll(existRequestedMySpotZones);

    }

    @Override
    @Transactional
    public void createMySpotZonesFromRequest(List<BigInteger> ids) {
        List<RequestedMySpotZones> existRequestedMySpotZones = qRequestedMySpotZonesRepository.findRequestedMySpotZonesByIds(ids);

        // 마이스팟 생성
        MySpotZone mySpotZone = mySpotZoneMapper.toMySpotZone(existRequestedMySpotZones);
        // 지역에 마이스팟 fk
        List<Region> regions = existRequestedMySpotZones.stream().map(RequestedMySpotZones::getRegion).toList();
        regions.forEach(region -> region.updateMySpotZone(mySpotZone.getId()));

        // mealInfo 생성
        String defaultTime = "00:00";
        String defaultDays = "월, 화, 수, 목, 금, 토, 일";

        List<MealInfo> mealInfoList = mySpotZone.getDiningTypes().stream()
                .map(diningType -> {
                    String mealTime = switch (diningType) {
                        case MORNING -> "07:00";
                        case LUNCH -> "12:00";
                        case DINNER -> "19:00";
                    };

                    return mySpotZoneMealInfoMapper.toMealInfo(mySpotZone, diningType, DateUtils.stringToLocalTime(mealTime), defaultTime, defaultDays, defaultTime);
                })
                .collect(Collectors.toList());

        groupRepository.save(mySpotZone);
        mealInfoRepository.saveAll(mealInfoList);

        // requestedMySpotZone을 가지고 있는 muSpot을 수정
        List<BigInteger> userIds = existRequestedMySpotZones.stream().flatMap(requestedMySpotZone -> requestedMySpotZone.getUserIds().stream()).toList();
        List<MySpot> mySpotList = qMySpotRepository.findMySpotByUserIds(userIds);
        mySpotList.forEach(mySpot -> mySpot.updateMySpotZone(mySpotZone));

        // userGroup
        List<User> users = mySpotList.stream().map(MySpot::getUser).toList();
        List<UserGroup> userGroups = users.stream().map(user -> userGroupMapper.toUserGroup(user, mySpotZone)).toList();
        userGroupRepository.saveAll(userGroups);

        // 신청된 마이스팟 존 삭제
        requestedMySpotZonesRepository.deleteAll(existRequestedMySpotZones);
    }

    @Override
    @Transactional
    public ListItemResponseDto<ShareSpotDto.Response> getAllShareSpotRequestList(Integer type, Integer limit, Integer page, OffsetBasedPageRequest pageable) {
        Page<RequestedShareSpot> requestedShareSpots = qRequestedShareSpotRepository.findAllByType(type, limit, page, pageable);
        return ListItemResponseDto.<ShareSpotDto.Response>builder().items(requestedShareSpotMapper.toDtos(requestedShareSpots))
                .limit(pageable.getPageSize()).total((long) requestedShareSpots.getTotalPages())
                .count(requestedShareSpots.getNumberOfElements()).offset(pageable.getOffset()).build();
    }

    @Override
    public void createShareSpotRequest(ShareSpotDto.AdminRequest request) throws ParseException {
        RequestedShareSpot requestedShareSpot = requestedShareSpotMapper.toEntity(request);
        requestedShareSpotRepository.save(requestedShareSpot);
    }

    @Override
    @Transactional
    public void updateShareSpotRequest(BigInteger applicationId, ShareSpotDto.AdminRequest request) throws ParseException {
       RequestedShareSpot requestedShareSpot = requestedShareSpotRepository.findById(applicationId)
               .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
       requestedShareSpotMapper.updateRequestedShareSpotFromRequest(request, requestedShareSpot);
    }

    @Override
    public void deleteShareSpotRequest(List<BigInteger> ids) {
        List<RequestedShareSpot> requestedShareSpots = requestedShareSpotRepository.findAllById(ids);
        requestedShareSpotRepository.deleteAll(requestedShareSpots);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean findRenewalMySpotRequest() {
        Integer requestedMySpotZones = qRequestedMySpotZonesRepository.findAlreadyExistMySpotZone().size();

        if(requestedMySpotZones > 0) return true;
        return false;
    }

    @Override
    @Transactional
    public void renewalMySpotRequest() {
        List<RequestedMySpotZones> requestedMySpotZones = qRequestedMySpotZonesRepository.findAlreadyExistMySpotZone();
        requestedMySpotZonesRepository.deleteAll(requestedMySpotZones);
    }


}
