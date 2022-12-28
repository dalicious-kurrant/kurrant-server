package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.dto.CreateAddressResponseDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.*;
import co.dalicious.domain.application_form.dto.apartment.*;
import co.dalicious.domain.application_form.dto.corporation.*;
import co.dalicious.domain.application_form.entity.ApartmentApplicationForm;
import co.dalicious.domain.application_form.entity.ApartmentMealInfo;
import co.dalicious.domain.application_form.repository.ApartmentApplicationFormRepository;
import co.dalicious.domain.application_form.repository.ApplyMealInfoRepository;
import co.dalicious.domain.client.repository.CorporationRepository;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.public_api.dto.client.*;
import co.kurrant.app.public_api.service.ApplicationFormService;
import co.kurrant.app.public_api.service.CommonService;
import co.kurrant.app.public_api.validator.ApplicationFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationFormServiceImpl implements ApplicationFormService {
    private final CommonService commonService;
    private final ApplicationFormValidator applicationFormValidator;
    private final ApplyMealInfoRepository applyMealInfoRepository;
    private final ApartmentApplicationFormRepository apartmentApplicationFormRepository;
    private final CorporationRepository corporationRepository;

    @Override
    @Transactional
    public void registerApartmentSpot(HttpServletRequest httpServletRequest, ApartmentApplicationFormRequestDto apartmentApplicationFormRequestDto) {
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
        ApartmentApplicationForm apartmentApplicationForm = apartmentApplicationFormRepository.save(ApartmentApplicationForm.builder().userId(userId).applyUserDto(applyUserDto).apartmentApplyInfoDto(apartmentApplyInfoDto).address(address).memo(memo).build());

        // 식사 정보 리스트 저장
        List<ApartmentMealInfo> apartmentMealInfoList = new ArrayList<>();
        for (ApartmentMealInfoRequestDto apartmentMealInfoRequestDto : apartmentMealInfoRequestDtoList) {
            apartmentMealInfoRequestDto.setApartmentApplicationForm(apartmentApplicationForm);
            ApartmentMealInfo apartmentMealInfo = applyMealInfoRepository.save(ApartmentMealInfo.builder().apartmentMealInfoRequestDto(apartmentMealInfoRequestDto).apartmentApplicationForm(apartmentApplicationForm).build());
            apartmentMealInfoList.add(apartmentMealInfo);
        }
        apartmentApplicationForm.setMealInfoList(apartmentMealInfoList);
    }

    @Override
    @Transactional
    public ApartmentApplicationFormResponseDto getApartmentApplicationFormDetail(HttpServletRequest httpServletRequest, Long id) {
        ApartmentApplicationForm apartmentApplicationForm = applicationFormValidator.isVaildApartmentApplicationForm(commonService.getUserId(httpServletRequest), id);

        ApplyUserDto applyUserDto = ApplyUserDto.builder().name(apartmentApplicationForm.getApplierName()).email(apartmentApplicationForm.getEmail()).phone(apartmentApplicationForm.getPhone()).build();

        Address address = apartmentApplicationForm.getAddress();
        CreateAddressResponseDto createAddressResponseDto = CreateAddressResponseDto.builder().address1(address.getAddress1()).address2(address.getAddress2()).build();

        ApartmentApplyInfoDto apartmentApplyInfoDto = ApartmentApplyInfoDto.builder().apartmentName(apartmentApplicationForm.getApartmentName()).dongCount(apartmentApplicationForm.getDongCount()).familyCount(apartmentApplicationForm.getTotalFamilyCount()).serviceStartDate(DateUtils.format(apartmentApplicationForm.getServiceStartDate(), "yyyy. MM. dd")).build();

        List<ApartmentMealInfoResponseDto> meal = new ArrayList<>();
        for (ApartmentMealInfo apartmentMealInfo : apartmentApplicationForm.getMealInfoList()) {
            meal.add(ApartmentMealInfoResponseDto.builder().apartmentMealInfo(apartmentMealInfo).build());
        }

        String memo = apartmentApplicationForm.getMemo();

        return ApartmentApplicationFormResponseDto.builder().user(applyUserDto).address(createAddressResponseDto).info(apartmentApplyInfoDto).meal(meal).memo(memo).build();
    }

    @Override
    public void updateApartmentApplicationFormMemo(HttpServletRequest httpServletRequest, Long id, ApplicationFormMemoDto applicationFormMemoDto) {
        ApartmentApplicationForm apartmentApplicationForm = applicationFormValidator.isVaildApartmentApplicationForm(commonService.getUserId(httpServletRequest), id);
        apartmentApplicationForm.updateMemo(apartmentApplicationForm.getMemo());
    }

    @Override
    public void registerCorporationSpot(HttpServletRequest httpServletRequest, CorporationApplicationFormRequestDto corporationApplicationFormRequestDto) {
        // 유저 아이디 가져오기
        BigInteger userId = commonService.getUserId(httpServletRequest);

        // 담당자 정보 가져오기
        ApplyUserDto applyUserDto = corporationApplicationFormRequestDto.getUser();

        // 스팟 신청 기업 정보 가져오기
        CorporationApplyInfoDto applyInfoDto = corporationApplicationFormRequestDto.getCorporationApplyInfoDto();

        // 스팟 신청 기업 주소 정보 가져오기
        CreateAddressRequestDto addressRequestDto = corporationApplicationFormRequestDto.getCreateAddressRequestDto();

        // 식사 정보 리스트 가져오기
        List<CorporationMealInfoRequestDto> mealInfoRequestDtoList = corporationApplicationFormRequestDto.getMealDetails();

        // 스팟 신청 기업의 등록 요청 스팟들 가져오기
        List<CorporationSpotApplicationFormDto> spots = corporationApplicationFormRequestDto.getSpots();

        // 옵션 내용 가져오기
        CorporationOptionsApplicationFormRequestDto corporationOptionsApplicationFormRequestDto = corporationApplicationFormRequestDto.getOption();

        // 스팟 신청 정보 저장

        // 식사 정보 저장
    }

    @Override
    public CorporationApplicationFormRequestDto getCorporationApplicationFormDetail(HttpServletRequest httpServletRequest, Long id) {
        return null;
    }

    @Override
    public void updateCorporationApplicationFormMemo(HttpServletRequest httpServletRequest, Long id, ApplicationFormMemoDto applicationFormMemoDto) {

    }

    @Override
    public ApplicationFormDto getSpotsApplicationList(HttpServletRequest httpServletRequest) {
        return null;
    }
}
