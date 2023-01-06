package co.kurrant.app.public_api.service;

import co.dalicious.domain.application_form.dto.ApplicationFormDto;
import co.dalicious.domain.application_form.dto.apartment.ApartmentApplicationFormRequestDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationApplicationFormRequestDto;
import co.kurrant.app.public_api.dto.client.*;

import javax.servlet.http.HttpServletRequest;

public interface AppApplicationFormService {
    // 아파트 스팟 개설 신청
    ApplicationFormDto registerApartmentSpot(HttpServletRequest httpServletRequest, ApartmentApplicationFormRequestDto apartmentApplicationFormRequestDto);
    // 아파트 스팟 개설 신청 기타 내용 변경
    void updateApartmentApplicationFormMemo(HttpServletRequest httpServletRequest, Long id, ApplicationFormMemoDto applicationFormMemoDto);
    // 기업 스팟 개설 신청
    ApplicationFormDto registerCorporationSpot(HttpServletRequest httpServletRequest, CorporationApplicationFormRequestDto corporationApplicationFormRequestDto);
    // 기업 스팟 개설 신청 기타 내용 변경
    void updateCorporationApplicationFormMemo(HttpServletRequest httpServletRequest, Long id, ApplicationFormMemoDto applicationFormMemoDto);
}
