package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.dto.CreateAddressResponseDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.*;
import co.dalicious.domain.application_form.entity.ApartmentApplicationForm;
import co.dalicious.domain.application_form.entity.ApplyMealInfo;
import co.dalicious.domain.application_form.repository.ApplicationFormApartmentRepository;
import co.dalicious.domain.application_form.repository.ApplyMealInfoRepository;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.public_api.dto.client.*;
import co.kurrant.app.public_api.service.ApplicationFormService;
import co.kurrant.app.public_api.service.CommonService;
import exception.ApiException;
import exception.ExceptionEnum;
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
    private final ApplyMealInfoRepository applyMealInfoRepository;
    private final ApplicationFormApartmentRepository applicationFormApartmentRepository;

    @Override
    @Transactional
    public void registerApartmentSpot(HttpServletRequest httpServletRequest, ApartmentApplicationFormRequestDto apartmentApplicationFormRequestDto) {
        // 유저 아이디 가져오기
        BigInteger userId = commonService.getUserId(httpServletRequest);

        // 담당자 정보 가져오기
        ApplyUserDto applyUserDto = apartmentApplicationFormRequestDto.getUser();

        // 스팟 신청 아파트 정보 가져오기
        ApartmentApplyInfoDto apartmentApplyInfoDto = apartmentApplicationFormRequestDto.getInfo();

        // 스팟 신청 아파트 주소 정보 가져오기
        CreateAddressRequestDto createAddressRequestDto = apartmentApplicationFormRequestDto.getAddress();
        Address address = Address.builder()
                .createAddressRequestDto(createAddressRequestDto)
                .build();

        // 식사 정보 리스트 가져오기
        List<ApplyMealInfoRequestDto> applyMealInfoRequestDtoList = apartmentApplicationFormRequestDto.getMeal();

        // 기타 내용 가져오기
        String memo = apartmentApplicationFormRequestDto.getMemo();

        // 스팟 신청 정보 저장
        ApartmentApplicationForm apartmentApplicationForm = applicationFormApartmentRepository.save(ApartmentApplicationForm.builder()
                .userId(userId)
                .applyUserDto(applyUserDto)
                .apartmentApplyInfoDto(apartmentApplyInfoDto)
                .address(address)
                .memo(memo)
                .build());

        // 식사 정보 리스트 저장
        List<ApplyMealInfo> applyMealInfoList = new ArrayList<>();
        for (ApplyMealInfoRequestDto applyMealInfoRequestDto : applyMealInfoRequestDtoList) {
            applyMealInfoRequestDto.insertApplicationFormApartment(apartmentApplicationForm);
            ApplyMealInfo applyMealInfo = applyMealInfoRepository.save(ApplyMealInfo.builder()
                    .applyMealInfoRequestDto(applyMealInfoRequestDto)
                    .apartmentApplicationForm(apartmentApplicationForm)
                    .build());
            applyMealInfoList.add(applyMealInfo);
        }
        apartmentApplicationForm.setMealInfoList(applyMealInfoList);
    }

    @Override
    @Transactional
    public ApartmentApplicationFormResponseDto getApartmentApplicationFormDetail(HttpServletRequest httpServletRequest, Long id) {
        // id에 해당하는 아파트 스팟 신청 내역 찾기
        ApartmentApplicationForm apartmentApplicationForm = applicationFormApartmentRepository.findById(id).orElseThrow(
                () -> new ApiException(ExceptionEnum.APPLICATION_FORM_NOT_FOUND)
        );

        // 로그인 한 사용자와 신청한 유저의 아이디가 일치하는지 확인
        if (!commonService.getUserId(httpServletRequest).equals(apartmentApplicationForm.getUserId())) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }

        ApplyUserDto applyUserDto = ApplyUserDto.builder()
                .name(apartmentApplicationForm.getApplierName())
                .email(apartmentApplicationForm.getEmail())
                .phone(apartmentApplicationForm.getPhone())
                .build();

        Address address = apartmentApplicationForm.getAddress();
        CreateAddressResponseDto createAddressResponseDto = CreateAddressResponseDto.builder()
                .address1(address.getAddress1())
                .address2(address.getAddress2())
                .build();

        ApartmentApplyInfoDto apartmentApplyInfoDto = ApartmentApplyInfoDto.builder()
                .apartmentName(apartmentApplicationForm.getApartmentName())
                .dongCount(apartmentApplicationForm.getDongCount())
                .familyCount(apartmentApplicationForm.getTotalFamilyCount())
                .serviceStartDate(DateUtils.format(apartmentApplicationForm.getServiceStartDate(), "yyyy. MM. dd"))
                .build();

        List<ApplyMealInfoResponseDto> meal = new ArrayList<>();
        for (ApplyMealInfo applyMealInfo : apartmentApplicationForm.getMealInfoList()) {
            meal.add(ApplyMealInfoResponseDto.builder()
                    .applyMealInfo(applyMealInfo)
                    .build());
        }

        String memo = apartmentApplicationForm.getMemo();

        return ApartmentApplicationFormResponseDto.builder()
                .user(applyUserDto)
                .address(createAddressResponseDto)
                .info(apartmentApplyInfoDto)
                .meal(meal)
                .memo(memo)
                .build();
    }

    @Override
    public void saveApartmentApplicationFormMemo(HttpServletRequest httpServletRequest, Long id, ApartmentApplicationFormMemoDto apartmentApplicationFormMemoDto) {

    }

    @Override
    public void registerCorporationSpot(HttpServletRequest httpServletRequest, CorporationSpotApplicationFormDto apartmentSpotApplicationFormDto) {

    }

    @Override
    public ApplicationFormDto getSpotsApplicationList(HttpServletRequest httpServletRequest) {
        return null;
    }
}
