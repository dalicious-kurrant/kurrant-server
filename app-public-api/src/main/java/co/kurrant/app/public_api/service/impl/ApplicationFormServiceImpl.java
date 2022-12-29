package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.dto.CreateAddressResponseDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.*;
import co.dalicious.domain.application_form.dto.apartment.*;
import co.dalicious.domain.application_form.dto.corporation.*;
import co.dalicious.domain.application_form.entity.*;
import co.dalicious.domain.application_form.repository.*;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.public_api.dto.client.*;
import co.kurrant.app.public_api.service.ApplicationFormService;
import co.kurrant.app.public_api.service.CommonService;
import co.kurrant.app.public_api.service.impl.mapper.CorporationMealInfoReqMapper;
import co.kurrant.app.public_api.service.impl.mapper.CorporationMealInfoResMapper;
import co.kurrant.app.public_api.service.impl.mapper.CorporationSpotReqMapper;
import co.kurrant.app.public_api.service.impl.mapper.CorporationSpotResMapper;
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
    private final CorporationApplicationFormRepository corporationApplicationFormRepository;
    private final CorporationApplicationFormSpotRepository corporationApplicationFormSpotRepository;
    private final CorporationMealInfoRepository corporationMealInfoRepository;

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
            ApartmentMealInfo apartmentMealInfo = applyMealInfoRepository.save(
                    ApartmentMealInfo.builder()
                    .apartmentMealInfoRequestDto(apartmentMealInfoRequestDto)
                    .apartmentApplicationForm(apartmentApplicationForm)
                    .build());
            apartmentMealInfoList.add(apartmentMealInfo);
        }
        apartmentApplicationForm.setMealInfoList(apartmentMealInfoList);
    }

    @Override
    @Transactional
    public ApartmentApplicationFormResponseDto getApartmentApplicationFormDetail(HttpServletRequest httpServletRequest, Long id) {
        // 가져오는 신청서의 작성자가 로그인한 유저와 일치하는지 확인
        ApartmentApplicationForm apartmentApplicationForm = applicationFormValidator.isValidApartmentApplicationForm(commonService.getUserId(httpServletRequest), id);
        // 등록한 유저의 정보 가져오기
        ApplyUserDto applyUserDto = ApplyUserDto.builder().name(apartmentApplicationForm.getApplierName()).email(apartmentApplicationForm.getEmail()).phone(apartmentApplicationForm.getPhone()).build();
        // 아파트의 주소 가져오기
        Address address = apartmentApplicationForm.getAddress();
        CreateAddressResponseDto createAddressResponseDto = new CreateAddressResponseDto(address);
        // 아파트 정보 가져오기
        ApartmentApplyInfoDto apartmentApplyInfoDto = ApartmentApplyInfoDto.builder().apartmentName(apartmentApplicationForm.getApartmentName()).dongCount(apartmentApplicationForm.getDongCount()).familyCount(apartmentApplicationForm.getTotalFamilyCount()).serviceStartDate(DateUtils.format(apartmentApplicationForm.getServiceStartDate(), "yyyy. MM. dd")).build();
        // 식사 정보 가져오기
        List<ApartmentMealInfoResponseDto> meal = new ArrayList<>();
        for (ApartmentMealInfo apartmentMealInfo : apartmentApplicationForm.getMealInfoList()) {
            meal.add(ApartmentMealInfoResponseDto.builder().apartmentMealInfo(apartmentMealInfo).build());
        }
        // 기타내용 가져오기.
        String memo = apartmentApplicationForm.getMemo();

        return ApartmentApplicationFormResponseDto.builder().user(applyUserDto).address(createAddressResponseDto).info(apartmentApplyInfoDto).meal(meal).memo(memo).build();
    }

    @Override
    @Transactional
    public void updateApartmentApplicationFormMemo(HttpServletRequest httpServletRequest, Long id, ApplicationFormMemoDto applicationFormMemoDto) {
        ApartmentApplicationForm apartmentApplicationForm = applicationFormValidator.isValidApartmentApplicationForm(commonService.getUserId(httpServletRequest), id);
        apartmentApplicationForm.updateMemo(apartmentApplicationForm.getMemo());
    }

    @Override
    @Transactional
    public void registerCorporationSpot(HttpServletRequest httpServletRequest, CorporationApplicationFormRequestDto corporationApplicationFormRequestDto) {
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
        CorporationOptionsApplicationFormRequestDto corporationOptionsApplicationFormRequestDto = corporationApplicationFormRequestDto.getOption();

        // 기업 스팟 신청서 저장
        CorporationApplicationForm corporationApplicationForm = corporationApplicationFormRepository.save(
                CorporationApplicationForm.builder()
                .userId(userId)
                .applyUserDto(applyUserDto)
                .applyInfoDto(applyInfoDto)
                .address(address)
                .corporationOptionsApplicationFormRequestDto(corporationOptionsApplicationFormRequestDto)
                .build());

        // 스팟 신청 정보 저장
        List<CorporationApplicationFormSpot> spotList = new ArrayList<>();
        for(CorporationSpotRequestDto spot : spots) {
            CorporationApplicationFormSpot corporationSpotApplicationForm = CorporationSpotReqMapper.INSTANCE.toEntity(spot);
            corporationSpotApplicationForm.setCorporationApplicationForm(corporationApplicationForm);
            spotList.add(corporationApplicationFormSpotRepository.save(corporationSpotApplicationForm));
        }
        corporationApplicationForm.setSpots(spotList);
        // 식사 정보 저장
        List<CorporationMealInfo> mealInfoList = new ArrayList<>();
        for(CorporationMealInfoRequestDto mealInfoRequestDto : mealInfoRequestDtoList) {
            CorporationMealInfo corporationMealInfo = CorporationMealInfoReqMapper.INSTANCE.toEntity(mealInfoRequestDto);
            corporationMealInfo.setApplicationFormCorporation(corporationApplicationForm);
            mealInfoList.add(corporationMealInfoRepository.save(corporationMealInfo));
        }
        corporationApplicationForm.setMealInfoList(mealInfoList);
    }

    @Override
    public CorporationApplicationFormResponseDto getCorporationApplicationFormDetail(HttpServletRequest httpServletRequest, Long id) {
        // 유저 아이디 가져오기
        BigInteger userId = commonService.getUserId(httpServletRequest);
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
        // 기업 정보 가져오기
        CorporationApplyInfoDto corporationApplyInfoDto = CorporationApplyInfoDto.builder()
                .corporationName(corporationApplicationForm.getCorporationName())
                .employeeCount(corporationApplicationForm.getEmployeeCount())
                .startDate(DateUtils.format(corporationApplicationForm.getServiceStartDate(), "yyyy. MM. dd"))
                .build();

        // 스팟 정보 가져오기
        List<CorporationApplicationFormSpot> spots = corporationApplicationFormSpotRepository.findByCorporationApplicationForm(corporationApplicationForm);
        List<CorporationSpotResponseDto> spotList = new ArrayList<>();
        for(CorporationApplicationFormSpot spot : spots) {
            spotList.add(CorporationSpotResMapper.INSTANCE.toDto(spot));
        }

        // 식사 정보 가져오기
        List<CorporationMealInfo> mealInfos = corporationMealInfoRepository.findByCorporationApplicationForm(corporationApplicationForm);
        List<CorporationMealInfoResponseDto> mealInfoList = new ArrayList<>();
        for(CorporationMealInfo mealInfo : mealInfos) {
            mealInfoList.add(CorporationMealInfoResMapper.INSTANCE.toDto(mealInfo));
        }

        return CorporationApplicationFormResponseDto.builder()
                .user(applyUserDto)
                .address(addressString)
                .corporationInfo(corporationApplyInfoDto)
                .spots(spotList)
                .mealDetails(mealInfoList)
                .build();
    }

    @Override
    public void updateCorporationApplicationFormMemo(HttpServletRequest httpServletRequest, Long id, ApplicationFormMemoDto applicationFormMemoDto) {

    }

    @Override
    public ApplicationFormDto getSpotsApplicationList(HttpServletRequest httpServletRequest) {
        return null;
    }
}
