package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;
import co.kurrant.app.admin_api.dto.GroupDto;
import org.locationtech.jts.io.ParseException;

import java.math.BigInteger;
import java.util.List;

public interface GroupService {
    List<GroupDto.Spot> getSpots(BigInteger groupId);
    ItemPageableResponseDto<GroupListDto> getGroupList(BigInteger groupId, Integer limit, Integer page, OffsetBasedPageRequest pageable);
    void saveCorporationList(List<GroupExcelRequestDto> groupListDto) throws ParseException;
    List<GroupListDto.GroupInfoList> getAllGroupForExcel();
}
