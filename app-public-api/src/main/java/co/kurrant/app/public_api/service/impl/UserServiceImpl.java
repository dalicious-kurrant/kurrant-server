package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.oauth.SnsLoginResponseDto;
import co.dalicious.client.oauth.SnsLoginService;
import co.dalicious.domain.client.dto.SpotListResponseDto;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.mapper.GroupResponseMapper;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.payment.dto.CreditCardSaveDto;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.payment.mapper.CreditCardInfoSaveMapper;
import co.dalicious.domain.payment.repository.CreditCardInfoRepository;
import co.dalicious.domain.payment.repository.QCreditCardInfoRepository;
import co.dalicious.domain.payment.util.TossUtil;
import co.dalicious.domain.user.dto.MembershipSubscriptionTypeDto;
import co.dalicious.domain.user.entity.ProviderEmail;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.Provider;
import co.dalicious.domain.user.repository.ProviderEmailRepository;
import co.dalicious.domain.user.repository.UserGroupRepository;
import co.dalicious.domain.user.util.MembershipUtil;
import co.dalicious.domain.user.validator.UserValidator;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.enums.RequiredAuth;
import co.kurrant.app.public_api.dto.user.*;
import co.kurrant.app.public_api.mapper.user.UserHomeInfoMapper;
import co.kurrant.app.public_api.mapper.user.UserPersonalInfoMapper;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserService;
import co.kurrant.app.public_api.service.UserUtil;
import co.kurrant.app.public_api.util.VerifyUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserUtil userUtil;
    private final SnsLoginService snsLoginService;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final VerifyUtil verifyUtil;
    private final MembershipUtil membershipUtil;
    private final ProviderEmailRepository providerEmailRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserHomeInfoMapper userHomeInfoMapper;
    private final UserPersonalInfoMapper userPersonalInfoMapper;
    private final GroupResponseMapper groupResponseMapper;
    private final GroupRepository groupRepository;
    private final QCreditCardInfoRepository qCreditCardInfoRepository;
    private final CreditCardInfoRepository creditCardInfoRepository;
    private final CreditCardInfoSaveMapper creditCardInfoSaveMapper;

    @Override
    @Transactional
    public UserHomeResponseDto getUserHomeInfo(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        UserHomeResponseDto userHomeResponseDto = userHomeInfoMapper.toDto(user);
        userHomeResponseDto.setMembershipUsingPeriod(membershipUtil.getUserPeriodOfUsingMembership(user));
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
    public void disconnectSnsAccount(SecurityUser securityUser, String sns) {
        // 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);

        // 등록된 SNS 플랫폼인지 확인
        Provider provider = UserValidator.isValidProvider(sns);

        // 현재 로그인 한 아이디가 이메일/비밀번호를 설정했는지 확인
        List<ProviderEmail> providerEmails = providerEmailRepository.findAllByUser(user);
        if(providerEmails == null) {
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
        if(passwordEncoder.matches(changePasswordRequestDto.getNewPassword(), user.getPassword())) {
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
        if(!user.getEmail().equals(setEmailAndPasswordDto.getEmail())) {
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

        // 일반 로그인 저장
        ProviderEmail providerEmail = ProviderEmail.builder()
                .provider(Provider.GENERAL)
                .email(email)
                .user(user)
                .build();
        providerEmailRepository.save(providerEmail);
    }

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

        // 마케팅 정보 수신 동의/철회
        if (isMarketingInfoAgree != null) {
            currantMarketingInfoAgree = isMarketingInfoAgree;
            currantMarketingAlarmAgree = isMarketingInfoAgree;
            currantOrderAlarmAgree = isMarketingInfoAgree;
            user.changeMarketingAgreement(now, currantMarketingAlarmAgree, currantMarketingAlarmAgree, currantOrderAlarmAgree);
        }
        // 혜택 및 소식 알림 동의/철회
        if (isMarketingAlarmAgree != null) {
            // 주문 알림이 활성화 되어 있을 경우
            if (currantOrderAlarmAgree) {
                currantMarketingAlarmAgree = isMarketingAlarmAgree;
                user.setMarketingAlarm(currantMarketingAlarmAgree);
            }
            // 주문 알림이 활성화 되어 있지 않을 경우
            else {
                currantMarketingInfoAgree = !currantMarketingInfoAgree;
                currantMarketingAlarmAgree = isMarketingAlarmAgree;
                user.changeMarketingAgreement(now, currantMarketingInfoAgree, currantMarketingAlarmAgree, currantOrderAlarmAgree);
            }
        }
        // 주문 알림 동의/철회
        if (isOrderAlarmAgree != null) {
            // 혜택 및 소식 알림이 활성화 되어 있을 경우
            if (currantMarketingAlarmAgree) {
                currantOrderAlarmAgree = isOrderAlarmAgree;
                user.setOrderAlarm(isOrderAlarmAgree);
            }
            // 혜택 및 소식 알림이 활성화 되어 있지 않을 경우
            else {
                currantMarketingInfoAgree = !currantMarketingInfoAgree;
                currantOrderAlarmAgree = isOrderAlarmAgree;
                user.changeMarketingAgreement(now, currantMarketingInfoAgree, currantMarketingAlarmAgree, currantOrderAlarmAgree);
            }
        }
        return MarketingAlarmResponseDto.builder()
                .marketingAgree(currantMarketingInfoAgree)
                .marketingAgreedDateTime(DateUtils.format(now, "yyyy년 MM월 dd일"))
                .marketingAlarm(currantMarketingAlarmAgree)
                .orderAlarm(currantOrderAlarmAgree)
                .build();
    }

    @Override
    @Transactional
    public UserPersonalInfoDto getPersonalUserInfo(SecurityUser securityUser) {
        // 로그인한 유저 정보 가져오기
        User user = userUtil.getUser(securityUser);
        // 일반 로그인 정보를 가지고 있는 유저인지 검사
        List<ProviderEmail> providerEmails = user.getProviderEmails();
        UserPersonalInfoDto userPersonalInfoDto = userPersonalInfoMapper.toDto(user);
        userPersonalInfoDto.setUsingMembershipPeriod(membershipUtil.getUserPeriodOfUsingMembership(user));
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
//        Integer dailyMealCount =

        return UserInfoDto.builder()
                .user(user)
                .membershipPeriod(membershipPeriod)
//                .dailyMealCount()
                .build();
    }

    @Override
    @Transactional
    // TODO: 추후 백오피스 구현시 삭제
    public void settingGroup(SecurityUser securityUser, BigInteger groupId) {
        User user = userUtil.getUser(securityUser);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        List<UserGroup> userGroups =  user.getGroups();
        Optional<UserGroup> selectedGroup =  userGroups.stream().filter(g -> g.getGroup().equals(group)).findAny();
        if(selectedGroup.isPresent()) {
            if(selectedGroup.get().getClientStatus() == ClientStatus.WITHDRAWAL) {
                selectedGroup.get().updateStatus(ClientStatus.BELONG);
                return;
            }
            throw new ApiException(ExceptionEnum.ALREADY_EXISTING_GROUP);
        }

        UserGroup userCorporation = UserGroup.builder()
                .clientStatus(ClientStatus.BELONG)
                .user(user)
                .group(group)
                .build();
        userGroupRepository.save(userCorporation);
    }
    @Override
    public List<MembershipSubscriptionTypeDto> getMembershipSubscriptionInfo() {
        List<MembershipSubscriptionTypeDto> membershipSubscriptionTypeDtos = new ArrayList<>();

        MembershipSubscriptionTypeDto monthSubscription = MembershipSubscriptionTypeDto.builder()
                .membershipSubscriptionType(MembershipSubscriptionType.MONTH)
                .build();

        MembershipSubscriptionTypeDto yearSubscription = MembershipSubscriptionTypeDto.builder()
                .membershipSubscriptionType(MembershipSubscriptionType.YEAR)
                .build();

        membershipSubscriptionTypeDtos.add(monthSubscription);
        membershipSubscriptionTypeDtos.add(yearSubscription);

        return membershipSubscriptionTypeDtos;
    }

    @Override
    @Transactional
    public List<SpotListResponseDto> getClients(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        // 그룹/스팟 정보 가져오기r
        List<UserGroup> userGroups = user.getGroups();
        // 그룹/스팟 리스트를 담아줄 Dto 생성하기
        List<SpotListResponseDto> spotListResponseDtoList = new ArrayList<>();
        // 그룹 추가
        for (UserGroup userGroup : userGroups) {
            // 현재 활성화된 유저 그룹일 경우만 가져오기
            if(userGroup.getClientStatus() == ClientStatus.BELONG) {
                Group group = userGroup.getGroup();
                spotListResponseDtoList.add(groupResponseMapper.toDto(group));
            }
        }
        return spotListResponseDtoList;
    }

    @Override
    public void saveCreditCard(SecurityUser securityUser, SaveCreditCardRequestDto saveCreditCardRequestDto) {
        User user = userUtil.getUser(securityUser);

        /*영문 대소문자, 숫자, 특수문자 -, _, =, ., @로 이루어진 최소 2자 이상 최대 300자 이하의 문자열*/
        //ASCII코드상 숫자 48~57 / 영대문자 65~90 / 영소문자 97~122
        String customerKey = TossUtil.CreateCustomerKey();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/billing/authorizations/card"))
                .header("Authorization", "Basic dGVzdF9za196WExrS0V5cE5BcldtbzUwblgzbG1lYXhZRzVSOg==")
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"cardNumber\":\""+saveCreditCardRequestDto.getCardNumber()+"\",\"cardExpirationYear\":\""+saveCreditCardRequestDto.getExpirationYear()+"\",\"cardExpirationMonth\":\""+saveCreditCardRequestDto.getExpirationMonth()+"\",\"cardPassword\":\""+ saveCreditCardRequestDto.getCardPassword()+"\",\"customerIdentityNumber\":\""+saveCreditCardRequestDto.getIdentityNumber()+"\",\"customerKey\":\""+customerKey+"\"}"))
                .build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.body() + " 바디바디");
        String[] strings = response.body().split(",");
        /*
        빌링키  / 카드번호 / 카드회사 / 카드타입 / 오너타입
        * */
        String billingKey = null;
        String cardNumber = null;
        String cardCompany = null;
        String cardType = null;
        String ownerType = null;

        for (String body : strings){
            if (body.contains("billingKey")){
                String[] billingKeyTemp = body.split(":");
                billingKey = billingKeyTemp[1].substring(1,billingKeyTemp[1].length()-1);
                System.out.println("billingKey : " + billingKey);
            }
            if (body.contains("cardNumber")){
                String[] cardNumbers = body.split(":");
                cardNumber = cardNumbers[1].substring(1,cardNumbers[1].length()-1);
                System.out.println("cardNumber : " + cardNumber);
            }
            if(body.contains("cardCompany")){
                String[] cardCompanys = body.split(":");
                cardCompany = cardCompanys[1].substring(1,cardCompanys[1].length()-1);
                System.out.println("cardCompany : " + cardCompany);
            }
            if(body.contains("cardType")){
                String[] cardTypes = body.split(":");
                cardType = cardTypes[1].substring(1,cardTypes[1].length()-1);
                System.out.println("cardType : " + cardType);
            }
            if(body.contains("ownerType")){
                String[] ownerTypes = body.split(":");
                ownerType = ownerTypes[1].substring(1,ownerTypes[1].length()-3);
                System.out.println("ownerType : " + ownerType);
            }
        }
        //CreditCard 저장을 위한 DTO 매핑
        CreditCardInfo creditCardInfo = creditCardInfoSaveMapper.toSaveEntity(cardNumber, user.getId(), ownerType, cardType, customerKey, billingKey, cardCompany);
//        CreditCardSaveDto creditCardSaveDto = creditCardInfoSaveMapper.toSaveDto(cardNumber, user.getId(), ownerType, cardType, customerKey, billingKey, cardCompany);


//        System.out.println(creditCardSaveDto.getUserId().toString() + " userId");®®
//        System.out.println(cardNumber + " cardNumber");
//        System.out.println(ownerType + " ownerType");
//        System.out.println(cardType + " cardType");
//        System.out.println(customerKey + " customerKey");
//        System.out.println(billingKey + " billingKey");
//        System.out.println(cardCompany + " cardCompany");

        creditCardInfoRepository.save(creditCardInfo);
    }
}
