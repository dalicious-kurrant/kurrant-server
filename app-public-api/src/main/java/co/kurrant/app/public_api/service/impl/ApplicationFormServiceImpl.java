package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.ApplicationFormDto;
import co.dalicious.domain.application_form.dto.*;
import co.dalicious.domain.application_form.dto.apartment.*;
import co.dalicious.domain.application_form.dto.corporation.*;
import co.dalicious.domain.application_form.entity.*;
import co.dalicious.domain.application_form.entity.enums.ProgressStatus;
import co.dalicious.domain.application_form.repository.*;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.public_api.dto.client.*;
import co.kurrant.app.public_api.service.ApplicationFormService;
import co.kurrant.app.public_api.service.CommonService;
import co.kurrant.app.public_api.mapper.client.CorporationMealInfoReqMapper;
import co.kurrant.app.public_api.mapper.client.CorporationSpotReqMapper;
import co.dalicious.domain.application_form.validator.ApplicationFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
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

    @Override
    @Transactional
    public ApplicationFormDto registerApartmentSpot(HttpServletRequest httpServletRequest, ApartmentApplicationFormRequestDto apartmentApplicationFormRequestDto) {
        // 유저 아이디 가져오기
        BigInteger userId = commonService.getUserId(httpServletRequest);

        // 담당자 정보 가져오기
        ApplyUserDto applyUserDto = apartmentApplicationFormRequestDto.getUser();

        // 스팟 신청 아파트 정보 가져오기
        ApartmentApplyInfoDto apartmentApplyInfoDto = apartmentApplicationFormRequestDto.getApartmentInfo();

        // 스팟 신청 아파트 주소 정보 가져오기
        CreateAddressRequestDto createAddressRequestDto = apartmentApplicationFormRequestDto.getAddress();
        Address address = Address.builder().createAddressRequestDto(createAddressRequestDto).build();

        // 식사 정보 리스트 가져오기
        List<ApartmentMealInfoRequestDto> apartmentMealInfoRequestDtoList = apartmentApplicationFormRequestDto.getMealDetails();

        // 기타 내용 가져오기
        String memo = apartmentApplicationFormRequestDto.getMemo();

        // 스팟 신청 정보 저장
        ApartmentApplicationForm apartmentApplicationForm = apartmentApplicationFormRepository.save(
                ApartmentApplicationForm.builder()
                        .progressStatus(ProgressStatus.APPLY)
                        .userId(userId)
                        .applyUserDto(applyUserDto)
                        .apartmentApplyInfoDto(apartmentApplyInfoDto)
                        .address(address)
                        .memo(memo)
                        .build());

        // 식사 정보 리스트 저장
        List<ApartmentApplicationMealInfo> apartmentApplicationMealInfoList = new ArrayList<>();
        for (ApartmentMealInfoRequestDto apartmentMealInfoRequestDto : apartmentMealInfoRequestDtoList) {
            apartmentMealInfoRequestDto.setApartmentApplicationForm(apartmentApplicationForm);
            ApartmentApplicationMealInfo apartmentApplicationMealInfo = apartmentApplicationFormMealRepository.save(
                    ApartmentApplicationMealInfo.builder()
                            .apartmentMealInfoRequestDto(apartmentMealInfoRequestDto)
                            .apartmentApplicationForm(apartmentApplicationForm)
                            .build());
            apartmentApplicationMealInfoList.add(apartmentApplicationMealInfo);
        }
        apartmentApplicationForm.setMealInfoList(apartmentApplicationMealInfoList);

        return ApplicationFormDto.builder()
                .clientType(0)
                .id(apartmentApplicationForm.getId())
                .date(DateUtils.format(LocalDate.now(), "yyyy. MM. dd"))
                .build();
    }

    @Override
    @Transactional
    public void updateApartmentApplicationFormMemo(HttpServletRequest httpServletRequest, Long id, ApplicationFormMemoDto applicationFormMemoDto) {
        ApartmentApplicationForm apartmentApplicationForm = applicationFormValidator.isValidApartmentApplicationForm(commonService.getUserId(httpServletRequest), id);
        apartmentApplicationForm.updateMemo(applicationFormMemoDto.getMemo());
    }

    @Override
    @Transactional
    public ApplicationFormDto registerCorporationSpot(HttpServletRequest httpServletRequest, CorporationApplicationFormRequestDto corporationApplicationFormRequestDto) {
        // 유저 아이디 가져오기
        BigInteger userId = commonService.getUserId(httpServletRequest);
        // 담당자 정보 가져오기
        ApplyUserDto applyUserDto = corporationApplicationFormRequestDto.getUser();

        // 스팟 신청 기업 정보 가져오기
        CorporationApplyInfoDto applyInfoDto = corporationApplicationFormRequestDto.getCorporationInfo();

        // 스팟 신청 기업 주소 정보 가져오기
        CreateAddressRequestDto addressRequestDto = corporationApplicationFormRequestDto.getAddress();
        Address address = Address.builder()
                .createAddressRequestDto(addressRequestDto)
                .build();

        // 식사 정보 리스트 가져오기
        List<CorporationMealInfoRequestDto> mealInfoRequestDtoList = corporationApplicationFormRequestDto.getMealDetails();

        // 스팟 신청 기업의 등록 요청 스팟들 가져오기
        List<CorporationSpotRequestDto> spots = corporationApplicationFormRequestDto.getSpots();

        // 옵션 내용 가져오기
        CorporationOptionsDto corporationOptionsDto = corporationApplicationFormRequestDto.getOption();

        // 기업 스팟 신청서 저장
        CorporationApplicationForm corporationApplicationForm = corporationApplicationFormRepository.save(
                CorporationApplicationForm.builder()
                        .progressStatus(ProgressStatus.APPLY)
                        .userId(userId)
                        .applyUserDto(applyUserDto)
                        .applyInfoDto(applyInfoDto)
                        .address(address)
                        .corporationOptionsDto(corporationOptionsDto)
                        .build());

        // 스팟 신청 정보 저장
        List<CorporationApplicationFormSpot> spotList = new ArrayList<>();
        for (CorporationSpotRequestDto spot : spots) {
            CorporationApplicationFormSpot corporationSpotApplicationForm = CorporationSpotReqMapper.INSTANCE.toEntity(spot);
            corporationSpotApplicationForm.setCorporationApplicationForm(corporationApplicationForm);
            spotList.add(corporationApplicationFormSpotRepository.save(corporationSpotApplicationForm));
        }
        corporationApplicationForm.setSpots(spotList);
        // 식사 정보 저장
        List<CorporationApplicationMealInfo> mealInfoList = new ArrayList<>();
        for (CorporationMealInfoRequestDto mealInfoRequestDto : mealInfoRequestDtoList) {
            CorporationApplicationMealInfo corporationApplicationMealInfo = CorporationMealInfoReqMapper.INSTANCE.toEntity(mealInfoRequestDto);
            corporationApplicationMealInfo.setApplicationFormCorporation(corporationApplicationForm);
            mealInfoList.add(corporationApplicationMealRepository.save(corporationApplicationMealInfo));
        }
        corporationApplicationForm.setMealInfoList(mealInfoList);

        return ApplicationFormDto.builder()
                .clientType(1)
                .id(corporationApplicationForm.getId())
                .date(DateUtils.format(LocalDate.now(), "yyyy. MM. dd"))
                .build();
    }

    @Override
    @Transactional
    public void updateCorporationApplicationFormMemo(HttpServletRequest httpServletRequest, Long id, ApplicationFormMemoDto applicationFormMemoDto) {
        // 유저 아이디 가져오기
        BigInteger userId = commonService.getUserId(httpServletRequest);
        // 로그인 한 유저와 수정하려는 신청서의 작성자가 같은 사람인지 검사
        CorporationApplicationForm corporationApplicationForm = applicationFormValidator.isValidCorporationApplicationForm(userId, id);
        // 내용 업데이트
        corporationApplicationForm.updateMemo(applicationFormMemoDto.getMemo());
    }
}
