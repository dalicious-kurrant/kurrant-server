package co.dalicious.domain.application_form.service;

import co.dalicious.domain.application_form.dto.ApplicationFormDto;
import co.dalicious.domain.application_form.dto.apartment.ApartmentApplicationFormResponseDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationApplicationFormResponseDto;
import co.dalicious.domain.user.entity.User;

import java.math.BigInteger;
import java.util.List;

public interface ApplicationFormService {
    // 아파트 스팟 개설 신청 내역 상세 조회
    ApartmentApplicationFormResponseDto getApartmentApplicationFormDetail(BigInteger userId, Long id);
    // 기업 스팟 개설 신청 내역 상세 조회
    CorporationApplicationFormResponseDto getCorporationApplicationFormDetail(BigInteger userId, Long id);
    // 스팟 신청 날짜 리스트
    List<ApplicationFormDto> getSpotsApplicationList(BigInteger userId);
}
