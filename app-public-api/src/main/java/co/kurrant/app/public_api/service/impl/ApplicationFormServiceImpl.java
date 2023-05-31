package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.application_form.dto.ApplicationFormDto;
import co.dalicious.domain.application_form.dto.apartment.ApartmentApplicationFormRequestDto;
import co.dalicious.domain.application_form.dto.apartment.ApartmentApplicationFormResponseDto;
import co.dalicious.domain.application_form.dto.apartment.ApartmentMealInfoRequestDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationApplicationFormRequestDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationApplicationFormResponseDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationMealInfoRequestDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationSpotRequestDto;
import co.dalicious.domain.application_form.dto.requestMySpotZone.publicApp.MySpotZoneApplicationFormRequestDto;
import co.dalicious.domain.application_form.entity.*;
import co.dalicious.domain.application_form.mapper.*;
import co.dalicious.domain.application_form.repository.*;
import co.dalicious.domain.application_form.validator.ApplicationFormValidator;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.client.entity.Region;
import co.dalicious.domain.client.repository.QMySpotZoneRepository;
import co.dalicious.domain.client.repository.QRegionRepository;
import co.dalicious.domain.user.entity.MySpot;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.mapper.MySpotMapper;
import co.kurrant.app.public_api.dto.client.ApplicationFormMemoDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.ApplicationFormService;
import co.kurrant.app.public_api.service.UserUtil;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationFormServiceImpl implements ApplicationFormService {
    private final UserUtil userUtil;
    private final ApplicationFormValidator applicationFormValidator;
    private final ApartmentApplicationFormMealRepository apartmentApplicationFormMealRepository;
    private final ApartmentApplicationFormRepository apartmentApplicationFormRepository;
    private final CorporationApplicationFormRepository corporationApplicationFormRepository;
    private final CorporationApplicationFormSpotRepository corporationApplicationFormSpotRepository;
    private final CorporationApplicationMealRepository corporationApplicationMealRepository;
    private final CorporationMealInfoReqMapper corporationMealInfoReqMapper;
    private final ApartmentApplicationFormResMapper apartmentApplicationFormResMapper;
    private final ApartmentApplicationReqMapper apartmentApplicationReqMapper;
    private final ApartmentApplicationMealInfoReqMapper apartmentApplicationMealInfoReqMapper;
    private final CorporationApplicationReqMapper corporationApplicationReqMapper;
    private final CorporationApplicationSpotReqMapper corporationApplicationSpotReqMapper;
    private final CorporationApplicationFormResMapper corporationApplicationFormResMapper;
    private final QMySpotZoneRepository qMySpotZoneRepository;
    private final QRequestedMySpotZonesRepository qRequestedMySpotZonesRepository;
    private final MySpotMapper mySpotMapper;
    private final ApplicationMapper applicationMapper;
    private final RequestedMySpotZonesMapper requestedMySpotZonesMapper;
    private final QRegionRepository qRegionRepository;


    @Override
    @Transactional
    public ApplicationFormDto registerApartmentSpot(SecurityUser securityuser, ApartmentApplicationFormRequestDto apartmentApplicationFormRequestDto) {
        // 유저 아이디 가져오기
        BigInteger userId = userUtil.getUserId(securityuser);

        // 스팟 신청 정보 저장
        ApartmentApplicationForm apartmentApplicationForm = apartmentApplicationReqMapper.toEntity(apartmentApplicationFormRequestDto);
        apartmentApplicationForm.setUserId(userId);
        apartmentApplicationFormRepository.save(apartmentApplicationForm);

        // 식사 정보 리스트 가져오기
        List<ApartmentMealInfoRequestDto> apartmentMealInfoRequestDtoList = apartmentApplicationFormRequestDto.getMealDetails();
        for (ApartmentMealInfoRequestDto apartmentMealInfoRequestDto : apartmentMealInfoRequestDtoList) {
            ApartmentApplicationMealInfo apartmentApplicationMealInfo = apartmentApplicationMealInfoReqMapper.toEntity(apartmentMealInfoRequestDto);
            apartmentApplicationMealInfo.setApartmentApplicationForm(apartmentApplicationForm);
            apartmentApplicationFormMealRepository.save(apartmentApplicationMealInfo);
        }

        return ApplicationFormDto.builder()
                .clientType(0)
                .id(apartmentApplicationForm.getId())
                .build();
    }

    @Override
    @Transactional
    public void updateApartmentApplicationFormMemo(SecurityUser securityuser, BigInteger id, ApplicationFormMemoDto applicationFormMemoDto) {
        ApartmentApplicationForm apartmentApplicationForm = applicationFormValidator.isValidApartmentApplicationForm(securityuser.getId(), id);
        apartmentApplicationForm.updateMemo(applicationFormMemoDto.getMemo());
    }

    @Override
    @Transactional
    public ApplicationFormDto registerCorporationSpot(SecurityUser securityuser, CorporationApplicationFormRequestDto corporationApplicationFormRequestDto) {
        // 유저 아이디 가져오기
        BigInteger userId = userUtil.getUserId(securityuser);
        // 식사 정보 리스트 가져오기
        List<CorporationMealInfoRequestDto> mealInfoRequestDtoList = corporationApplicationFormRequestDto.getMealDetails();
        // 스팟 신청 기업의 등록 요청 스팟들 가져오기
        List<CorporationSpotRequestDto> spots = corporationApplicationFormRequestDto.getSpots();

        // 기업 스팟 신청서 저장
        CorporationApplicationForm corporationApplicationForm = corporationApplicationReqMapper.toEntity(corporationApplicationFormRequestDto);
        corporationApplicationForm.setUserId(userId);
        corporationApplicationFormRepository.save(corporationApplicationForm);

        // 스팟 신청 정보 저장
        for (CorporationSpotRequestDto spot : spots) {
            CorporationApplicationFormSpot corporationSpotApplicationForm = null;
            try {
                corporationSpotApplicationForm = corporationApplicationSpotReqMapper.toEntity(spot);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            corporationSpotApplicationForm.setCorporationApplicationForm(corporationApplicationForm);
            corporationApplicationFormSpotRepository.save(corporationSpotApplicationForm);
        }
        // 식사 정보 저장
        for (CorporationMealInfoRequestDto mealInfoRequestDto : mealInfoRequestDtoList) {
            CorporationApplicationMealInfo corporationApplicationMealInfo = corporationMealInfoReqMapper.toEntity(mealInfoRequestDto);
            corporationApplicationMealInfo.setApplicationFormCorporation(corporationApplicationForm);
            corporationApplicationMealRepository.save(corporationApplicationMealInfo);
        }

        return ApplicationFormDto.builder()
                .clientType(1)
                .id(corporationApplicationForm.getId())
                .name(corporationApplicationForm.getCorporationName())
                .build();
    }

    @Override
    @Transactional
    public void updateCorporationApplicationFormMemo(SecurityUser securityUser, BigInteger id, ApplicationFormMemoDto applicationFormMemoDto) {
        // 유저 아이디 가져오기
        BigInteger userId = userUtil.getUserId(securityUser);
        // 로그인 한 유저와 수정하려는 신청서의 작성자가 같은 사람인지 검사
        CorporationApplicationForm corporationApplicationForm = applicationFormValidator.isValidCorporationApplicationForm(userId, id);
        // 내용 업데이트
        corporationApplicationForm.updateMemo(applicationFormMemoDto.getMemo());
    }

    @Override
    @Transactional
    public ApartmentApplicationFormResponseDto getApartmentApplicationFormDetail(BigInteger userId, BigInteger id) {
        // 가져오는 신청서의 작성자가 로그인한 유저와 일치하는지 확인
        ApartmentApplicationForm apartmentApplicationForm = applicationFormValidator.isValidApartmentApplicationForm(userId, id);

        return apartmentApplicationFormResMapper.toDto(apartmentApplicationForm);

    }

    @Override
    @Transactional
    public CorporationApplicationFormResponseDto getCorporationApplicationFormDetail(BigInteger userId, BigInteger id) {
        // 가져오는 신청서의 작성자가 로그인한 유저와 일치하는지 확인
        CorporationApplicationForm corporationApplicationForm = applicationFormValidator.isValidCorporationApplicationForm(userId, id);

        return corporationApplicationFormResMapper.toDto(corporationApplicationForm);
    }

    @Override
    @Transactional
    public List<ApplicationFormDto> getSpotsApplicationList(BigInteger userId) {
        // 유저가 등록한 기업/아파트 신청서 정보 리스트 가져오기
        List<CorporationApplicationForm> corporationApplicationForms = corporationApplicationFormRepository.findAllByUserId(userId);
        List<ApartmentApplicationForm> apartmentApplicationForms = apartmentApplicationFormRepository.findAllByUserId(userId);
        List<ApplicationFormDto> applicationFormDtos = new ArrayList<>();
        // 응답값 생성
        for (CorporationApplicationForm corporationApplicationForm : corporationApplicationForms) {
            applicationFormDtos.add(ApplicationFormDto.builder()
                    .id(corporationApplicationForm.getId())
                    .clientType(1)
                    .name(corporationApplicationForm.getCorporationName())
                    .build());
        }
        for (ApartmentApplicationForm apartmentApplicationForm : apartmentApplicationForms) {
            applicationFormDtos.add(ApplicationFormDto.builder()
                    .id(apartmentApplicationForm.getId())
                    .clientType(0)
                    .name(apartmentApplicationForm.getApartmentName())
                    .build());
        }

        Collections.reverse(applicationFormDtos);

        return applicationFormDtos;
    }

    @Override
    @Transactional
    public ApplicationFormDto registerMySpot(SecurityUser securityUser, MySpotZoneApplicationFormRequestDto requestDto) throws ParseException {
        // user 찾기
//        User user = userUtil.getUser(securityUser);
//
//        // my spot 생성
//        MySpot mySpot = mySpotMapper.toMySpot(user, requestDto);
//
//        // my spot zone 찾기
//        MySpotZone mySpotZone = qMySpotZoneRepository.findExistMySpotZoneByZipcode(requestDto.getAddress().getZipCode());
//
//        if(mySpotZone != null) {
//            mySpotZone.updateMySpotZoneUserCount(1);
//            mySpot.updateMySpotZone(mySpotZone);
//            return applicationMapper.
//        }
//
//        // my spot zone 없으면 my spot zone 신청하기
//        RequestedMySpotZones existRequestedMySpotZones = qRequestedMySpotZonesRepository.findRequestedMySpotZoneByZipcode(requestDto.getAddress().getZipCode());
//        if(existRequestedMySpotZones != null) {
//            existRequestedMySpotZones.updateWaitingUserCount(1);
//            mySpot.updateRequestedMySpotZones(existRequestedMySpotZones);
//            return applicationMapper.
//        }
//
//        String[] jibunAddress = requestDto.getJibunAddress().split(" ");
//        String county = null;
//        String village = null;
//
//        for(String addr : jibunAddress) {
//            if(addr.endsWith("구")) county = addr;
//            else if(addr.endsWith(""))
//        }
//
//        Region region = qRegionRepository.findRegionByZipcodeAndCountyAndVillage(requestDto.getAddress().getZipCode(),);
//        RequestedMySpotZones requestedMySpotZones = requestedMySpotZonesMapper.toRequestedMySpotZones(1, requestDto.getMemo(), )
//
//
//
//        // my spot zone 존재 여부 response
//        return applicationMapper.;
        return null;
    }
}
