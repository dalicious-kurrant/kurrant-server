package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.oauth.SnsLoginResponseDto;
import co.dalicious.client.oauth.SnsLoginService;
import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.repository.ApartmentRepository;
import co.dalicious.domain.client.repository.CorporationRepository;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.repository.ProviderEmailRepository;
import co.dalicious.domain.user.repository.UserApartmentRepository;
import co.dalicious.domain.user.repository.UserCorporationRepository;
import co.dalicious.domain.user.util.MembershipUtil;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.RequiredAuth;
import co.kurrant.app.public_api.dto.user.*;
import co.kurrant.app.public_api.service.impl.mapper.user.UserHomeInfoMapper;
import co.kurrant.app.public_api.service.impl.mapper.user.UserPersonalInfoMapper;
import co.kurrant.app.public_api.util.VerifyUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import co.kurrant.app.public_api.service.CommonService;

import co.kurrant.app.public_api.service.UserService;
import co.kurrant.app.public_api.validator.UserValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final CommonService commonService;
    private final SnsLoginService snsLoginService;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final VerifyUtil verifyUtil;
    private final MembershipUtil membershipUtil;
    private final ProviderEmailRepository providerEmailRepository;
    private final CorporationRepository corporationRepository;
    private final UserCorporationRepository userCorporationRepository;
    private final ApartmentRepository apartmentRepository;
    private final UserApartmentRepository userApartmentRepository;

    @Override
    public UserHomeResponseDto getUserHomeInfo(HttpServletRequest httpServletRequest) {
        User user = commonService.getUser(httpServletRequest);
        return UserHomeInfoMapper.INSTANCE.toDto(user);
    }

    @Override
    public void connectSnsAccount(HttpServletRequest httpServletRequest, SnsAccessToken snsAccessToken, String sns) {
        // 유저 정보 가져오기
        User user = commonService.getUser(httpServletRequest);

        // 등록된 SNS 플랫폼인지 확인
        Provider provider = UserValidator.isValidProvider(sns);

        // 현재 로그인 한 아이디가 같은 Vendor의 아이디와 연결되어있는지 체크
        List<ProviderEmail> providerEmails = providerEmailRepository.findByUser(user);
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
        Optional<ProviderEmail> providerEmail = providerEmailRepository.findAllByProviderAndEmail(provider, email);

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
    public void disconnectSnsAccount(HttpServletRequest httpServletRequest, String sns) {
        // 유저 정보 가져오기
        User user = commonService.getUser(httpServletRequest);

        // 등록된 SNS 플랫폼인지 확인
        Provider provider = UserValidator.isValidProvider(sns);

        // 현재 로그인 한 아이디가 이메일/비밀번호를 설정했는지 확인
        List<ProviderEmail> providerEmails = providerEmailRepository.findByUser(user);
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
    public void changePhoneNumber(HttpServletRequest httpServletRequest, ChangePhoneRequestDto changePhoneRequestDto) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        // 로그인한 유저의 정보를 받아온다.
        User user = commonService.getUser(httpServletRequest);
        // 입력한 휴대폰 번호가 기존에 등록된 번호인지 확인한다.
        userValidator.isPhoneValid(changePhoneRequestDto.getPhone());
        // 인증번호가 일치하는지 확인한다.
        verifyUtil.verifyCertificationNumber(changePhoneRequestDto.getKey(), RequiredAuth.MYPAGE_CHANGE_PHONE_NUMBER);
        // 비밀번호를 업데이트 한다.
        user.changePhoneNumber(changePhoneRequestDto.getPhone());
    }

    @Override
    @Transactional
    public void changePassword(HttpServletRequest httpServletRequest, ChangePasswordDto changePasswordRequestDto) {
        // 로그인한 유저의 정보를 받아온다.
        User user = commonService.getUser(httpServletRequest);
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
    public void setEmailAndPassword(HttpServletRequest httpServletRequest, SetEmailAndPasswordDto setEmailAndPasswordDto) {
        // 로그인한 유저의 정보를 받아온다.
        User user = commonService.getUser(httpServletRequest);
        String email = setEmailAndPasswordDto.getEmail();

        // 기존에 존재하는 이메일인지 확인
        userValidator.isEmailValid(Provider.GENERAL, email);

        // 다른 계정에서 주요 이메일(아이디)로 사용하는 이메일인지 확인
        userValidator.isExistingMainEmail(email);

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
    public MarketingAlarmResponseDto getAlarmSetting(HttpServletRequest httpServletRequest) {
        // 유저 정보 가져오기
        User user = commonService.getUser(httpServletRequest);
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
    public MarketingAlarmResponseDto changeAlarmSetting(HttpServletRequest httpServletRequest, MarketingAlarmRequestDto marketingAlarmDto) {
        // 유저 정보 가져오기
        User user = commonService.getUser(httpServletRequest);
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
    public UserPersonalInfoDto getPersonalUserInfo(HttpServletRequest httpServletRequest) {
        // 유저 정보 가져오기
        User user = commonService.getUser(httpServletRequest);

        // 일반 로그인 정보를 가지고 있는 유저인지 검사
        List<ProviderEmail> providerEmails = user.getProviderEmails();
        UserPersonalInfoDto userPersonalInfoDto = UserPersonalInfoMapper.INSTANCE.toDto(user);
        Boolean hasGeneralProvider = providerEmails.stream()
                .anyMatch(e -> e.getProvider().equals(Provider.GENERAL));

        // 일반 로그인을 가지고 있는 유저인지 아닌지 상태 업데이트.
        userPersonalInfoDto.hasGeneralProvider(hasGeneralProvider);
        return userPersonalInfoDto;
    }

    @Override
    public UserInfoDto getUserInfo(HttpServletRequest httpServletRequest) {
        User user = commonService.getUser(httpServletRequest);
        Integer membershipPeriod = membershipUtil.getUserPeriodOfUsingMembership(user);
//        Integer dailyMealCount =

        return UserInfoDto.builder()
                .user(user)
                .membershipPeriod(membershipPeriod)
//                .dailyMealCount()
                .build();
    }

    @Override
    // TODO: 추후 백오피스 구현시 삭제
    public void settingCorporation(HttpServletRequest httpServletRequest, BigInteger corporationId) {
        User user = commonService.getUser(httpServletRequest);
        Corporation corporation = corporationRepository.findById(corporationId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        UserCorporation userCorporation = UserCorporation.builder()
                .user(user)
                .corporation(corporation)
                .build();
        userCorporationRepository.save(userCorporation);
    }

    @Override
    // TODO: 추후 백오피스 구현시 삭제
    public void settingApartment(HttpServletRequest httpServletRequest, BigInteger apartmentId) {
        User user = commonService.getUser(httpServletRequest);
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        UserApartment userApartment = UserApartment.builder()
                .user(user)
                .apartment(apartment)
                .build();
        userApartmentRepository.save(userApartment);
    }
}
