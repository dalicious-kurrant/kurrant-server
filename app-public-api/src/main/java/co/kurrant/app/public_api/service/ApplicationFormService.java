package co.kurrant.app.public_api.service;

import co.dalicious.domain.application_form.dto.apartment.ApartmentApplicationFormRequestDto;
import co.dalicious.domain.application_form.dto.apartment.ApartmentApplicationFormResponseDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationApplicationFormRequestDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationApplicationFormResponseDto;
import co.kurrant.app.public_api.dto.client.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ApplicationFormService {
    // 아파트 스팟 개설 신청
    ApplicationFormDto registerApartmentSpot(HttpServletRequest httpServletRequest, ApartmentApplicationFormRequestDto apartmentApplicationFormRequestDto);
    // 아파트 스팟 개설 신청 내역 상세 조회
    ApartmentApplicationFormResponseDto getApartmentApplicationFormDetail(HttpServletRequest httpServletRequest, Long id);
    // 아파트 스팟 개설 신청 기타 내용 변경
    void updateApartmentApplicationFormMemo(HttpServletRequest httpServletRequest, Long id, ApplicationFormMemoDto applicationFormMemoDto);
    // 기업 스팟 개설 신청
    ApplicationFormDto registerCorporationSpot(HttpServletRequest httpServletRequest, CorporationApplicationFormRequestDto corporationApplicationFormRequestDto);
    // 기업 스팟 개설 신청 내역 상세 조회
    CorporationApplicationFormResponseDto getCorporationApplicationFormDetail(HttpServletRequest httpServletRequest, Long id);
    // 기업 스팟 개설 신청 기타 내용 변경
    void updateCorporationApplicationFormMemo(HttpServletRequest httpServletRequest, Long id, ApplicationFormMemoDto applicationFormMemoDto);
    // 스팟 신청 날짜 리스트
    List<ApplicationFormDto> getSpotsApplicationList(HttpServletRequest httpServletRequest);

}
