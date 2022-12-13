package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.order.entity.OrderDetail;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.repository.FoodRepository;
import co.dalicious.domain.order.repository.OrderDetailRepository;
import co.dalicious.domain.user.entity.Provider;
import co.dalicious.domain.user.entity.ProviderEmail;
import co.dalicious.domain.user.repository.ProviderEmailRepository;
import co.kurrant.app.public_api.dto.user.*;
import co.kurrant.app.public_api.service.impl.mapper.UserInfoMapper;
import exception.ApiException;
import exception.ExceptionEnum;
import co.dalicious.client.external.sms.NaverSmsServiceImpl;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.service.CommonService;
import co.dalicious.domain.user.dto.OrderDetailDto;
import co.dalicious.domain.user.dto.OrderItemDto;

import co.dalicious.domain.user.repository.UserRepository;
import co.kurrant.app.public_api.service.UserService;
import co.kurrant.app.public_api.validator.UserValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final CommonService commonService;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final NaverSmsServiceImpl smsService;
    private final ProviderEmailRepository providerEmailRepository;
    private final UserRepository userRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final FoodRepository foodRepository;

    @Override
    public void editSnsAccount(HttpServletRequest httpServletRequest, String sns) {
        // 등록된 SNS 플랫폼인지 확인
        Provider provider = null;
        boolean isContainedSns = Arrays.toString(Provider.values()).contains(sns.toUpperCase());
        if(!isContainedSns) {
            throw new ApiException(ExceptionEnum.SNS_PLATFORM_NOT_FOUND);
        } else {
            provider = Provider.valueOf(sns);
        }

        // 연결된 SNS 계정 확인
        UserInfoDto userInfoDto = getUserInfo(httpServletRequest);
        List<ProviderEmail> providerEmails = userInfoDto.getProviderEmails();
        ProviderEmail providerSelectedEmail = null;
        boolean isConnectedSns = false;
        boolean hasGeneralProvider = false;
        List<Provider> providers = new ArrayList<>();

        for(ProviderEmail providerEmail : providerEmails) {
            // 연결이 되어있다면 연결된 계정 정보 가져오기
            if(providerEmail.getProvider().equals(provider)) {
                providerSelectedEmail = providerEmail;
                isConnectedSns = true;
            }
            providers.add(providerEmail.getProvider());
        }
        hasGeneralProvider = providers.contains(Provider.GENERAL);

        // 연결이 되어있지만 일반로그인 정보가 없는 경우
        if(isConnectedSns && !hasGeneralProvider) {
            throw new ApiException(ExceptionEnum.GENERAL_PROVIDER_NOT_FOUND);
        }
        // 연결이 되어있고 일반 로그인 정보가 있는 경우
        else if(isConnectedSns && hasGeneralProvider) {
            disconnectSnsAccount(providerSelectedEmail, provider);
        }
        // 연결이 되어있지 않은 경우, 소셜로그인 연결 진행
        else {
            connectSnsAccount(userInfoDto, provider);
        }

    }

    @Override
    public void connectSnsAccount(UserInfoDto userInfoDto, Provider provider) {
        // 소셜 로그인 연결
    }

    @Override
    public void disconnectSnsAccount(ProviderEmail providerSelectedEmail, Provider provider) {
        // 소셜 로그인 일치하는지 확인
        if(!providerSelectedEmail.getProvider().equals(provider)) {
            throw new ApiException(ExceptionEnum.USER_NOT_FOUND);
        }
        // 소셜로그인 연결 계정 삭제
        providerEmailRepository.deleteById(providerSelectedEmail.getId());
    }

    @Override
    @Transactional
    public void changePhoneNumber(HttpServletRequest httpServletRequest, ChangePhoneRequestDto changePhoneRequestDto) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        // 로그인한 유저의 정보를 받아온다.
        User user = commonService.getUser(httpServletRequest);
        // 입력한 휴대폰 번호가 기존에 등록된 번호인지 확인한다.
        userValidator.isPhoneValid(changePhoneRequestDto.getPhone());
        // 인증번호가 일치하는지 확인한다.
        smsService.verifySms(changePhoneRequestDto.getKey());
        // 비밀번호를 업데이트 한다.
        user.changePhoneNumber(changePhoneRequestDto.getPhone());
    }

    @Override
    @Transactional
    public void changePassword(HttpServletRequest httpServletRequest, ChangePasswordRequestDto changePasswordRequestDto) {
        // 로그인한 유저의 정보를 받아온다.
        User user = commonService.getUser(httpServletRequest);
        // 현재 비밀번호가 일치하는지 확인
        if(!passwordEncoder.matches(changePasswordRequestDto.getCurrantPassword(), user.getPassword())) {
            throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
        };
        // 새로 등록한 번호 두개가 일치하는 지 확인
        if(!changePasswordRequestDto.getNewPassword().equals(changePasswordRequestDto.getNewPasswordCheck())) {
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

        // 기존에 존재하는 이메일인지 확인
        userValidator.isEmailValid(Provider.GENERAL, setEmailAndPasswordDto.getEmail());

        // 비밀번호 일치 확인
        String password = setEmailAndPasswordDto.getPassword();
        String passwordCheck = setEmailAndPasswordDto.getPasswordCheck();
        userValidator.isPasswordMatched(password, passwordCheck);

        // 이메일/비밀번호 업데이트
        String email = setEmailAndPasswordDto.getEmail();
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
    public ChangeMarketingDto changeAlarmSetting(HttpServletRequest httpServletRequest, Boolean isMarketingInfoAgree,
                                                 Boolean isMarketingAlarmAgree, Boolean isOrderAlarmAgree) {
        // 유저 정보 가져오기
        User user = commonService.getUser(httpServletRequest);
        Boolean currantMarketingInfoAgree = user.getMarketingAgree();
        Boolean currantMarketingAlarmAgree = user.getMarketingAlarm();
        Boolean currantOrderAlarmAgree = user.getOrderAlarm();

        // 현재 시간 가져오기
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        // 마케팅 정보 수신 동의/철회
        if(isMarketingInfoAgree != null) {
            user.changeMarketingAgreement(now, isMarketingInfoAgree, isMarketingInfoAgree, isMarketingInfoAgree);
        }
        // 혜택 및 소식 알림 동의/철회
        if(isMarketingAlarmAgree != null) {
            // 주문 알림이 활성화 되어 있을 경우
            if(currantOrderAlarmAgree) {
                user.setMarketingAlarm(isMarketingAlarmAgree);
            }
            // 주문 알림이 활성화 되어 있지 않을 경우
            else {
                user.changeMarketingAgreement(now, !currantMarketingInfoAgree, isMarketingAlarmAgree, currantOrderAlarmAgree);
            }
        }
        // 주문 알림 동의/철회
        if(isOrderAlarmAgree != null) {
            // 혜택 및 소식 알림이 활성화 되어 있을 경우
            if(currantMarketingAlarmAgree) {
                user.setOrderAlarm(isOrderAlarmAgree);
            }
            // 혜택 및 소식 알림이 활성화 되어 있지 않을 경우
            else {
                user.changeMarketingAgreement(now, !currantMarketingInfoAgree, currantMarketingAlarmAgree, isOrderAlarmAgree);
            }
        }
        return ChangeMarketingDto.builder()
                .marketingAgree(user.getMarketingAgree())
                .marketingAgreedDateTime(user.getMarketingAgreedDateTime())
                .marketingAlarm(user.getMarketingAlarm())
                .orderAlarm(user.getOrderAlarm())
                .build();
    }

    @Override
    @Transactional
    public UserInfoDto getUserInfo(HttpServletRequest httpServletRequest) {
        User user = commonService.getUser(httpServletRequest);
        return UserInfoMapper.INSTANCE.toDto(user);
    }
    @Override
    public User findAll() {
        User user = userRepository.findAll().get(0);
        return user;
    }

    @Override
    public OrderDetailDto findOrderByServiceDate(Date startDate, Date endDate){
        //JWT로 아이디 받기
        OrderDetailDto orderDetailDto = new OrderDetailDto();

        List<OrderItemDto> orderItemDtoList = new ArrayList<>();

        System.out.println(startDate +" startDate, " + endDate +" endDate");
        List<OrderDetail> byServiceDateBetween = orderDetailRepository.findByServiceDateBetween(startDate, endDate);

        byServiceDateBetween.forEach( x -> {
            orderDetailDto.setId(x.getId());
            orderDetailDto.setServiceDate(x.getServiceDate());

            Food food = foodRepository.findById(x.getFoodId());

           OrderItemDto orderItemDto = OrderItemDto.builder()
                   .name(food.getName())
                   .diningType(x.getEDiningType())
                   .img(food.getImg())
                   .count(x.getCount())
                   .build();

           orderItemDtoList.add(orderItemDto);
           orderDetailDto.setOrderItemDtoList(orderItemDtoList);
        });


        return orderDetailDto;
    }

}
