package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.kurrant.app.admin_api.dto.client.GroupListDto;

import java.math.BigInteger;

public interface GroupService {
    ListItemResponseDto<GroupListDto> getGroupList(BigInteger groupId, Integer limit, Integer page, OffsetBasedPageRequest pageable);
    void saveCorporationList(GroupListDto groupListDto);
}
