package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.ApartmentApplicationFormRequestDto;
import co.dalicious.domain.application_form.dto.ApartmentApplyInfoDto;
import co.dalicious.domain.application_form.dto.ApplyMealInfoDto;
import co.dalicious.domain.application_form.dto.ApplyUserDto;
import co.dalicious.domain.application_form.entity.ApplicationFormApartment;
import co.dalicious.domain.application_form.entity.ApplyMealInfo;
import co.dalicious.domain.application_form.repository.ApplicationFormApartmentRepository;
import co.dalicious.domain.application_form.repository.ApplyMealInfoRepository;
import co.kurrant.app.public_api.dto.client.*;
import co.kurrant.app.public_api.service.ApplicationFormService;
import co.kurrant.app.public_api.service.CommonService;
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
        List<ApplyMealInfoDto> applyMealInfoDtoList = apartmentApplicationFormRequestDto.getMeal();

        // 기타 내용 가져오기
        String option = apartmentApplicationFormRequestDto.getOption();

        // 스팟 신청 정보 저장
        ApplicationFormApartment applicationFormApartment = applicationFormApartmentRepository.save(ApplicationFormApartment.builder()
                .userId(userId)
                .applyUserDto(applyUserDto)
                .apartmentApplyInfoDto(apartmentApplyInfoDto)
                .address(address)
                .build());

        // 식사 정보 리스트 저장
        List<ApplyMealInfo> applyMealInfoList = new ArrayList<>();
        for (ApplyMealInfoDto applyMealInfoDto : applyMealInfoDtoList) {
            applyMealInfoDto.insertApplicationFormApartment(applicationFormApartment);
            ApplyMealInfo applyMealInfo = applyMealInfoRepository.save(ApplyMealInfo.builder()
                    .applyMealInfoDto(applyMealInfoDto)
                    .applicationFormApartment(applicationFormApartment)
                    .build());
            applyMealInfoList.add(applyMealInfo);
        }
        applicationFormApartment.setMealInfoList(applyMealInfoList);
    }

    @Override
    public ApartmentApplicationFormResponseDto getApartmentApplicationFormDetail(HttpServletRequest httpServletRequest, Long id) {
        return null;
    }

    @Override
    public void editApartmentApplicationForm(HttpServletRequest httpServletRequest, Long id, EditApartmentApplicationFormDto editApartmentApplicationFormDto) {

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
