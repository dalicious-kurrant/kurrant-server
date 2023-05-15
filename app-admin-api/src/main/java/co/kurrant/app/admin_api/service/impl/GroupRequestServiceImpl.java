package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.dto.mySpotZone.filter.FilterDto;
import co.dalicious.domain.client.entity.RequestedMySpotZones;
import co.dalicious.domain.client.mapper.RequestedMySpotZonesMapper;
import co.dalicious.domain.client.repository.QRequestedMySpotZonesRepository;
import co.dalicious.domain.client.repository.RequestedMySpotZonesRepository;
import co.kurrant.app.admin_api.service.GroupRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupRequestServiceImpl implements GroupRequestService {
    private final RequestedMySpotZonesRepository requestedMySpotZonesRepository;
    private final QRequestedMySpotZonesRepository qRequestedMySpotZonesRepository;
    private final RequestedMySpotZonesMapper requestedMySpotZonesMapper;
    @Override
    @Transactional
    public FilterDto getAllListForFilter(Map<String, Object> parameters) {
        // 시/도, 군/구, 동/읍/리 별로 필터. - 군/구, 동/읍/리는 다중 필터 가능
        String city = parameters.get("city") == null || !parameters.containsKey("city") ? null : String.valueOf(parameters.get("city"));
        String county = parameters.get("county") == null || !parameters.containsKey("county") ? null : String.valueOf(parameters.get("county"));

        List<RequestedMySpotZones> requestedMySpotZonesList = requestedMySpotZonesRepository.findAll();

        Set<String> citySet = requestedMySpotZonesList.stream().map(RequestedMySpotZones::getCity).collect(Collectors.toSet());
        if (city != null) {
            requestedMySpotZonesList = requestedMySpotZonesList.stream().filter(r -> r.getCity().equals(city)).toList();
        }
        Set<String> countySet = requestedMySpotZonesList.stream().map(RequestedMySpotZones::getCounty).collect(Collectors.toSet());
        if (county != null) {
            requestedMySpotZonesList = requestedMySpotZonesList.stream().filter(r -> r.getCounty().equals(county)).toList();
        }
        Set<String> villageSet = requestedMySpotZonesList.stream().map(RequestedMySpotZones::getVillage).collect(Collectors.toSet());
        Set<String> zipcodeSet = requestedMySpotZonesList.stream().map(RequestedMySpotZones::getZipcode).collect(Collectors.toSet());

        FilterDto filterDto = requestedMySpotZonesMapper.toFilterDto(citySet, countySet, villageSet, zipcodeSet);

        return null;
    }
}
