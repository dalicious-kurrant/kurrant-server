package co.kurrant.app.public_api.service;

import co.dalicious.domain.application_form.dto.ApartmentApplicationFormRequestDto;
import co.dalicious.domain.application_form.dto.ApartmentApplicationFormResponseDto;
import co.kurrant.app.public_api.dto.client.*;

import javax.servlet.http.HttpServletRequest;

public interface ApplicationFormService {
    // 아파트 스팟 개설 신청
    void registerApartmentSpot(HttpServletRequest httpServletRequest, ApartmentApplicationFormRequestDto apartmentApplicationFormRequestDto);
    // 아파트 스팟 개설 신청 내역 상세 조회
    ApartmentApplicationFormResponseDto getApartmentApplicationFormDetail(HttpServletRequest httpServletRequest, Long id);
    // 아파트 스팟 개설 신청 기타 내용 변경
    void updateApartmentApplicationFormMemo(HttpServletRequest httpServletRequest, Long id, ApartmentApplicationFormMemoDto apartmentApplicationFormMemoDto);
    // 기업 스팟 개설 신청
    void registerCorporationSpot(HttpServletRequest httpServletRequest, CorporationSpotApplicationFormDto apartmentSpotApplicationFormDto);
    // 스팟 신청 날짜 리스트
    ApplicationFormDto getSpotsApplicationList(HttpServletRequest httpServletRequest);

}
