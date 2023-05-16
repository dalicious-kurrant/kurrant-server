package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.client.dto.mySpotZone.filter.FilterDto;
import co.dalicious.domain.client.dto.mySpotZone.requestMySpotZone.admin.CreateRequestDto;
import co.dalicious.domain.client.dto.mySpotZone.requestMySpotZone.admin.ListResponseDto;
import co.dalicious.domain.client.dto.mySpotZone.requestMySpotZone.admin.RequestedMySpotDetailDto;
import co.kurrant.app.admin_api.dto.IdDto;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface GroupRequestService {
    FilterDto getAllListForFilter(Map<String, Object> parameters);
    ListItemResponseDto<ListResponseDto> getAllMySpotRequestList(Map<String, Object> parameters, Integer limit, Integer page, OffsetBasedPageRequest pageable);
    void createMySpotRequest(CreateRequestDto createRequestDto);
    void updateMySpotRequest(RequestedMySpotDetailDto requestedMySpotDetailDto);
    void deleteMySpotRequest(List<IdDto> ids);
    void createMySpotZonesFromRequest(List<IdDto> ids);
}
