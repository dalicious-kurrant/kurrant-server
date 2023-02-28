package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.domain.client.dto.GroupListDto;
import co.dalicious.domain.client.dto.GroupExcelRequestDto;

import java.math.BigInteger;
import java.util.List;

public interface GroupService {
    ItemPageableResponseDto<GroupListDto> getGroupList(BigInteger groupId, Integer limit, Integer page, OffsetBasedPageRequest pageable);
    void saveCorporationList(List<GroupExcelRequestDto> groupListDto);
}
