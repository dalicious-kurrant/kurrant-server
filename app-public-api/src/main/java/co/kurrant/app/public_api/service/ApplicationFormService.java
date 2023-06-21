package co.kurrant.app.public_api.service;

import co.dalicious.domain.application_form.dto.ApplicationFormDto;
import co.dalicious.domain.application_form.dto.PushAlarmSettingDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationApplicationFormRequestDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationApplicationFormResponseDto;
import co.dalicious.domain.application_form.dto.mySpotZone.MySpotZoneApplicationFormRequestDto;
import co.dalicious.domain.application_form.dto.share.ShareSpotDto;
import co.kurrant.app.public_api.dto.client.*;
import co.kurrant.app.public_api.model.SecurityUser;
import org.locationtech.jts.io.ParseException;

import java.math.BigInteger;
import java.util.List;

public interface ApplicationFormService {
    // 마이 스팟 신청
    ApplicationFormDto registerMySpot(SecurityUser securityUser, MySpotZoneApplicationFormRequestDto mySpotZoneApplicationFormRequestDto) throws ParseException;
    // 공유 스팟 신청
    void registerShareSpot(SecurityUser securityUser, Integer typeId, ShareSpotDto.Request request) throws ParseException;
    // 신청한 마이스팟 기록 삭제
    void deleteRequestedMySpot(SecurityUser securityUser);
    // 신청한 스팟 알림 신청
    void updateRequestedMySpotAlarmUser(SecurityUser securityUser, PushAlarmSettingDto dto);
    // 기업 스팟 개설 신청
    ApplicationFormDto registerCorporationSpot(SecurityUser securityuser, CorporationApplicationFormRequestDto corporationApplicationFormRequestDto);
    // 기업 스팟 개설 신청 기타 내용 변경
    void updateCorporationApplicationFormMemo(SecurityUser securityuser, BigInteger id, ApplicationFormMemoDto applicationFormMemoDto);
    // 기업 스팟 개설 신청 내역 상세 조회
    CorporationApplicationFormResponseDto getCorporationApplicationFormDetail(BigInteger userId, BigInteger id);
    // 스팟 신청 날짜 리스트
    List<ApplicationFormDto> getSpotsApplicationList(BigInteger userId);

}
