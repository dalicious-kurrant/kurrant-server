package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.filter.FilterInfo;
import co.dalicious.domain.application_form.entity.RequestedMySpotZones;
import co.dalicious.domain.application_form.repository.QRequestedMySpotZonesRepository;
import co.dalicious.domain.application_form.repository.RequestedMySpotZonesRepository;
import co.dalicious.domain.application_form.dto.requestMySpotZone.filter.FilterDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.CreateRequestDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.ListResponseDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.RequestedMySpotDetailDto;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.application_form.mapper.RequestedMySpotZonesMapper;
import co.dalicious.domain.client.repository.MealInfoRepository;
import co.dalicious.domain.client.repository.MySpotZoneRepository;
import co.dalicious.domain.client.repository.QMySpotZoneRepository;
import co.dalicious.domain.user.entity.MySpot;
import co.dalicious.domain.user.repository.QMySpotRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.admin_api.service.GroupRequestService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupRequestServiceImpl implements GroupRequestService {

    private final QRequestedMySpotZonesRepository qRequestedMySpotZonesRepository;
    private final RequestedMySpotZonesMapper requestedMySpotZonesMapper;
    private final RequestedMySpotZonesRepository requestedMySpotZonesRepository;
    private final QMySpotRepository qMySpotRepository;
    private final MySpotZoneRepository mySpotZoneRepository;
    private final MealInfoRepository mealInfoRepository;
    private final QMySpotZoneRepository qMySpotZoneRepository;

    @Override
    @Transactional(readOnly = true)
    public FilterDto getAllListForFilter(Map<String, Object> parameters) {
        // 시/도, 군/구, 동/읍/리 별로 필터. - 군/구, 동/읍/리는 다중 필터 가능
        String city = parameters.get("city") == null || !parameters.containsKey("city") ? null : qRequestedMySpotZonesRepository.findCityNameById(BigInteger.valueOf(Integer.parseInt((String) parameters.get("city"))));
        String county = parameters.get("county") == null || !parameters.containsKey("county") ? null : qRequestedMySpotZonesRepository.findCountyNameById(BigInteger.valueOf(Integer.parseInt((String) parameters.get("county"))));
        List<String> villages = parameters.get("villages") == null || !parameters.containsKey("villages") ? null : qRequestedMySpotZonesRepository.findVillageNameById(StringUtils.parseBigIntegerList((String) parameters.get("villages")));

        List<FilterInfo> cityList = qRequestedMySpotZonesRepository.findAllCity();
        List<FilterInfo> countyList = qRequestedMySpotZonesRepository.findAllCountyByCity(city);
        List<FilterInfo> villageList = qRequestedMySpotZonesRepository.findAllVillageByCounty(city, county);
        List<FilterInfo> zipcodeList = qRequestedMySpotZonesRepository.findAllZipcodeByCityAndCountyAndVillage(city, county, villages);

        return requestedMySpotZonesMapper.toFilterDto(cityList, countyList, villageList, zipcodeList);
    }

    @Override
    @Transactional(readOnly = true)
    public ListItemResponseDto<ListResponseDto> getAllMySpotRequestList(Map<String, Object> parameters, Integer limit, Integer page, OffsetBasedPageRequest pageable) {
        String city = parameters.get("city") == null || !parameters.containsKey("city") ? null : qRequestedMySpotZonesRepository.findCityNameById(BigInteger.valueOf(Integer.parseInt((String) parameters.get("city"))));
        String county = parameters.get("county") == null || !parameters.containsKey("county") ? null : qRequestedMySpotZonesRepository.findCountyNameById(BigInteger.valueOf(Integer.parseInt((String) parameters.get("county"))));
        List<String> villages = parameters.get("villages") == null || !parameters.containsKey("villages") ? null : qRequestedMySpotZonesRepository.findVillageNameById(StringUtils.parseBigIntegerList((String) parameters.get("villages")));
        List<String> zipcodes = parameters.get("zipcode") == null || !parameters.containsKey("zipcode") ? null : qRequestedMySpotZonesRepository.findZipcodeById(StringUtils.parseBigIntegerList((String) parameters.get("zipcode")));
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
        RequestedMySpotZones requestedMySpotZones = requestedMySpotZonesMapper.toRequestedMySpotZones(createRequestDto);
        requestedMySpotZonesRepository.save(requestedMySpotZones);
    }

    @Override
    @Transactional
    public void updateMySpotRequest(RequestedMySpotDetailDto requestedMySpotDetailDto) {
        RequestedMySpotZones existRequestedMySpotZones = requestedMySpotZonesRepository.findById(requestedMySpotDetailDto.getId())
                .orElseThrow(() -> new ApiException(ExceptionEnum.SPOT_NOT_FOUND));

        existRequestedMySpotZones.updateRequestedMySpotZones(requestedMySpotDetailDto);
    }

    @Override
    @Transactional
    public void deleteMySpotRequest(List<BigInteger> ids) {

        List<RequestedMySpotZones> existRequestedMySpotZones = qRequestedMySpotZonesRepository.findRequestedMySpotZonesByIds(ids);

        // requestedMySpotZone을 가지고 있는 muSpot을 수정
        List<MySpot> mySpotList = qMySpotRepository.findMySpotByRequestedMySpotZones(existRequestedMySpotZones);

        if(mySpotList.isEmpty()) requestedMySpotZonesRepository.deleteAll(existRequestedMySpotZones);

        mySpotList.forEach(mySpot -> mySpot.updateRequestedMySpotZones(null));
        requestedMySpotZonesRepository.deleteAll(existRequestedMySpotZones);

    }

    @Override
    @Transactional
    public void createMySpotZonesFromRequest(List<BigInteger> ids) {
        List<RequestedMySpotZones> existRequestedMySpotZones = qRequestedMySpotZonesRepository.findRequestedMySpotZonesByIds(ids);

        // 마이스팟 생성
        MySpotZone mySpotZone = requestedMySpotZonesMapper.toMySpotZone(existRequestedMySpotZones);

        // mealInfo 생성
        List<MealInfo> mealInfoList = new ArrayList<>();

        String defaultTime = "00:00";
        String defaultDays = "월, 화, 수, 목, 금, 토, 일";

        for (DiningType diningType : mySpotZone.getDiningTypes()) {
            String mealTime = switch (diningType) {
                case MORNING -> "07:00";
                case LUNCH -> "12:00";
                case DINNER -> "19:00";
            };

            mealInfoList.add(requestedMySpotZonesMapper.toMealInfo(mySpotZone, diningType, DateUtils.stringToLocalTime(mealTime), defaultTime, defaultDays, defaultTime));
        }

        mySpotZoneRepository.save(mySpotZone);
        mealInfoRepository.saveAll(mealInfoList);

        // requestedMySpotZone을 가지고 있는 muSpot을 수정
        List<MySpot> mySpotList = qMySpotRepository.findMySpotByRequestedMySpotZones(existRequestedMySpotZones);

        if(!mySpotList.isEmpty()) {
            mySpotList.forEach(mySpot -> {
                mySpot.updateRequestedMySpotZones(null);
                mySpot.updateMySpotZone(mySpotZone);
            });
        }

        // 신청된 마이스팟 존 삭제
        requestedMySpotZonesRepository.deleteAll(existRequestedMySpotZones);
    }


}
