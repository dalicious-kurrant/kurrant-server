package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.application_form.dto.ApplicationFormDto;
import co.dalicious.domain.application_form.dto.apartment.*;
import co.dalicious.domain.application_form.dto.corporation.*;
import co.dalicious.domain.application_form.entity.*;
import co.dalicious.domain.application_form.mapper.*;
import co.dalicious.domain.application_form.repository.*;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.public_api.dto.client.*;
import co.kurrant.app.public_api.service.ApplicationFormService;
import co.kurrant.app.public_api.service.CommonService;
import co.dalicious.domain.application_form.mapper.CorporationMealInfoReqMapper;
import co.dalicious.domain.application_form.validator.ApplicationFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationFormServiceImpl implements ApplicationFormService {
    private final CommonService commonService;
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

    @Override
    @Transactional
    public ApplicationFormDto registerApartmentSpot(HttpServletRequest httpServletRequest, ApartmentApplicationFormRequestDto apartmentApplicationFormRequestDto) {
        // 유저 아이디 가져오기
        BigInteger userId = commonService.getUserId(httpServletRequest);

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
                .date(DateUtils.format(LocalDate.now(), "yyyy. MM. dd"))
                .build();
    }

    @Override
    @Transactional
    public void updateApartmentApplicationFormMemo(HttpServletRequest httpServletRequest, BigInteger id, ApplicationFormMemoDto applicationFormMemoDto) {
        ApartmentApplicationForm apartmentApplicationForm = applicationFormValidator.isValidApartmentApplicationForm(commonService.getUserId(httpServletRequest), id);
        apartmentApplicationForm.updateMemo(applicationFormMemoDto.getMemo());
    }

    @Override
    @Transactional
    public ApplicationFormDto registerCorporationSpot(HttpServletRequest httpServletRequest, CorporationApplicationFormRequestDto corporationApplicationFormRequestDto) {
        // 유저 아이디 가져오기
        BigInteger userId = commonService.getUserId(httpServletRequest);
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
            CorporationApplicationFormSpot corporationSpotApplicationForm = corporationApplicationSpotReqMapper.toEntity(spot);
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
                .date(DateUtils.format(LocalDate.now(), "yyyy. MM. dd"))
                .build();
    }

    @Override
    @Transactional
    public void updateCorporationApplicationFormMemo(HttpServletRequest httpServletRequest, BigInteger id, ApplicationFormMemoDto applicationFormMemoDto) {
        // 유저 아이디 가져오기
        BigInteger userId = commonService.getUserId(httpServletRequest);
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
        // 기업 스팟 신청서 가져오기
        CorporationApplicationForm corporationApplicationForm = applicationFormValidator.isValidCorporationApplicationForm(userId, id);
        return corporationApplicationFormResMapper.toDto(corporationApplicationForm);
//        // 담당자 정보 가져오기
//        ApplyUserDto applyUserDto = ApplyUserDto.builder()
//                .name(corporationApplicationForm.getApplierName())
//                .email(corporationApplicationForm.getEmail())
//                .phone(corporationApplicationForm.getPhone()).build();
//        // 기업 주소 가져오기
//        Address address = corporationApplicationForm.getAddress();
//        String addressString = address.getAddress1() + " " + address.getAddress2();
//
//        // 스팟 정보 가져오기
//        List<CorporationApplicationFormSpot> spots = corporationApplicationFormSpotRepository.findAllByCorporationApplicationForm(corporationApplicationForm);
//        List<CorporationSpotResponseDto> spotList = new ArrayList<>();
//        for (CorporationApplicationFormSpot spot : spots) {
//            spotList.add(CorporationSpotResMapper.INSTANCE.toDto(spot));
//        }
//
//        // 식사 정보 가져오기
//        List<CorporationApplicationMealInfo> mealInfos = corporationApplicationMealRepository.findAllByCorporationApplicationForm(corporationApplicationForm);
//        List<String> diningTypes = new ArrayList<>();
//        List<CorporationMealInfoResponseDto> mealInfoList = new ArrayList<>();
//        for (CorporationApplicationMealInfo mealInfo : mealInfos) {
//            mealInfoList.add(corporationMealInfoResMapper.toDto(mealInfo));
//            diningTypes.add(mealInfo.getDiningType().getDiningType());
//        }
//
//        // 기업 정보 가져오기
//        CorporationApplyInfoDto corporationApplyInfoDto = CorporationApplyInfoDto.builder()
//                .corporationName(corporationApplicationForm.getCorporationName())
//                .employeeCount(corporationApplicationForm.getEmployeeCount())
//                .startDate(DateUtils.format(corporationApplicationForm.getServiceStartDate(), "yyyy. MM. dd"))
//                .diningTypes(diningTypes)
//                .build();
//
//        // 옵션 정보 가져오기
//        CorporationOptionsDto corporationOptionsDto = CorporationOptionsDto.builder()
//                .isGarbage(corporationApplicationForm.getIsGarbage())
//                .isHotStorage(corporationApplicationForm.getIsHotStorage())
//                .isSetting(corporationApplicationForm.getIsSetting())
//                .memo(corporationApplicationForm.getMemo())
//                .build();
//
//        return CorporationApplicationFormResponseDto.builder()
//                .date(DateUtils.format(corporationApplicationForm.getCreatedDateTime(), "yyyy. MM. dd"))
//                .progressStatus(corporationApplicationForm.getProgressStatus().getCode())
//                .user(applyUserDto)
//                .address(addressString)
//                .corporationInfo(corporationApplyInfoDto)
//                .spots(spotList)
//                .mealDetails(mealInfoList)
//                .option(corporationOptionsDto)
//                .rejectedReason(corporationApplicationForm.getRejectedReason())
//                .build();
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
                    .date(DateUtils.format(corporationApplicationForm.getCreatedDateTime(), "yyyy. MM. dd"))
                    .build());
        }
        for (ApartmentApplicationForm apartmentApplicationForm : apartmentApplicationForms) {
            applicationFormDtos.add(ApplicationFormDto.builder()
                    .id(apartmentApplicationForm.getId())
                    .clientType(0)
                    .name(apartmentApplicationForm.getApartmentName())
                    .date(DateUtils.format(apartmentApplicationForm.getCreatedDateTime(), "yyyy. MM. dd"))
                    .build());
        }

        // 생성일자 순으로 정렬
        applicationFormDtos.sort(Comparator.comparing(ApplicationFormDto::getDate));

        return applicationFormDtos;
    }
}
