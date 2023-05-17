package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.client.dto.mySpotZone.filter.FilterDto;
import co.dalicious.domain.client.dto.mySpotZone.requestMySpotZone.admin.CreateRequestDto;
import co.dalicious.domain.client.dto.mySpotZone.requestMySpotZone.admin.ListResponseDto;
import co.dalicious.domain.client.dto.mySpotZone.requestMySpotZone.admin.RequestedMySpotDetailDto;
import co.dalicious.domain.client.entity.MealInfo;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.client.entity.MySpotZoneMealInfo;
import co.dalicious.domain.client.entity.RequestedMySpotZones;
import co.dalicious.domain.client.mapper.RequestedMySpotZonesMapper;
import co.dalicious.domain.client.repository.MySpotZoneRepository;
import co.dalicious.domain.client.repository.QRequestedMySpotZonesRepository;
import co.dalicious.domain.client.repository.RequestedMySpotZonesRepository;
import co.dalicious.domain.user.entity.MySpot;
import co.dalicious.domain.user.repository.QMySpotRepository;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
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
    @Override
    @Transactional(readOnly = true)
    public FilterDto getAllListForFilter(Map<String, Object> parameters) {
        // 시/도, 군/구, 동/읍/리 별로 필터. - 군/구, 동/읍/리는 다중 필터 가능
        String city = parameters.get("city") == null || !parameters.containsKey("city") ? null : String.valueOf(parameters.get("city"));
        String county = parameters.get("county") == null || !parameters.containsKey("county") ? null : String.valueOf(parameters.get("county"));
        List<String> villages = parameters.get("villages") == null || !parameters.containsKey("villages") ? null : List.of(String.valueOf(parameters.get("villages")).split(",|, "));

        List<String> cityList = qRequestedMySpotZonesRepository.findAllCity();
        List<String> countyList = qRequestedMySpotZonesRepository.findAllCountyByCity(city);
        List<String> villageList = qRequestedMySpotZonesRepository.findAllVillageByCounty(city, county);
        List<String> zipcodeList = qRequestedMySpotZonesRepository.findAllZipcodeByCityAndCountyAndVillage(city, county, villages);

        return requestedMySpotZonesMapper.toFilterDto(cityList, countyList, villageList, zipcodeList);
    }

    @Override
    @Transactional(readOnly = true)
    public ListItemResponseDto<ListResponseDto> getAllMySpotRequestList(Map<String, Object> parameters, Integer limit, Integer page, OffsetBasedPageRequest pageable) {
        String city = parameters.get("city") == null || !parameters.containsKey("city") ? null : String.valueOf(parameters.get("city"));
        String county = parameters.get("county") == null || !parameters.containsKey("county") ? null : String.valueOf(parameters.get("county"));
        List<String> villages = parameters.get("villages") == null || !parameters.containsKey("villages") ? null : List.of(String.valueOf(parameters.get("villages")).split(",|, "));
        List<String> zipcodes = parameters.get("zipcode") == null || !parameters.containsKey("zipcode") ? null : List.of(String.valueOf(parameters.get("zipcode")).split(", |,"));
        Integer minUserCount = parameters.get("min") == null || !parameters.containsKey("min") ? null : Integer.valueOf(String.valueOf(parameters.get("min")));
        Integer maxUserCount = parameters.get("max") == null || !parameters.containsKey("max") ? null : Integer.valueOf(String.valueOf(parameters.get("max")));

        Page<RequestedMySpotZones> requestedMySpotZonesList = qRequestedMySpotZonesRepository.findAllRequestedMySpotZonesByFilter(city, county, villages, zipcodes, minUserCount, maxUserCount, limit, page, pageable);
        List<ListResponseDto> listResponseDtos = new ArrayList<>();
        if(requestedMySpotZonesList == null || requestedMySpotZonesList.isEmpty()) {
            return ListItemResponseDto.<ListResponseDto>builder().items(listResponseDtos).limit(pageable.getPageSize()).total(0L).count(0).offset(pageable.getOffset()).build();
        }

        listResponseDtos = requestedMySpotZonesList.stream().map(requestedMySpotZonesMapper::toListResponseDto).toList();
        listResponseDtos = listResponseDtos.stream()
                .sorted(Comparator.comparing(ListResponseDto::getRequestUserCount).reversed()
                        .thenComparing(ListResponseDto::getZipcode).reversed())
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
        if(existRequestedMySpotZones != null) throw new ApiException(ExceptionEnum.ALREADY_EXIST_REQUEST);

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

        List<DiningType> diningTypeList = mySpotZone.getDiningTypes();

        for(DiningType diningType : diningTypeList) {
            switch (diningType) {
                case MORNING -> {
                    mealInfoList.add(requestedMySpotZonesMapper.toMealInfo(mySpotZone, diningType, DateUtils.stringToLocalTime("08:00"), "00:00", "월, 화, 수, 목, 금, 토, 일", "00:00"));
                }
                case LUNCH -> {
                    mealInfoList.add(requestedMySpotZonesMapper.toMealInfo(mySpotZone, diningType, DateUtils.stringToLocalTime("12:00"), "00:00", "월, 화, 수, 목, 금, 토, 일", "00:00"));
                }
                case DINNER -> {
                    mealInfoList.add(requestedMySpotZonesMapper.toMealInfo(mySpotZone, diningType, DateUtils.stringToLocalTime("19:00"), "00:00", "월, 화, 수, 목, 금, 토, 일", "00:00"));
                }
            }
        }

        mySpotZoneRepository.save(mySpotZone);
//        mySpotZoneM

        // requestedMySpotZone을 가지고 있는 muSpot을 수정
        List<MySpot> mySpotList = qMySpotRepository.findMySpotByRequestedMySpotZones(existRequestedMySpotZones);

        if(mySpotList.isEmpty()) requestedMySpotZonesRepository.deleteAll(existRequestedMySpotZones);

        mySpotList.forEach(mySpot -> {
            mySpot.updateRequestedMySpotZones(null);
            mySpot.updateMySpotZone(mySpotZone);
        });

        // 신청된 마이스팟 존 삭제
        requestedMySpotZonesRepository.deleteAll(existRequestedMySpotZones);
    }


}
