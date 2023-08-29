package co.kurrant.app.public_api.service.impl;

import co.dalicious.data.redis.dto.SseReceiverDto;
import co.dalicious.data.redis.repository.NotificationHashRepository;
import co.dalicious.domain.address.entity.Region;
import co.dalicious.domain.address.repository.QRegionRepository;
import co.dalicious.domain.application_form.dto.ApplicationFormDto;
import co.dalicious.domain.application_form.dto.PushAlarmSettingDto;
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
import co.dalicious.domain.application_form.utils.ApplicationSlackUtil;
import co.dalicious.domain.application_form.validator.ApplicationFormValidator;
import co.dalicious.domain.client.entity.MySpot;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.entity.enums.MySpotZoneStatus;
import co.dalicious.domain.client.entity.enums.SpotStatus;
import co.dalicious.domain.client.repository.MySpotRepository;
import co.dalicious.domain.client.repository.QMySpotZoneRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.UserSpot;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.repository.UserGroupRepository;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.domain.user.repository.UserSpotRepository;
import co.dalicious.domain.user.mapper.UserGroupMapper;
import co.dalicious.domain.user.mapper.UserSpotMapper;
import co.kurrant.app.public_api.dto.client.ApplicationFormMemoDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.ApplicationFormService;
import co.kurrant.app.public_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationFormServiceImpl implements ApplicationFormService {
    private final UserUtil userUtil;
    private final ApplicationSlackUtil applicationSlackUtil;
    private final ApplicationFormValidator applicationFormValidator;
    private final CorporationApplicationFormRepository corporationApplicationFormRepository;
    private final CorporationApplicationFormSpotRepository corporationApplicationFormSpotRepository;
    private final CorporationApplicationMealRepository corporationApplicationMealRepository;
    private final CorporationMealInfoReqMapper corporationMealInfoReqMapper;
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
    private final ApplicationEventPublisher applicationEventPublisher;
    private final NotificationHashRepository notificationHashRepository;
    private final UserRepository userRepository;


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

        Optional<User> optionalUser = userRepository.findById(userId);
        String message = null;
        if (optionalUser.isPresent()) {
            message = "[기업스팟] 신청 내역이 있어요!\n" + " 스팟 이름 : " + corporationApplicationForm.getCorporationName() +
                    "\n 인원 수 : " + corporationApplicationForm.getEmployeeCount() +
                    "\n 신청자 이름 : " + optionalUser.get().getName() +
                    "\n 연락처 : " + optionalUser.get().getPhone();
        }

        applicationSlackUtil.sendSlack(message);

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
        List<RequestedMySpot> requestedMySpotList = qRequestedMySpotRepository.findAllRequestedMySpotByUserId(userId);
        List<ApplicationFormDto> applicationFormDtos = new ArrayList<>();
        // 응답값 생성
        for (CorporationApplicationForm corporationApplicationForm : corporationApplicationForms) {
            applicationFormDtos.add(ApplicationFormDto.builder()
                    .id(corporationApplicationForm.getId())
                    .clientType(1)
                    .name(corporationApplicationForm.getCorporationName())
                    .build());
        }
        for (RequestedMySpot requestedMySpot : requestedMySpotList) {
            applicationFormDtos.add(ApplicationFormDto.builder()
                    .id(requestedMySpot.getId())
                    .clientType(0)
                    .name(requestedMySpot.getName())
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
            if(requestedMySpot != null) requestedMySpot.getRequestedMySpotZones().updatePushAlarmUserIds(user.getId());
            else {
                Optional<UserSpot> userSpot = user.getUserSpots().stream().filter(s -> s.getSpot().getId().equals(dto.getId())).findAny();
                userSpot.ifPresent(v -> {
                    if(v.getSpot() instanceof MySpot mySpot) mySpot.updateAlarm(true);
                });
            }
        }
    }

    @Override
    @Transactional
    public ApplicationFormDto registerMySpot(SecurityUser securityUser, MySpotZoneApplicationFormRequestDto requestDto) throws ParseException {
        if(requestDto.getAddress().getZipCode().isEmpty() || requestDto.getAddress().getZipCode().isBlank() || requestDto.getAddress().getAddress3() == null) throw new ApiException(ExceptionEnum.CANT_NOT_REQUESTED_SPOT);

        // user 찾기
        User user = userUtil.getUser(securityUser);
        if(user.getPhone() == null) user.updatePhone(requestDto.getPhone());
        else if(requestDto.getPhone() == null) requestDto.setPhone(user.getPhone());

        // 신청한 my spot이 이미 존재하면
        RequestedMySpot existRequestedMySpot = qRequestedMySpotRepository.findRequestedMySpotByUserId(user.getId());
        if(existRequestedMySpot != null) return updateRequestedMySpot(existRequestedMySpot, requestDto, user);

        // 신청한 my spot 없으면
        MySpotZone mySpotZone = qMySpotZoneRepository.findExistMySpotZoneByZipcode(requestDto.getAddress().getZipCode());
        if(mySpotZone != null) return updateMySpotZone(user, requestDto, mySpotZone);

        // my spot zone 없으면 my spot zone 신청하기
        RequestedMySpotZones existRequestedMySpotZones = qRequestedMySpotZonesRepository.findRequestedMySpotZoneByZipcode(requestDto.getAddress().getZipCode());
        if(existRequestedMySpotZones != null) {
            RequestedMySpot requestedMySpot = requestedMySpotMapper.toEntity(user.getId(), existRequestedMySpotZones, requestDto);
            requestedMySpotRepository.save(requestedMySpot);
            updateRequestedMySpotZonesUserCount(user, existRequestedMySpotZones);

            return applicationMapper.toApplicationFromDto(requestedMySpot.getId(), requestedMySpot.getName(), requestedMySpot.getAddress(), GroupDataType.MY_SPOT.getCode(), false);
        }

        RequestedMySpotZones requestedMySpotZones = createRequestedMySpotZones(requestDto, user);
        RequestedMySpot requestedMySpot = requestedMySpotMapper.toEntity(user.getId(), requestedMySpotZones, requestDto);
        requestedMySpotRepository.save(requestedMySpot);

        String message = "[마이스팟] 등록 신청 내역이 있어요! \n"
                        +" 스팟 이름 : " +requestedMySpot.getName()
                        +"\n 신청자 이름 : " + user.getName()
                        +"\n 연락처 : " + user.getPhone();

        applicationSlackUtil.sendSlack(message);


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

        String message = "[공유스팟] 등록 신청 내역이 있어요!"
                +"\n 스팟 주소 : " + requestedShareSpot.getAddress().getAddress1() + requestedShareSpot.getAddress().getAddress2()
                +"\n 신청자 이름 : " + user.getName()
                +"\n 연락처  : " + user.getPhone();

        applicationSlackUtil.sendSlack(message);
    }
    
    private ApplicationFormDto updateRequestedMySpot(RequestedMySpot requestedMySpot, MySpotZoneApplicationFormRequestDto requestDto, User user) throws ParseException {
        // my spot zone 있으면
        MySpotZone mySpotZone = qMySpotZoneRepository.findExistMySpotZoneByZipcode(requestDto.getAddress().getZipCode());
        if(mySpotZone != null) return updateMySpotZone(user, requestDto, mySpotZone);

        // my spot zone 없으면 my spot zone 신청하기
        RequestedMySpotZones existRequestedMySpotZones = qRequestedMySpotZonesRepository.findRequestedMySpotZoneByZipcode(requestDto.getAddress().getZipCode());
        if(existRequestedMySpotZones != null) {
            requestedMySpotMapper.updateRequestedMySpot(requestDto, requestedMySpot);
            requestedMySpot.updateRequestedMySpotZones(existRequestedMySpotZones);

            return applicationMapper.toApplicationFromDto(requestedMySpot.getId(), requestedMySpot.getName(), requestedMySpot.getAddress(), GroupDataType.MY_SPOT.getCode(), false);
        }
        // 기존 신청 마이스팟 존에서 카운트 빼기
        requestedMySpot.getRequestedMySpotZones().updateWaitingUserCount(1, true);

        RequestedMySpotZones requestedMySpotZones = createRequestedMySpotZones(requestDto, user);
        requestedMySpotMapper.updateRequestedMySpot(requestDto, requestedMySpot);
        requestedMySpot.updateRequestedMySpotZones(requestedMySpotZones);

        // my spot zone 존재 여부 response
        return applicationMapper.toApplicationFromDto(requestedMySpot.getId(), requestedMySpot.getName(), requestedMySpot.getAddress(), GroupDataType.MY_SPOT.getCode(), false);
        
    }
    
    private RequestedMySpotZones createRequestedMySpotZones(MySpotZoneApplicationFormRequestDto requestDto, User user) {
        String[] jibunAddress = requestDto.getAddress().getAddress3().split(" ");
        System.out.println("jibunAddress = " + Arrays.toString(jibunAddress) + ", zipcode = " + requestDto.getAddress().getZipCode());
        String county = null;
        String village = null;

        for(String addr : jibunAddress) {
            if(addr.matches(".*?(?:시$|군$|구$)")) county = addr;
            else if(addr.matches(".*?(?:동$|읍$|면$|가$|로$)")) village = addr;
        }

        Region region = qRegionRepository.findRegionByZipcodeAndCountyAndVillage(requestDto.getAddress().getZipCode(), county, village);

        RequestedMySpotZones requestedMySpotZones = requestedMySpotZonesMapper.toRequestedMySpotZones(1, null, region, user.getId());
        requestedMySpotZonesRepository.save(requestedMySpotZones);

        return requestedMySpotZones;
    }

    private static void updateRequestedMySpotZonesUserCount(User user, RequestedMySpotZones existRequestedMySpotZones) {
        Set<BigInteger> userIds = existRequestedMySpotZones.getRequestedMySpots().stream()
                .map(RequestedMySpot::getUserId)
                .collect(Collectors.toSet());

        if(!userIds.contains(user.getId())) {
            userIds.add(user.getId());
            existRequestedMySpotZones.updateWaitingUserCount(1, false);
        }
    }

    private ApplicationFormDto updateMySpotZone(User user, MySpotZoneApplicationFormRequestDto requestDto, MySpotZone mySpotZone) throws ParseException {
        mySpotZone.updateMySpotZoneUserCount(1, SpotStatus.ACTIVE);
        // my spot 생성
        MySpot mySpot = mySpotMapper.toMySpot(user.getId(), mySpotZone, requestDto);
        mySpot.updateGroup(mySpotZone);
        mySpotRepository.save(mySpot);

        Boolean zoneStatus = mySpotZone.getMySpotZoneStatus().equals(MySpotZoneStatus.OPEN);

        // 동일한 user group에 등록되어 있으면 패스
        UserGroup userGroup = user.getGroups().stream().filter(g -> g.getGroup().equals(mySpotZone)).findAny().orElse(null);
        // user group 생성 - 없으면 생성 / 오픈이면 활성 / 오픈 대기면 비활성
        if (userGroup != null) {
            if (zoneStatus) {
                userGroup.updateStatus(ClientStatus.BELONG);
                applicationEventPublisher.publishEvent(new SseReceiverDto(user.getId(), 7, null, mySpotZone.getId(), null));
            }
            else userGroup.updateStatus(ClientStatus.WAITING);
        } else {
            if (zoneStatus) userGroupRepository.save(userGroupMapper.toUserGroup(user, mySpotZone, ClientStatus.BELONG));
            else userGroupRepository.save(userGroupMapper.toUserGroup(user, mySpotZone, ClientStatus.WAITING));
        }

        // user spot 생성
        UserSpot userSpot = userSpotMapper.toUserSpot(mySpot, user, false, GroupDataType.MY_SPOT);
        userSpotRepository.save(userSpot);

        return applicationMapper.toApplicationFromDto(mySpot.getId(), mySpot.getName(), mySpot.getAddress(), GroupDataType.MY_SPOT.getCode(), zoneStatus);
    }
}
