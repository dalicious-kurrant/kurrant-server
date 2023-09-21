package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.application_form.dto.StatusUpdateDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationRequestReqDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationRequestResDto;
import co.dalicious.domain.application_form.dto.makers.AdminRecommendMakersResDto;
import co.dalicious.domain.application_form.dto.makers.MakersRequestedReqDto;
import co.dalicious.domain.application_form.dto.makers.MakersRequestedResDto;
import co.dalicious.domain.application_form.dto.makers.RecommendMakersDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.CreateRequestDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.ListResponseDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.admin.RequestedMySpotDetailDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.filter.FilterDto;
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
    List<BigInteger> findRenewalMySpotRequest();
    void renewalMySpotRequest(List<BigInteger> ids);

    // 메이커스 신청
    ListItemResponseDto<MakersRequestedResDto> getAllMakersRequestList(OffsetBasedPageRequest pageable);
    void createMakersRequest(MakersRequestedReqDto request);
    void updateMakerRequestStatus(BigInteger id, StatusUpdateDto request);
    void deleteMakersRequest(List<BigInteger> ids);

    // 고객사 신청
    ListItemResponseDto<CorporationRequestResDto> getAllCorporationRequestList(OffsetBasedPageRequest pageable);
    void createCorporationRequest(CorporationRequestReqDto request);
    void updateCorporationRequestStatus(BigInteger id, StatusUpdateDto request);
    void deleteCorporationRequest(List<BigInteger> ids);

    // 메이커스 추천 - 앱
    ListItemResponseDto<AdminRecommendMakersResDto> getAllRecommendMakersList(Map<String, Object> parameters,OffsetBasedPageRequest pageable);
    void updateRecommendMakersStatus(BigInteger id, StatusUpdateDto request);
    List<RecommendMakersDto> getRecommendMakersName();

}
