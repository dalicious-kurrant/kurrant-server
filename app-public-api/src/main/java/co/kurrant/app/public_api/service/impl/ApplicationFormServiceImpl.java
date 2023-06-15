package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.address.repository.QRegionRepository;
import co.dalicious.domain.address.utils.AddressUtil;
import co.dalicious.domain.application_form.dto.ApplicationFormDto;
import co.dalicious.domain.application_form.dto.PushAlarmSettingDto;
import co.dalicious.domain.application_form.dto.apartment.ApartmentApplicationFormRequestDto;
import co.dalicious.domain.application_form.dto.apartment.ApartmentApplicationFormResponseDto;
import co.dalicious.domain.application_form.dto.apartment.ApartmentMealInfoRequestDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationApplicationFormRequestDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationApplicationFormResponseDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationMealInfoRequestDto;
import co.dalicious.domain.application_form.dto.corporation.CorporationSpotRequestDto;
import co.dalicious.domain.application_form.dto.mySpotZone.MySpotZoneApplicationFormRequestDto;
import co.dalicious.domain.application_form.dto.share.ShareSpotDto;
import co.dalicious.domain.application_form.entity.*;
import co.dalicious.domain.application_form.entity.enums.ShareSpotRequestType;
import co.dalicious.domain.application_form.mapper.*;
import co.dalicious.domain.application_form.repository.*;
import co.dalicious.domain.application_form.validator.ApplicationFormValidator;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.entity.enums.SpotStatus;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.UserSpot;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.repository.UserGroupRepository;
import co.dalicious.domain.user.repository.UserSpotRepository;
import co.dalicious.domain.client.entity.MySpot;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.integration.client.user.entity.Region;
import co.dalicious.integration.client.user.mapper.UserGroupMapper;
import co.dalicious.integration.client.user.mapper.UserSpotMapper;
import co.dalicious.domain.client.repository.MySpotRepository;
import co.dalicious.domain.client.repository.QMySpotZoneRepository;
import co.kurrant.app.public_api.dto.client.ApplicationFormMemoDto;
import co.dalicious.domain.application_form.mapper.MySpotMapper;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.ApplicationFormService;
import co.kurrant.app.public_api.service.UserUtil;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

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
    private final RequestedMySpotZonesRepository requestedMySpotZonesRepository;
    private final UserGroupRepository userGroupRepository;
    private final MySpotRepository mySpotRepository;
    private final UserGroupMapper userGroupMapper;
    private final RequestedShareSpotMapper requestedShareSpotMapper;
    private final RequestedShareSpotRepository requestedShareSpotRepository;
    private final RequestedMySpotMapper requestedMySpotMapper;
    private final RequestedMySpotRepository requestedMySpotRepository;
    private final UserSpotMapper userSpotMapper;
    private final UserSpotRepository userSpotRepository;
    private final QRequestedMySpotRepository qRequestedMySpotRepository;

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
    public void deleteRequestedMySpot(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        RequestedMySpot requestedMySpot = qRequestedMySpotRepository.findRequestedMySpotByUserId(user.getId());
        requestedMySpotRepository.delete(requestedMySpot);
    }

    @Override
    @Transactional
    public void updateRequestedMySpotAlarmUser(SecurityUser securityUser, PushAlarmSettingDto dto) {
        User user = userUtil.getUser(securityUser);

        GroupDataType type = GroupDataType.ofCode(dto.getSpotType());

        if(type.equals(GroupDataType.MY_SPOT)) {
            RequestedMySpot requestedMySpot = qRequestedMySpotRepository.findRequestedMySpotById(dto.getId());
            requestedMySpot.getRequestedMySpotZones().updatePushAlarmUserIds(user.getId());
        }
    }

    @Override
    @Transactional
    public ApplicationFormDto registerMySpot(SecurityUser securityUser, MySpotZoneApplicationFormRequestDto requestDto) throws ParseException {
        // user 찾기
        User user = userUtil.getUser(securityUser);
        if(user.getPhone() == null || !user.getPhone().equals(requestDto.getPhone())) user.updatePhone(requestDto.getPhone());

        // 신청한 my spot이 이미 존재하면
//        RequestedMySpot existRequestedMySpot = qRequestedMySpotRepository.findRequestedMySpotByUserId(user.getId());
//        if(existRequestedMySpot != null) throw new ApiException(ExceptionEnum.OVER_MY_SPOT_LIMIT);

        // my spot zone 찾기
        MySpotZone mySpotZone = qMySpotZoneRepository.findExistMySpotZoneByZipcode(requestDto.getAddress().getZipCode());

        if(mySpotZone != null) {
            mySpotZone.updateMySpotZoneUserCount(1, SpotStatus.ACTIVE);
            // my spot 생성
            MySpot mySpot = mySpotMapper.toMySpot(user.getId(), mySpotZone, requestDto);
            mySpot.updateGroup(mySpotZone);
            mySpotRepository.save(mySpot);

            // 동일한 user group에 등록되어 있으면 패스
            UserGroup userGroup = user.getGroups().stream().filter(g -> g.getGroup().equals(mySpotZone)).findAny().orElse(null);
            // user group 생성
            if(userGroup == null) userGroupRepository.save(userGroupMapper.toUserGroup(user, mySpotZone));
            else userGroup.updateStatus(ClientStatus.BELONG);

            // user spot 생성
            UserSpot userSpot = userSpotMapper.toUserSpot(mySpot, user, false, GroupDataType.MY_SPOT);
            userSpotRepository.save(userSpot);

            return applicationMapper.toApplicationFromDto(mySpot.getId(), mySpot.getName(), mySpot.getAddress(), GroupDataType.MY_SPOT.getCode(),true);
        }

        // my spot zone 없으면 my spot zone 신청하기
        RequestedMySpotZones existRequestedMySpotZones = qRequestedMySpotZonesRepository.findRequestedMySpotZoneByZipcode(requestDto.getAddress().getZipCode());
        if(existRequestedMySpotZones != null) {
            RequestedMySpot requestedMySpot = requestedMySpotMapper.toEntity(user.getId(), existRequestedMySpotZones, requestDto);
            requestedMySpotRepository.save(requestedMySpot);

            Set<BigInteger> userIds = existRequestedMySpotZones.getRequestedMySpots().stream()
                    .map(RequestedMySpot::getUserId)
                    .collect(Collectors.toSet());

            if(!userIds.contains(user.getId())) {
                userIds.add(user.getId());
                existRequestedMySpotZones.updateWaitingUserCount(1);
            }

            return applicationMapper.toApplicationFromDto(requestedMySpot.getId(), requestedMySpot.getName(), requestedMySpot.getAddress(), GroupDataType.MY_SPOT.getCode(), false);
        }

        String[] jibunAddress = requestDto.getAddress().getAddress3().split(" ");
        System.out.println("jibunAddress = " + Arrays.toString(jibunAddress));
        String county = null;
        String village = null;

        for(String addr : jibunAddress) {
            if(addr.endsWith("구")) county = addr;
            else if(addr.endsWith("동")) village = addr;
        }

        Region region = qRegionRepository.findRegionByZipcodeAndCountyAndVillage(requestDto.getAddress().getZipCode(), county, Objects.requireNonNull(village));
        RequestedMySpotZones requestedMySpotZones = requestedMySpotZonesMapper.toRequestedMySpotZones(1, null, region, user.getId());
        requestedMySpotZonesRepository.save(requestedMySpotZones);

        RequestedMySpot requestedMySpot = requestedMySpotMapper.toEntity(user.getId(), requestedMySpotZones, requestDto);
        requestedMySpotRepository.save(requestedMySpot);

        // my spot zone 존재 여부 response
        return applicationMapper.toApplicationFromDto(requestedMySpot.getId(), requestedMySpot.getName(), requestedMySpot.getAddress(), GroupDataType.MY_SPOT.getCode(), false);
    }

    @Override
    @Transactional
    public void registerShareSpot(SecurityUser securityUser, Integer typeId, ShareSpotDto.Request request) throws ParseException {
        User user = userUtil.getUser(securityUser);
        ShareSpotRequestType shareSpotRequestType = ShareSpotRequestType.ofCode(typeId);
        RequestedShareSpot requestedShareSpot = requestedShareSpotMapper.toEntity(request);
        requestedShareSpot.updateShareSpotRequestType(shareSpotRequestType);
        requestedShareSpot.updateUserId(user.getId());
        requestedShareSpotRepository.save(requestedShareSpot);
    }
}
