package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.filter.FilterDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.CreateRequestDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.ListResponseDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.RequestedMySpotDetailDto;
import co.dalicious.domain.application_form.dto.share.ShareSpotDto;
import org.locationtech.jts.io.ParseException;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface ApplicationFormService {
    FilterDto getAllListForFilter(Map<String, Object> parameters);

    ListItemResponseDto<ListResponseDto> getAllMySpotRequestList(Map<String, Object> parameters, Integer limit, Integer page, OffsetBasedPageRequest pageable);

    void createMySpotRequest(CreateRequestDto createRequestDto);

    void updateMySpotRequest(RequestedMySpotDetailDto requestedMySpotDetailDto);

    void deleteMySpotRequest(List<BigInteger> ids);

    void createMySpotZonesFromRequest(List<BigInteger> ids);

    ListItemResponseDto<ShareSpotDto.Response> getAllShareSpotRequestList(Integer type, Integer limit, Integer page, OffsetBasedPageRequest pageable);
    void createShareSpotRequest(ShareSpotDto.AdminRequest request) throws ParseException;
    void updateShareSpotRequest(BigInteger applicationId, ShareSpotDto.AdminRequest request) throws ParseException;

    void deleteShareSpotRequest(List<BigInteger> ids);
    Boolean findRenewalMySpotRequest();
    void renewalMySpotRequest();

}
