package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.dalicious.domain.client.dto.UpdateSpotDetailRequestDto;
import co.dalicious.integration.client.user.dto.filter.FilterDto;
import co.dalicious.integration.client.user.dto.mySpotZone.AdminListResponseDto;
import co.dalicious.integration.client.user.dto.mySpotZone.CreateRequestDto;
import co.dalicious.integration.client.user.dto.mySpotZone.UpdateRequestDto;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.dalicious.domain.client.dto.UpdateSpotDetailResponseDto;
import org.locationtech.jts.io.ParseException;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface GroupService {
    List<GroupDto.Spot> getSpots(BigInteger groupId);
    ItemPageableResponseDto<GroupListDto> getGroupList(BigInteger groupId, Integer limit, Integer page, OffsetBasedPageRequest pageable);
    void saveCorporationList(List<GroupListDto.GroupInfoList> corporationListDto) throws ParseException;
    GroupListDto.GroupInfoList  getGroupDetail(BigInteger groupId);

    void updateGroupDetail(UpdateSpotDetailRequestDto updateSpotDetailRequestDto) throws ParseException;
    List<GroupListDto.GroupInfoList> getAllGroupForExcel();
    FilterDto getAllListForFilter(Map<String, Object> parameters);
    ListItemResponseDto<AdminListResponseDto> getAllMySpotZoneList(Map<String, Object> parameters, Integer limit, Integer size, OffsetBasedPageRequest pageable);
    void createMySpotZone(CreateRequestDto createRequestDto);
    void updateMySpotZone(UpdateRequestDto updateRequestDto);
    void deleteMySpotZone(List<BigInteger> id);
    void updateLocation() throws ParseException;
    void saveCorporationOrOpenGroup(GroupListDto.GroupInfoList requestDto) throws ParseException;
}
