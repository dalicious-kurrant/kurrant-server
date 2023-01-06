package co.dalicious.domain.application_form.service;

import co.dalicious.domain.address.dto.CreateAddressResponseDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.ApplicationFormDto;
import co.dalicious.domain.application_form.dto.ApplyUserDto;
import co.dalicious.domain.application_form.dto.apartment.ApartmentApplicationFormResponseDto;
import co.dalicious.domain.application_form.dto.apartment.ApartmentApplyInfoDto;
import co.dalicious.domain.application_form.dto.apartment.ApartmentMealInfoResponseDto;
import co.dalicious.domain.application_form.dto.corporation.*;
import co.dalicious.domain.application_form.entity.*;
import co.dalicious.domain.application_form.mapper.CorporationMealInfoResMapper;
import co.dalicious.domain.application_form.mapper.CorporationSpotResMapper;
import co.dalicious.domain.application_form.repository.ApartmentApplicationFormRepository;
import co.dalicious.domain.application_form.repository.CorporationApplicationFormRepository;
import co.dalicious.domain.application_form.repository.CorporationApplicationFormSpotRepository;
import co.dalicious.domain.application_form.repository.CorporationApplicationMealRepository;
import co.dalicious.domain.application_form.validator.ApplicationFormValidator;
import co.dalicious.system.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationFormServiceImpl implements ApplicationFormService{
    private final ApplicationFormValidator applicationFormValidator;
    private final CorporationApplicationFormSpotRepository corporationApplicationFormSpotRepository;
    private final CorporationApplicationMealRepository corporationApplicationMealRepository;
    private final CorporationMealInfoResMapper corporationMealInfoResMapper;
    private final ApartmentApplicationFormRepository apartmentApplicationFormRepository;
    private final CorporationApplicationFormRepository corporationApplicationFormRepository;
    @Override
    @Transactional
    public ApartmentApplicationFormResponseDto getApartmentApplicationFormDetail(BigInteger userId, Long id) {
        // 가져오는 신청서의 작성자가 로그인한 유저와 일치하는지 확인
        ApartmentApplicationForm apartmentApplicationForm = applicationFormValidator.isValidApartmentApplicationForm(userId, id);
        // 등록한 유저의 정보 가져오기
        ApplyUserDto applyUserDto = ApplyUserDto.builder().name(apartmentApplicationForm.getApplierName()).email(apartmentApplicationForm.getEmail()).phone(apartmentApplicationForm.getPhone()).build();
        // 아파트의 주소 가져오기
        Address address = apartmentApplicationForm.getAddress();
        CreateAddressResponseDto createAddressResponseDto = new CreateAddressResponseDto(address);
        // 식사 정보 가져오기
        List<ApartmentMealInfoResponseDto> meal = new ArrayList<>();
        List<String> diningTypes = new ArrayList<>();
        for (ApartmentApplicationMealInfo apartmentApplicationMealInfo : apartmentApplicationForm.getMealInfoList()) {
            meal.add(ApartmentMealInfoResponseDto.builder().apartmentApplicationMealInfo(apartmentApplicationMealInfo).build());
            diningTypes.add(apartmentApplicationMealInfo.getDiningType().getDiningType());
        }
        // 아파트 정보 가져오기
        ApartmentApplyInfoDto apartmentApplyInfoDto = ApartmentApplyInfoDto.builder()
                .apartmentName(apartmentApplicationForm.getApartmentName())
                .dongCount(apartmentApplicationForm.getDongCount())
                .familyCount(apartmentApplicationForm.getTotalFamilyCount())
                .serviceStartDate(DateUtils.format(apartmentApplicationForm.getServiceStartDate(), "yyyy. MM. dd"))
                .diningTypes(diningTypes)
                .build();
        // 기타내용 가져오기.
        String memo = apartmentApplicationForm.getMemo();

        return ApartmentApplicationFormResponseDto.builder()
                .date(DateUtils.format(apartmentApplicationForm.getCreatedDateTime(), "yyyy. MM. dd"))
                .progressStatus(apartmentApplicationForm.getProgressStatus().getCode())
                .user(applyUserDto)
                .address(createAddressResponseDto)
                .info(apartmentApplyInfoDto)
                .meal(meal)
                .memo(memo)
                .rejectedReason(apartmentApplicationForm.getRejectedReason())
                .build();
    }

    @Override
    public CorporationApplicationFormResponseDto getCorporationApplicationFormDetail(BigInteger userId, Long id) {
        // 기업 스팟 신청서 가져오기
        CorporationApplicationForm corporationApplicationForm = applicationFormValidator.isValidCorporationApplicationForm(userId, id);
        // 담당자 정보 가져오기
        ApplyUserDto applyUserDto = ApplyUserDto.builder()
                .name(corporationApplicationForm.getApplierName())
                .email(corporationApplicationForm.getEmail())
                .phone(corporationApplicationForm.getPhone()).build();
        // 기업 주소 가져오기
        Address address = corporationApplicationForm.getAddress();
        String addressString = address.getAddress1() + " " + address.getAddress2();

        // 스팟 정보 가져오기
        List<CorporationApplicationFormSpot> spots = corporationApplicationFormSpotRepository.findByCorporationApplicationForm(corporationApplicationForm);
        List<CorporationSpotResponseDto> spotList = new ArrayList<>();
        for (CorporationApplicationFormSpot spot : spots) {
            spotList.add(CorporationSpotResMapper.INSTANCE.toDto(spot));
        }

        // 식사 정보 가져오기
        List<CorporationApplicationMealInfo> mealInfos = corporationApplicationMealRepository.findByCorporationApplicationForm(corporationApplicationForm);
        List<String> diningTypes = new ArrayList<>();
        List<CorporationMealInfoResponseDto> mealInfoList = new ArrayList<>();
        for (CorporationApplicationMealInfo mealInfo : mealInfos) {
            mealInfoList.add(corporationMealInfoResMapper.toDto(mealInfo));
            diningTypes.add(mealInfo.getDiningType().getDiningType());
        }

        // 기업 정보 가져오기
        CorporationApplyInfoDto corporationApplyInfoDto = CorporationApplyInfoDto.builder()
                .corporationName(corporationApplicationForm.getCorporationName())
                .employeeCount(corporationApplicationForm.getEmployeeCount())
                .startDate(DateUtils.format(corporationApplicationForm.getServiceStartDate(), "yyyy. MM. dd"))
                .diningTypes(diningTypes)
                .build();

        // 옵션 정보 가져오기
        CorporationOptionsDto corporationOptionsDto = CorporationOptionsDto.builder()
                .isGarbage(corporationApplicationForm.getIsGarbage())
                .isHotStorage(corporationApplicationForm.getIsHotStorage())
                .isSetting(corporationApplicationForm.getIsSetting())
                .memo(corporationApplicationForm.getMemo())
                .build();

        return CorporationApplicationFormResponseDto.builder()
                .date(DateUtils.format(corporationApplicationForm.getCreatedDateTime(), "yyyy. MM. dd"))
                .progressStatus(corporationApplicationForm.getProgressStatus().getCode())
                .user(applyUserDto)
                .address(addressString)
                .corporationInfo(corporationApplyInfoDto)
                .spots(spotList)
                .mealDetails(mealInfoList)
                .option(corporationOptionsDto)
                .rejectedReason(corporationApplicationForm.getRejectedReason())
                .build();
    }

    @Override
    public List<ApplicationFormDto> getSpotsApplicationList(BigInteger userId) {
        // 유저가 등록한 기업/아파트 신청서 정보 리스트 가져오기
        List<CorporationApplicationForm> corporationApplicationForms = corporationApplicationFormRepository.findByUserId(userId);
        List<ApartmentApplicationForm> apartmentApplicationForms = apartmentApplicationFormRepository.findByUserId(userId);
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
