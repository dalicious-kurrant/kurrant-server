package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.core.entity.RefreshToken;
import co.dalicious.client.core.filter.provider.JwtTokenProvider;
import co.dalicious.client.core.repository.RefreshTokenRepository;
import co.dalicious.client.oauth.SnsLoginResponseDto;
import co.dalicious.client.oauth.SnsLoginService;
import co.dalicious.data.redis.pubsub.SseService;
import co.dalicious.domain.application_form.utils.ApplicationUtil;
import co.dalicious.domain.client.dto.GroupCountDto;
import co.dalicious.domain.client.dto.SpotListResponseDto;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Employee;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.OpenGroup;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.repository.EmployeeRepository;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.client.repository.QGroupRepository;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.domain.payment.dto.*;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.payment.entity.enums.PaymentPasswordStatus;
import co.dalicious.domain.payment.mapper.CreditCardInfoMapper;
import co.dalicious.domain.payment.repository.CreditCardInfoRepository;
import co.dalicious.domain.payment.repository.QCreditCardInfoRepository;
import co.dalicious.domain.payment.service.PaymentService;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.entity.enums.*;
import co.dalicious.domain.user.mapper.UserSpotMapper;
import co.dalicious.domain.user.repository.*;
import co.dalicious.domain.user.util.ClientUtil;
import co.dalicious.domain.user.util.FoundersUtil;
import co.dalicious.domain.user.util.MembershipUtil;
import co.dalicious.domain.user.validator.UserValidator;
import co.dalicious.domain.user.mapper.UserGroupMapper;
import co.dalicious.system.enums.RequiredAuth;
import co.dalicious.domain.user.dto.UserGroupDto;
import co.kurrant.app.public_api.dto.user.*;
import co.kurrant.app.public_api.mapper.user.UserHomeInfoMapper;
import co.kurrant.app.public_api.mapper.user.UserPersonalInfoMapper;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserService;
import co.kurrant.app.public_api.util.UserUtil;
import co.kurrant.app.public_api.util.VerifyUtil;
import co.kurrant.app.public_api.util.WordsUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.json.simple.parser.ParseException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.*;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserUtil userUtil;
    private final PaymentService paymentService;
    private final SnsLoginService snsLoginService;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final VerifyUtil verifyUtil;
    private final MembershipUtil membershipUtil;
    private final ProviderEmailRepository providerEmailRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserSpotRepository userSpotRepository;
    private final UserHomeInfoMapper userHomeInfoMapper;
    private final UserPersonalInfoMapper userPersonalInfoMapper;
    private final QCreditCardInfoRepository qCreditCardInfoRepository;
    private final CreditCardInfoRepository creditCardInfoRepository;
    private final CreditCardInfoMapper creditCardInfoMapper;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final FoundersUtil foundersUtil;
    private final ClientUtil clientUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final QUserRepository qUserRepository;
    private final EmployeeRepository employeeRepository;
    private final GroupRepository groupRepository;
    private final UserGroupMapper userGroupMapper;
    private final UserSpotMapper userSpotMapper;
    private final QGroupRepository qGroupRepository;
    private final ApplicationUtil applicationUtil;
    private final SseService sseService;


    @Override
    @Transactional
    public UserHomeResponseDto getUserHomeInfo(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        UserHomeResponseDto userHomeResponseDto = userHomeInfoMapper.toDto(user);
        userHomeResponseDto.setMembershipUsingPeriod(membershipUtil.getUserPeriodOfUsingMembership(user));
        userHomeResponseDto.setFoundersNumber(foundersUtil.getFoundersNumber(user));
        userHomeResponseDto.setLeftFoundersNumber(foundersUtil.getLeftFoundersNumber());
        userHomeResponseDto.setRequestedMySpotDto(applicationUtil.findExistRequestedMySpot(user.getId()));
        return userHomeResponseDto;
    }

    @Override
    @Transactional
    public void connectSnsAccount(SecurityUser securityUser, SnsAccessToken snsAccessToken, String sns) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        // 등록된 SNS 플랫폼인지 확인
        Provider provider = UserValidator.isValidProvider(sns);

        // 현재 로그인 한 아이디가 같은 Vendor의 아이디와 연결되어있는지 체크
        List<ProviderEmail> providerEmails = providerEmailRepository.findAllByUser(user);
        if (providerEmails.stream().anyMatch(pe -> pe.getProvider().equals(provider))) {
            throw new ApiException(ExceptionEnum.CANNOT_CONNECT_SNS);
        }

        // Vendor 로그인 시도
        SnsLoginResponseDto snsLoginResponseDto = snsLoginService.getSnsLoginUserInfo(provider, snsAccessToken.getSnsAccessToken());

        // Response 값이 존재하지 않으면 예외 발생
        if (snsLoginResponseDto == null) {
            throw new ApiException(ExceptionEnum.CANNOT_CONNECT_SNS);
        }

        String email = snsLoginResponseDto.getEmail();

        // 해당 아이디를 가지고 있는 유저가 존재하지는 지 확인.
        Optional<ProviderEmail> providerEmail = providerEmailRepository.findOneByProviderAndEmail(provider, email);

        // 존재한다면 예외 발생
        if (providerEmail.isPresent()) {
            throw new ApiException(ExceptionEnum.ALREADY_EXISTING_USER);
        }
        // 존재하지 않는다면 저장하기.
        else {
            ProviderEmail newProviderEmail = ProviderEmail.builder()
                    .provider(provider)
                    .email(email)
                    .user(user)
                    .build();

            providerEmailRepository.save(newProviderEmail);
        }
    }

    @Override
    @Transactional
    public void connectAppleAccount(SecurityUser securityUser, Map<String, Object> appleLoginDto) throws JsonProcessingException {
        String sns = "APPLE";
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        // 등록된 SNS 플랫폼인지 확인
        Provider provider = UserValidator.isValidProvider(sns);

        // 현재 로그인 한 아이디가 같은 Vendor의 아이디와 연결되어있는지 체크
        List<ProviderEmail> providerEmails = providerEmailRepository.findAllByUser(user);
        if (providerEmails.stream().anyMatch(pe -> pe.getProvider().equals(provider))) {
            throw new ApiException(ExceptionEnum.CANNOT_CONNECT_SNS);
        }

        // Vendor 로그인 시도
        SnsLoginResponseDto snsLoginResponseDto = snsLoginService.getAppleLoginUserInfo(appleLoginDto);

        String email = snsLoginResponseDto.getEmail();

        // 해당 아이디를 가지고 있는 유저가 존재하지는 지 확인.
        Optional<ProviderEmail> providerEmail = providerEmailRepository.findOneByProviderAndEmail(provider, email);

        // 존재한다면 예외 발생
        if (providerEmail.isPresent()) {
            throw new ApiException(ExceptionEnum.ALREADY_EXISTING_USER);
        }
        // 존재하지 않는다면 저장하기.
        else {
            ProviderEmail newProviderEmail = ProviderEmail.builder()
                    .provider(provider)
                    .email(email)
                    .user(user)
                    .build();

            providerEmailRepository.save(newProviderEmail);
        }
    }

    @Override
    @Transactional
    public void disconnectSnsAccount(SecurityUser securityUser, String sns) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        // 등록된 SNS 플랫폼인지 확인
        Provider provider = UserValidator.isValidProvider(sns);

        // 현재 로그인 한 아이디가 이메일/비밀번호를 설정했는지 확인
        List<ProviderEmail> providerEmails = providerEmailRepository.findAllByUser(user);
        if (providerEmails == null) {
            throw new ApiException(ExceptionEnum.USER_NOT_FOUND);
        }
        providerEmails.stream()
                .filter(e -> e.getProvider().equals(Provider.GENERAL))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.GENERAL_PROVIDER_NOT_FOUND));

        // 현재 로그인 한 아이디가 같은 Vendor의 아이디와 연결되어있는지 체크
        ProviderEmail providerEmail = providerEmails.stream()
                .filter(e -> e.getProvider().equals(provider))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.USER_NOT_FOUND));

        // 소셜로그인 연결 계정 삭제
        providerEmailRepository.deleteById(providerEmail.getId());
    }

    @Override
    @Transactional
    public void changePhoneNumber(SecurityUser securityUser, ChangePhoneRequestDto changePhoneRequestDto) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        // 로그인한 유저의 정보를 받아온다.
        User user = userUtil.getUser(securityUser);
        // 입력한 휴대폰 번호가 기존에 등록된 번호인지 확인한다.
        userValidator.isPhoneValid(changePhoneRequestDto.getPhone());
        // 인증번호가 일치하는지 확인한다.
        verifyUtil.verifyCertificationNumber(changePhoneRequestDto.getKey(), RequiredAuth.MYPAGE_CHANGE_PHONE_NUMBER);
        // 비밀번호를 업데이트 한다.
        user.changePhoneNumber(changePhoneRequestDto.getPhone());
    }

    @Override
    @Transactional
    public void changePassword(SecurityUser securityUser, ChangePasswordDto changePasswordRequestDto) {
        // 로그인한 유저의 정보를 받아온다.
        User user = userUtil.getUser(securityUser);
        // 현재 비밀번호가 일치하는지 확인
        if (!passwordEncoder.matches(changePasswordRequestDto.getCurrantPassword(), user.getPassword())) {
            throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
        }
        // 변경할 비밀번호가 현재 비밀번호와 같을 경우 에러 발생
        if (passwordEncoder.matches(changePasswordRequestDto.getNewPassword(), user.getPassword())) {
            throw new ApiException(ExceptionEnum.CHANGED_PASSWORD_SAME);
        }

        // 새로 등록한 번호 두개가 일치하는 지 확인
        if (!changePasswordRequestDto.getNewPassword().equals(changePasswordRequestDto.getNewPasswordCheck())) {
            throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
        }
        // 비밀번호 변경
        String hashedPassword = passwordEncoder.encode(changePasswordRequestDto.getNewPassword());
        user.changePassword(hashedPassword);
    }

    @Override
    @Transactional
    public void setEmailAndPassword(SecurityUser securityUser, SetEmailAndPasswordDto setEmailAndPasswordDto) {
        // 로그인한 유저의 정보를 받아온다.
        User user = userUtil.getUser(securityUser);
        String email = setEmailAndPasswordDto.getEmail();

        // 기존에 존재하는 이메일인지 확인
        userValidator.isEmailValid(Provider.GENERAL, email);

        // 다른 계정에서 주요 이메일(아이디)로 사용하는 이메일인지 확인. 소셜로그인 계정과 이메일이 같을 경우 제외.
        if (!user.getEmail().equals(setEmailAndPasswordDto.getEmail())) {
            userValidator.isExistingMainEmail(email);
        }
        // 인증을 진행한 유저인지 체크
        verifyUtil.isAuthenticated(email, RequiredAuth.MYPAGE_SETTING_EMAIL_AND_PASSWORD);

        // 비밀번호 일치 확인
        String password = setEmailAndPasswordDto.getPassword();
        String passwordCheck = setEmailAndPasswordDto.getPasswordCheck();
        UserValidator.isPasswordMatched(password, passwordCheck);

        // 이메일/비밀번호 업데이트
        String hashedPassword = passwordEncoder.encode(password);
        user.setEmailAndPassword(email, hashedPassword);

        //그룹에서 초대된 유저인지 확인 후 초대된 유저라면 group과 spot 등록
        List<Employee> employeeList = employeeRepository.findAllByEmail(email);
        if (!employeeList.isEmpty()){
            userGroupSave(employeeList, user);
        }

        // 일반 로그인 저장
        ProviderEmail providerEmail = ProviderEmail.builder()
                .provider(Provider.GENERAL)
                .email(email)
                .user(user)
                .build();
        providerEmailRepository.save(providerEmail);
    }

    private void userGroupSave(List<Employee> employeeList, User user) {
        for (Employee employee : employeeList){
            Group group = groupRepository.findById(employee.getCorporation().getId()).orElseThrow(() -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND));
            UserGroup userGroup = userGroupMapper.toUserGroup(user, group, ClientStatus.BELONG);
            userGroupRepository.save(userGroup);
            //스팟도 추가
            if (!group.getSpots().isEmpty()){
                UserSpot userSpot = userSpotMapper.toUserSpot(group.getSpots().get(0), user, true, GroupDataType.CORPORATION);
                userSpotRepository.save(userSpot);
            }
        }
    }

    /*
        @Override
        @Transactional
        public MarketingAlarmResponseDto getAlarmSetting(SecurityUser securityUser) {
            // 유저 정보 가져오기
            User user = userUtil.getUser(securityUser);
            Timestamp marketingAgreedDateTime = user.getMarketingAgreedDateTime();
            return MarketingAlarmResponseDto.builder()
                    .marketingAgree(user.getMarketingAgree())
                    .orderAlarm(user.getOrderAlarm())
                    .marketingAlarm(user.getMarketingAlarm())
                    .marketingAgreedDateTime(marketingAgreedDateTime == null ? null : DateUtils.format(user.getMarketingAgreedDateTime(), "yyyy년 MM월 dd일"))
                    .build();
        }


        @Override
        @Transactional
        public MarketingAlarmResponseDto changeAlarmSetting(SecurityUser securityUser, MarketingAlarmRequestDto marketingAlarmDto) {
                // 유저 정보 가져오기
                User user = userUtil.getUser(securityUser);
                Boolean currantMarketingInfoAgree = user.getMarketingAgree();
                Boolean currantMarketingAlarmAgree = user.getMarketingAlarm();
                Boolean currantOrderAlarmAgree = user.getOrderAlarm();

                // 현재 시간 가져오기
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());

                // 변수 설정
                Boolean isMarketingInfoAgree = marketingAlarmDto.getIsMarketingInfoAgree();
                Boolean isMarketingAlarmAgree = marketingAlarmDto.getIsMarketingAlarmAgree();
                Boolean isOrderAlarmAgree = marketingAlarmDto.getIsOrderAlarmAgree();

                user.changeMarketingAgreement(isMarketingInfoAgree, isMarketingAlarmAgree, isOrderAlarmAgree);

                return MarketingAlarmResponseDto.builder()
                        .marketingAgree(currantMarketingInfoAgree)
                        .marketingAgreedDateTime(DateUtils.format(now, "yyyy년 MM월 dd일"))
                        .marketingAlarm(currantMarketingAlarmAgree)
                        .orderAlarm(currantOrderAlarmAgree)
                        .build();
            }
        */
    @Override
    @Transactional
    public List<MarketingAlarmResponseDto> getAlarmSetting(SecurityUser securityUser) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);
        List<PushCondition> userPushConditionList = user.getPushConditionList();
        List<PushCondition> pushConditionList = new ArrayList<>(Arrays.asList(PushCondition.values()));
        pushConditionList.removeAll(PushCondition.getNoShowCondition());

        return pushConditionList.stream().map(c -> userPersonalInfoMapper.toMarketingAlarmResponseDto(userPushConditionList, c)).toList();
    }

    @Override
    @Transactional
    public List<MarketingAlarmResponseDto> changeAlarmSetting(SecurityUser securityUser, MarketingAlarmRequestDto marketingAlarmDto) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);
        List<PushCondition> userPushConditionList = new ArrayList<>();
        if (user.getPushConditionList() != null && !user.getPushConditionList().isEmpty()) {
            userPushConditionList = user.getPushConditionList();
        }

        if (marketingAlarmDto.getIsActive()) {
            PushCondition pushCondition = PushCondition.ofCode(marketingAlarmDto.getCode());
            userPushConditionList.add(pushCondition);
        } else {
            PushCondition pushCondition = userPushConditionList.stream()
                    .filter(c -> c.getCode().equals(marketingAlarmDto.getCode()))
                    .findFirst().orElseThrow(() -> new ApiException(ExceptionEnum.ALREADY_NOT_ACTIVE));
            userPushConditionList.remove(pushCondition);
        }
        List<PushCondition> finalUserPushConditionList = userPushConditionList;
        user.updatePushCondition(finalUserPushConditionList);

        List<PushCondition> pushConditionList = List.of(PushCondition.class.getEnumConstants());
        return pushConditionList.stream().map(c -> userPersonalInfoMapper.toMarketingAlarmResponseDto(finalUserPushConditionList, c)).toList();
    }

    @Override
    @Transactional
    public UserPersonalInfoDto getPersonalUserInfo(SecurityUser securityUser) {
        // 로그인한 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);
        // 일반 로그인 정보를 가지고 있는 유저인지 검사
        List<ProviderEmail> providerEmails = user.getProviderEmails();
        UserPersonalInfoDto userPersonalInfoDto = userPersonalInfoMapper.toDto(user);
        userPersonalInfoDto.setMembershipPeriod(membershipUtil.getUserPeriodOfUsingMembership(user));
        Boolean hasGeneralProvider = providerEmails.stream()
                .anyMatch(e -> e.getProvider().equals(Provider.GENERAL));

        // 일반 로그인을 가지고 있는 유저인지 아닌지 상태 업데이트.
        userPersonalInfoDto.hasGeneralProvider(hasGeneralProvider);
        return userPersonalInfoDto;
    }

    @Override
    @Transactional
    public UserInfoDto getUserInfo(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        Integer membershipPeriod = membershipUtil.getUserPeriodOfUsingMembership(user);

        // 식사 일정 개수 구하기
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findAllMealScheduleByUser(user);
        Integer dailyMealCount = getDailyFoodScheduleCount(orderItemDailyFoods);

        return UserInfoDto.builder()
                .user(user)
                .membershipPeriod(membershipPeriod)
                .dailyMealCount(dailyMealCount)
                .build();
    }

    @Override
    @Transactional
    public void settingOpenGroup(SecurityUser securityUser, BigInteger groupId) {
        User user = userUtil.getUser(securityUser);
        Group group = qGroupRepository.findGroupByTypeAndId(groupId, GroupDataType.OPEN_GROUP);
        if (group == null) throw new ApiException(ExceptionEnum.GROUP_NOT_FOUND);

        List<UserGroup> userGroups = user.getActiveUserGroups();

        // TODO: 그룹 슬롯 증가의 경우 반영 필요
        // 오픈 스팟 그룹의 개수가 2개 이상일 떄
        long userGroupCount = userGroups.stream()
                .filter(v -> v.getGroup() instanceof OpenGroup)
                .count();
        if (userGroupCount >= 2) {
            throw new ApiException(ExceptionEnum.REQUEST_OVER_GROUP);
        }

        OpenGroup openGroup = (OpenGroup) group;
        Optional<UserGroup> selectedGroup = userGroups.stream().filter(g -> g.getGroup().equals(group)).findAny();
        if (selectedGroup.isPresent()) {
            if (selectedGroup.get().getClientStatus() == ClientStatus.WITHDRAWAL) {
                selectedGroup.get().updateStatus(ClientStatus.BELONG);
                openGroup.updateOpenGroupUserCount(1, true);
                sseService.send(user.getId(), 7, null, null, null);
                return;
            }
            return;
        }

        UserGroup userCorporation = userGroupMapper.toUserGroup(user, group, ClientStatus.BELONG);
        userGroupRepository.save(userCorporation);
        openGroup.updateOpenGroupUserCount(1, true);
        sseService.send(user.getId(), 7, null, null, null);
    }

    @Override
    @Transactional
    public GroupCountDto getClients(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        // 현재 활성화된 유저 그룹일 경우만 가져오기
        List<UserGroup> userGroups = user.getActiveUserGroups();
        // 그룹/스팟 리스트를 담아줄 Dto 생성하기
        List<SpotListResponseDto> spotListResponseDtoList = new ArrayList<>();
        // 그룹 추가
        for (UserGroup userGroup : userGroups) {
            SpotListResponseDto spotListResponseDto = userGroupMapper.toSpotListResponseDto(userGroup);
            spotListResponseDtoList.add(spotListResponseDto);

        }
        // 기업 -> 공유 -> 마이 스팟 별로 정렬.
        spotListResponseDtoList = spotListResponseDtoList.stream().sorted(Comparator.comparing(SpotListResponseDto::getSpotType)).sorted(Comparator.comparingInt(dto -> {
                    int spotType = dto.getSpotType();
                    if (spotType == 0) {
                        return 0;
                    } else if (spotType == 2) {
                        return 1;
                    } else {
                        return 2;
                    }
                }))
                .toList();

        return userGroupMapper.toGroupCountDto(spotListResponseDtoList);
    }

    @Override
    @Transactional
    public UserGroupDto getClientManagement(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        return userGroupMapper.toUserGroupDto(user);
    }

    @Override
    @Transactional
    public Integer isHideEmail(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        Optional<ProviderEmail> providerEmail = user.getProviderEmails().stream()
                .filter(v -> v.getProvider().equals(Provider.GENERAL))
                .findAny();

        if (user.getEmail().contains("appleid")) {
            return 3;
        } else return 2;
    }

    @Override
    @Transactional
    public String createNiceBillingKeyFirst(SecurityUser securityUser, Integer typeId, BillingKeyDto billingKeyDto) throws IOException, ParseException {
        //유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        PaymentPasswordStatus paymentPasswordStatus = PaymentPasswordStatus.ofCode(typeId);

        // TYPE1: 결제 비밀번호가 등록이 된 유저
        if (paymentPasswordStatus.equals(PaymentPasswordStatus.HAS_PAYMENT_PASSWORD)) {
            // 결제 비밀번호가 6자리인지 확인
            if (user.getPaymentPassword() == null || billingKeyDto.getPayNumber() == null || billingKeyDto.getPayNumber().equals("") || billingKeyDto.getPayNumber().length() != 6) {
                throw new ApiException(ExceptionEnum.PAYMENT_PASSWORD_LENGTH_ERROR);
            }
            //결제 비밀번호 일치 확인
            if (!passwordEncoder.matches(billingKeyDto.getPayNumber(), user.getPaymentPassword())) {
                throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
            }
        }

        // TYPE2: 결제 비밀번호가 등록되지 않았으면서, 애플 유저가 아닌 경우
        if (paymentPasswordStatus.equals(PaymentPasswordStatus.NOT_HAVE_PAYMENT_PASSWORD_GENERAL)) {
            // 결제 비밀번호가 6자리인지 확인
            if (billingKeyDto.getPayNumber() == null || billingKeyDto.getPayNumber().equals("") || billingKeyDto.getPayNumber().length() != 6) {
                throw new ApiException(ExceptionEnum.PAYMENT_PASSWORD_LENGTH_ERROR);
            }
            // 이메일 인증을 하였는지 검증
            verifyUtil.isAuthenticated(user.getEmail(), RequiredAuth.PAYMENT_PASSWORD_CREATE);

            // 결제 비밀번호 등록
            String password = passwordEncoder.encode(billingKeyDto.getPayNumber());
            user.updatePaymentPassword(password);
        }

        // TYPE3: 결제 비밀번호가 등록되지 않았으면서, 애플 유저인 경우
        if (paymentPasswordStatus.equals(PaymentPasswordStatus.NOT_HAVE_PAYMENT_PASSWORD_AND_HIDE_EMAIL)) {
            // 결제 비밀번호가 6자리인지 확인
            if (billingKeyDto.getPayNumber() == null || billingKeyDto.getPayNumber().equals("") || billingKeyDto.getPayNumber().length() != 6) {
                throw new ApiException(ExceptionEnum.PAYMENT_PASSWORD_LENGTH_ERROR);
            }

            String email = billingKeyDto.getEmail();

            if (email == null || email.isEmpty()) {
                throw new ApiException(ExceptionEnum.NOT_FOUND);
            }

            // 이메일 인증을 하였는지 검증
            verifyUtil.isAuthenticated(email, RequiredAuth.PAYMENT_PASSWORD_CREATE_APPLE);

            // 이메일과 비밀번호 설정
            String password = passwordEncoder.encode(billingKeyDto.getPassword());
            user.setEmailAndPassword(email, password);
            ProviderEmail providerEmail = new ProviderEmail(Provider.GENERAL, email, user);
            providerEmailRepository.save(providerEmail);

            // 결제 비밀번호 등록
            String payPassword = passwordEncoder.encode(billingKeyDto.getPayNumber());
            user.updatePaymentPassword(payPassword);
        }

        CreditCardDto.Response saveCardResponse = paymentService.getBillingKey(billingKeyDto.getCardNumber(), billingKeyDto.getExpirationYear(), billingKeyDto.getExpirationMonth(), billingKeyDto.getCardPassword(), billingKeyDto.getIdentityNumber());

        int defaultType = (billingKeyDto.getDefaultType() == null) ? 0 : billingKeyDto.getDefaultType();

        // 중복 카드확인
        List<CreditCardInfo> creditCardInfos = creditCardInfoRepository.findAllByUserId(user.getId());

        Optional<CreditCardInfo> creditCardInfo = creditCardInfos.stream()
                .filter(v -> v.isSameCard(saveCardResponse.getCardNumber(), saveCardResponse.getCardCompany()))
                .findAny();

        if (creditCardInfo.isPresent()) {
            // 이미 존재하는 카드라면 에러 발생
            if (creditCardInfo.get().getStatus() == 1) {
                throw new ApiException(ExceptionEnum.ALREADY_EXIST_CARD);
            }
            // 기존에 삭제되었던 카드라면 빌링키 업데이트
            if (creditCardInfo.get().getStatus() == 0) {
                creditCardInfo.get().updateStatus(1);
                creditCardInfo.get().updateNiceBillingKey(saveCardResponse.getBillingKey());
                return saveCardResponse.getBillingKey();
            }
        }
        // 등록된 카드가 없다면 기본카드로 셋팅
        if (creditCardInfos.size() == 0 && defaultType == 0) {
            defaultType = 1;
        }

        CreditCardInfo cardInfo = creditCardInfoMapper.toEntity(saveCardResponse, user.getId(), defaultType);

        creditCardInfoRepository.save(cardInfo);

        return cardInfo.getNiceBillingKey();
    }

    @Override
    public List<CreditCardResponseDto> getCardList(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        List<CreditCardInfo> creditCardInfoList = qCreditCardInfoRepository.findAllByUserId(user.getId());
        //결과값 담아줄 LIST 생성
        List<CreditCardResponseDto> resultList = new ArrayList<>();

        for (CreditCardInfo creditCardInfo : creditCardInfoList) {
            CreditCardResponseDto creditCardResponseDto = creditCardInfoMapper.toDto(creditCardInfo);
            resultList.add(creditCardResponseDto);

        }
        return resultList;
    }

    @Override
    @Transactional
    public void patchDefaultCard(SecurityUser securityUser, CreditCardDefaultSettingDto creditCardDefaultSettingDto) {
        User user = userUtil.getUser(securityUser);

        //입력받은 ID에 해당하는 카드 조회
        Optional<CreditCardInfo> updateCardInfo = creditCardInfoRepository.findById(creditCardDefaultSettingDto.getCardId());

        //DefaultType을 변경요청하는 경우는 3가지가 있다. 0/1(기본)/2(멤버십)

        //0으로 변경 요청한 경우는 그냥 0으로 변경해준다.
        if (creditCardDefaultSettingDto.getDefaultType() == 0) {
            //해당 카드를 입력받은 디폴트 번호로 수정
            qCreditCardInfoRepository.patchDefaultCard(updateCardInfo.get().getId(), creditCardDefaultSettingDto.getDefaultType());
        }

        //1로 변경 요청한 경우, 기존 DefaultType이 1인 카드를 0으로 변경하고, 해당 카드가 DefaultType이 2인 카드라면 3으로 변경해준다.
        if (creditCardDefaultSettingDto.getDefaultType() == 1) {
            //해당카드가 DefaultType이 2일경우 3으로 변경, 다른 모든카드는 0으로 변경
            if (updateCardInfo.get().getDefaultType() == 2) {
                Integer defaultType = 3;
                //해당 카드를 DefaultType 3으로 변경
                qCreditCardInfoRepository.patchDefaultCard(updateCardInfo.get().getId(), defaultType);
                //변경한 카드 외에 나머지를 모두 0으로 변경
                qCreditCardInfoRepository.patchOtherCardAllZero(updateCardInfo.get().getId(), user.getId());
            }
            //해당카드가 DefaultType이 1이나 3일 경우는 의미가 없으므로, 0일 경우에 1로 변경하고 기존 1을 0으로, 기존 3을 2로 변경한다.
            if (updateCardInfo.get().getDefaultType() == 0) {
                qCreditCardInfoRepository.patchDefaultCard(updateCardInfo.get().getId(), creditCardDefaultSettingDto.getDefaultType());
                //기존 1을 0으로
                qCreditCardInfoRepository.patchOneToZero(updateCardInfo.get().getId(), user.getId());
                //기존 3을 2로
                qCreditCardInfoRepository.patchThreeToTwo(updateCardInfo.get().getId(), user.getId());
            }
        }

        //2로 변경 요청한 경우, 기존 DefaultType이 2인 카드를 0으로 변경하고 해당 카드가 DefaultType이 1인 카드라면 3으로 변경해준다.
        if (creditCardDefaultSettingDto.getDefaultType() == 2) {
            //해당카드가 DefaultType이 1일경우 3으로 변경, 다른 모든카드는 0으로 변경
            if (updateCardInfo.get().getDefaultType() == 1) {
                Integer defaultType = 3;
                //해당 카드를 DefaultType 3으로 변경
                qCreditCardInfoRepository.patchDefaultCard(updateCardInfo.get().getId(), defaultType);
                //변경한 카드 외에 나머지를 모두 0으로 변경
                qCreditCardInfoRepository.patchOtherCardAllZero(updateCardInfo.get().getId(), user.getId());
            }
            //해당카드가 2나 3이면 변경의 의미가 없으므로 0인 경우에만 2로 바꾸고 기존 2를 0으로 기존 3을 1로 바꾼다.
            if (updateCardInfo.get().getDefaultType() == 0) {
                qCreditCardInfoRepository.patchDefaultCard(updateCardInfo.get().getId(), creditCardDefaultSettingDto.getDefaultType());
                //기존 2를 0으로
                qCreditCardInfoRepository.patchTwoToZero(updateCardInfo.get().getId(), user.getId());
                //기존 3을 1로
                qCreditCardInfoRepository.patchThreeToZero(updateCardInfo.get().getId(), user.getId());
            }
        }


    }

    @Override
    @Transactional
    public void deleteCard(DeleteCreditCardDto deleteCreditCardDto) {
        qCreditCardInfoRepository.deleteCard(deleteCreditCardDto.getCardId());
    }

    @Override
    @Transactional
    public void changeName(SecurityUser securityUser, ChangeNameDto changeNameDto) {
        User user = userUtil.getUser(securityUser);

        if (!user.getName().equals("이름없음")) {
            throw new ApiException(ExceptionEnum.ALREADY_EXISTING_NAME);
        }

        user.updateName(changeNameDto.getName());
    }

    @Override
    @Transactional
    public void changeNickname(SecurityUser securityUser, String nickname) {
        User user = userUtil.getUser(securityUser);
        WordsUtil.isContainingSwearWords(nickname);
        user.updateNickname(nickname);
    }

    @Override
    @Transactional
    public void withdrawal(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        user.updateUserStatus(UserStatus.REQUEST_WITHDRAWAL);
    }

    @Override
    @Transactional
    public void withdrawalCancel(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        user.updateUserStatus(UserStatus.ACTIVE);
    }

    @Override
    @Transactional
    public LoginResponseDto autoLogin(HttpServletRequest httpServletRequest) {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);

        if (!jwtTokenProvider.validateToken(token)) {
            throw new ApiException(ExceptionEnum.ACCESS_TOKEN_ERROR);
        }

        String userId = jwtTokenProvider.getUserPk(token);

        User user = userRepository.findById(BigInteger.valueOf(Integer.parseInt(userId)))
                .orElseThrow(() -> new ApiException(ExceptionEnum.USER_NOT_FOUND));

        List<RefreshToken> refreshTokenHashes = refreshTokenRepository.findAllByUserId(BigInteger.valueOf(Integer.parseInt(userId)));

        if (refreshTokenHashes.isEmpty()) {
            throw new ApiException(ExceptionEnum.REFRESH_TOKEN_ERROR);
        }
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        user.updateRecentLoginDateTime(timestamp);

        Integer leftWithdrawDays = null;

        if (user.getUserStatus().equals(UserStatus.REQUEST_WITHDRAWAL)) {
            LocalDateTime withdrawRequestDateTime = user.getUpdatedDateTime().toLocalDateTime();
            Duration interval = Duration.between(withdrawRequestDateTime, LocalDateTime.now());
            leftWithdrawDays = (int) interval.toDays();
        }

        return LoginResponseDto.builder()
                .accessToken(token)
                .refreshToken(refreshTokenHashes.get(0).getRefreshToken())
                .hasNickname(user.hasNickname())
                .isActive(user.getUserStatus().equals(UserStatus.ACTIVE))
                .expiresIn(jwtTokenProvider.getExpiredIn(token))
                .leftWithdrawDays(leftWithdrawDays)
                .spotStatus(clientUtil.getSpotStatus(user).getCode())
                .build();
    }

    @Override
    @Transactional
    public void saveToken(FcmTokenSaveReqDto fcmTokenSaveReqDto, SecurityUser securityUser) {
        //유저ID로 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        long result = qUserRepository.saveFcmToken(fcmTokenSaveReqDto.getToken(), user.getId());
        if (result != 1) {
            throw new ApiException(ExceptionEnum.TOKEN_SAVE_FAILED);
        }
    }

    @Override
    @Transactional
    public String savePaymentPassword(SecurityUser securityUser, SavePaymentPasswordDto savePaymentPasswordDto) {
        //유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        //이메일이 등록되어 있지 않은 유저라면 예외처리.
        UserValidator.isAuthorizedUser(user);

        //이메일 인증 검증
        verifyUtil.verifyCertificationNumber(savePaymentPasswordDto.getKey(), RequiredAuth.PAYMENT_PASSWORD_CREATE);

        //결제 비밀번호가 등록이 안된 유저라면
        if (savePaymentPasswordDto.getPayNumber() != null && !savePaymentPasswordDto.getPayNumber().equals("")) {
            //결제 비밀번호 등록
            if (savePaymentPasswordDto.getPayNumber().length() == 6) {
                String password = passwordEncoder.encode(savePaymentPasswordDto.getPayNumber());
                qUserRepository.updatePaymentPassword(password, user.getId());
            } else {
                throw new ApiException(ExceptionEnum.PAYMENT_PASSWORD_LENGTH_ERROR);
            }
        }


        return "결제 비밀번호 등록 성공!";
    }

    @Override
    public String checkPaymentPassword(SecurityUser securityUser, SavePaymentPasswordDto savePaymentPasswordDto) {
        User user = userUtil.getUser(securityUser);

        if (user.getPaymentPassword() == null) {
            throw new ApiException(ExceptionEnum.NOT_FOUND_PAYMENT_PASSWORD);
        }

        if (savePaymentPasswordDto.getPayNumber() != null && !savePaymentPasswordDto.getPayNumber().equals("")) {
            //결제 비밀번호 확인
            if (savePaymentPasswordDto.getPayNumber().length() == 6) {
                if (!passwordEncoder.matches(savePaymentPasswordDto.getPayNumber(), user.getPaymentPassword())) {
                    throw new ApiException(ExceptionEnum.PAYMENT_PASSWORD_NOT_MATCH);
                }
            } else {
                throw new ApiException(ExceptionEnum.PAYMENT_PASSWORD_LENGTH_ERROR);
            }
        }

        return "결제 비밀번호 확인 성공!";
    }

    @Override
    public Boolean isPaymentPassword(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        return user.getPaymentPassword() != null;
    }

    @Override
    @Transactional
    public void paymentPasswordReset(SecurityUser securityUser, PaymentResetReqDto resetDto) {
        User user = userUtil.getUser(securityUser);
        String paymentPassword = passwordEncoder.encode(resetDto.getPayNumber());
        if (resetDto.getPayNumber().equals("") || resetDto.getPayNumber() == null) {
            throw new ApiException(ExceptionEnum.PAYMENT_PASSWORD_RESET_FAILED);
        }
        if (resetDto.getPayNumber().length() == 6) {
            qUserRepository.resetPaymentPassword(user.getId(), paymentPassword);
        } else {
            throw new ApiException(ExceptionEnum.PAYMENT_PASSWORD_LENGTH_ERROR);
        }
    }

    //    @Override
//    @Transactional
//    public Integer saveCreditCard(SecurityUser securityUser, SaveCreditCardRequestDto saveCreditCardRequestDto) throws IOException, ParseException {
//        User user = userUtil.getUser(securityUser);
//
//        /*영문 대소문자, 숫자, 특수문자 -, _, =, ., @로 이루어진 최소 2자 이상 최대 300자 이하의 문자열*/
//        //ASCII코드상 숫자 48~57 / 영대문자 65~90 / 영소문자 97~122
//        String customerKey = tossUtil.createCustomerKey();
//
//        System.out.println(customerKey + "customerKey");
//
//        String identityNumber = saveCreditCardRequestDto.getIdentityNumber().substring(2);
//
//        //TOSS에 요청하기 위한 request 객체 빌드
//        JSONObject response = tossUtil.cardRegisterRequest(saveCreditCardRequestDto.getCardNumber(), saveCreditCardRequestDto.getExpirationYear(), saveCreditCardRequestDto.getExpirationMonth(),
//                saveCreditCardRequestDto.getCardPassword(), identityNumber, customerKey);
//        System.out.println(response + " RESPONSE CHECK===========================================");
//
//        //빌링키가 없다면 Exception 처리
//        if (!response.containsKey("billingKey")) {
//            throw new ApiException(ExceptionEnum.FAIL_TO_CREDITCARD_REGIST);
//        }
//        /*
//        빌링키  / 카드번호 / 카드회사 / 카드타입 / 오너타입
//        * */
//        String billingKey = response.get("billingKey").toString();
//        String cardNumber = response.get("cardNumber").toString();
//        String cardCompany = response.get("cardCompany").toString();
//        JSONObject cardObject = (JSONObject) response.get("card");
//        String cardType = cardObject.get("cardType").toString();
//        String ownerType = cardObject.get("ownerType").toString();
//
//        Integer defaultType = saveCreditCardRequestDto.getDefaultType();
//
//
//        //카드 등록하기전에 중복카드가 존재하는지 확인
//        List<CreditCardInfo> cardInfoList = qCreditCardInfoRepository.findAllByUserId(user.getId());
//        if (cardInfoList.size() != 0) {
//            if (defaultType == null) {
//                defaultType = 0;
//            }
//            for (CreditCardInfo card : cardInfoList) {
//                if (cardNumber.equals(card.getCardNumber()) && cardCompany.equals(card.getCardCompany()) && card.getStatus() != 0) {
//                    return 2;
//                }
//                //중복카드지만 status가 0인 경우는 삭제된 카드를 재등록하는 경우이므로 Status값을 1로 바꿔준다.
//                if (cardNumber.equals(card.getCardNumber()) && cardCompany.equals(card.getCardCompany()) && card.getStatus() != 1) {
//                    qCreditCardInfoRepository.updateStatusCard(card.getId(), billingKey);
//                }
//            }
//        }
//        //중복카드가 없고 디폴트타입이 null일 경우는 디폴트 타입으로 설정
//        if (cardInfoList.size() == 0 && defaultType == null) {
//            defaultType = 1;
//        }
//
//        //CreditCard 저장을 위한 엔티티 매핑
//        CreditCardInfo creditCardInfo = creditCardInfoSaveMapper.toSaveEntity(cardNumber, user.getId(), ownerType, cardType, customerKey, billingKey, cardCompany, defaultType);
//
//        //카드정보 저장
//        creditCardInfoRepository.save(creditCardInfo);
//        return 1;
//    }


    private Integer getDailyFoodScheduleCount(List<OrderItemDailyFood> orderItemDailyFoods) {
        return orderItemDailyFoods.stream()
                .filter(orderItemDailyFood -> OrderStatus.beforeDelivered().contains(orderItemDailyFood.getOrderStatus()))
                .mapToInt(OrderItemDailyFood::getCount)
                .sum();
    }
    @Override
    public String generateRandomNickName() throws IOException {
        return UserUtil.generateRandomNickName();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Boolean isMembershipSupport(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        return user.getActiveUserGroups().stream()
                .map(UserGroup::getGroup)
                .filter(group -> Hibernate.getClass(group).equals(Corporation.class))
                .anyMatch(group -> ((Corporation) Hibernate.unproxy(group)).getIsMembershipSupport());
    }
}
