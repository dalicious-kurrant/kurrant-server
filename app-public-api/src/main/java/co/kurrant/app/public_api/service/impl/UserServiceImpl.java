package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.oauth.SnsLoginResponseDto;
import co.dalicious.client.oauth.SnsLoginService;
import co.dalicious.domain.order.dto.OrderCartDto;
import co.dalicious.domain.order.entity.OrderCart;
import co.dalicious.domain.order.entity.OrderCartItem;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.repository.FoodRepository;
import co.dalicious.domain.order.repository.OrderCartItemRepository;
import co.dalicious.domain.order.repository.OrderCartRepository;
import co.dalicious.domain.order.repository.OrderItemRepository;
import co.dalicious.domain.user.entity.Provider;
import co.dalicious.domain.user.entity.ProviderEmail;
import co.dalicious.domain.user.repository.ProviderEmailRepository;
import co.dalicious.system.util.RequiredAuth;
import co.kurrant.app.public_api.dto.user.*;
import co.kurrant.app.public_api.service.impl.mapper.UserHomeInfoMapper;
import co.kurrant.app.public_api.service.impl.mapper.UserInfoMapper;
import co.kurrant.app.public_api.util.VerifyUtil;
import exception.ApiException;
import exception.ExceptionEnum;
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
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDate;
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
    private final ProviderEmailRepository providerEmailRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderCartItemRepository orderCartItemRepository;
    private final FoodRepository foodRepository;
    private final OrderCartRepository orderCartRepository;

    @Override
    public UserHomeResponseDto getUserHomeInfo(HttpServletRequest httpServletRequest) {
        User user = commonService.getUser(httpServletRequest);
        return UserHomeInfoMapper.INSTANCE.toDto(user);
    }
    @Override
    @Transactional
    public void connectSnsAccount(HttpServletRequest httpServletRequest, SnsAccessToken snsAccessToken, String sns) {
        // 유저 정보 가져오기
        User user = commonService.getUser(httpServletRequest);

        // 등록된 SNS 플랫폼인지 확인
        Provider provider = UserValidator.isValidProvider(sns);

        // 현재 로그인 한 아이디가 같은 Vendor의 아이디와 연결되어있는지 체크
        List<ProviderEmail> providerEmails = user.getProviderEmails();
        for (ProviderEmail providerEmail : providerEmails) {
            if (providerEmail.getProvider().equals(provider)) {
                throw new ApiException(ExceptionEnum.CANNOT_CONNECT_SNS);
            }
        }

        // Vendor 로그인 시도
        SnsLoginResponseDto snsLoginResponseDto = switch (provider) {
            case NAVER -> snsLoginService.getNaverLoginUserInfo(snsAccessToken.getSnsAccessToken());
            case KAKAO -> snsLoginService.getKakaoLoginUserInfo(snsAccessToken.getSnsAccessToken());
            case GOOGLE -> snsLoginService.getGoogleLoginUserInfo(snsAccessToken.getSnsAccessToken());
            case APPLE -> snsLoginService.getAppleLoginUserInfo(snsAccessToken.getSnsAccessToken());
            case FACEBOOK -> snsLoginService.getFacebookLoginUserInfo(snsAccessToken.getSnsAccessToken());
            default -> null;
        };

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
    @Transactional
    public void disconnectSnsAccount(HttpServletRequest httpServletRequest, String sns) {
        // 유저 정보 가져오기
        UserInfoDto userInfoDto = getUserInfo(httpServletRequest);

        // 등록된 SNS 플랫폼인지 확인
        Provider provider = UserValidator.isValidProvider(sns);

        // 현재 로그인 한 아이디가 이메일/비밀번호를 설정했는지 확인
        List<ProviderEmail> providerEmails = userInfoDto.getProviderEmails();
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
    public void changePassword(HttpServletRequest httpServletRequest, ChangePasswordRequestDto changePasswordRequestDto) {
        // 로그인한 유저의 정보를 받아온다.
        User user = commonService.getUser(httpServletRequest);
        // 현재 비밀번호가 일치하는지 확인
        if (!passwordEncoder.matches(changePasswordRequestDto.getCurrantPassword(), user.getPassword())) {
            throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
        }
        ;
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
        if (isMarketingInfoAgree != null) {
            user.changeMarketingAgreement(now, isMarketingInfoAgree, isMarketingInfoAgree, isMarketingInfoAgree);
        }
        // 혜택 및 소식 알림 동의/철회
        if (isMarketingAlarmAgree != null) {
            // 주문 알림이 활성화 되어 있을 경우
            if (currantOrderAlarmAgree) {
                user.setMarketingAlarm(isMarketingAlarmAgree);
            }
            // 주문 알림이 활성화 되어 있지 않을 경우
            else {
                user.changeMarketingAgreement(now, !currantMarketingInfoAgree, isMarketingAlarmAgree, currantOrderAlarmAgree);
            }
        }
        // 주문 알림 동의/철회
        if (isOrderAlarmAgree != null) {
            // 혜택 및 소식 알림이 활성화 되어 있을 경우
            if (currantMarketingAlarmAgree) {
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
    public OrderDetailDto findOrderByServiceDate(Date startDate, Date endDate) {
        //JWT로 아이디 받기
        OrderDetailDto orderDetailDto = new OrderDetailDto();

        List<OrderItemDto> orderItemDtoList = new ArrayList<>();

        System.out.println(startDate + " startDate, " + endDate + " endDate");
        List<OrderItem> byServiceDateBetween = orderItemRepository.findByServiceDateBetween(startDate, endDate);

        byServiceDateBetween.forEach(x -> {
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

    @Override
    @Transactional
    public void saveOrderCart(HttpServletRequest httpServletRequest, OrderCartDto orderCartDto) {
        //User user = commonService.getUser(httpServletRequest);
        //BigInteger id = user.getId();

        BigInteger id = BigInteger.valueOf(1); // 임시로 적용

        OrderCart orderCart1 = OrderCart.builder()
                .userId(id)
                .build();

        orderCartRepository.save(orderCart1);
        OrderCart orderCartId = orderCartRepository.findByUserId(id);

        //Food DB 생성용
        Food createFood = Food.builder()
                .price(10000)
                .name("무야호장 팥붕어빵")
                .description("무야호장의 심혈을 기울인 팥붕")
                .build();
        Food createFood1 = Food.builder()
                .price(10000)
                .name("무야호장 슈크림붕어빵")
                .description("무야호장의 심혈을 기울인 슈붕")
                .build();
        Food createFood2 = Food.builder()
                .price(10000)
                .name("무야호장 피자붕어빵")
                .description("무야호장의 심혈을 기울인 피붕")
                .build();

        foodRepository.save(createFood);
        foodRepository.save(createFood1);
        foodRepository.save(createFood2);

        Food food = Food.builder()
                .id(orderCartDto.getFoodId())
                .build();

        OrderCartItem orderCartItem = OrderCartItem.builder()
                .created(LocalDate.now())
                .serviceDate(orderCartDto.getServiceDate())
                .diningType(orderCartDto.getDiningType())
                .count(orderCartDto.getCount())
                .orderCart(orderCartId)
                .foodId(food)
                .build();
        orderCartItemRepository.save(orderCartItem);

    }
}
