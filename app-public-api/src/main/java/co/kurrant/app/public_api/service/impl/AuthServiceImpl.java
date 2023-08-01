package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.core.dto.request.LoginTokenDto;
import co.dalicious.client.core.entity.RefreshToken;
import co.dalicious.client.core.repository.RefreshTokenRepository;
import co.dalicious.data.redis.entity.BlackListTokenHash;
import co.dalicious.data.redis.entity.TempRefreshTokenHash;
import co.dalicious.data.redis.repository.BlackListTokenRepository;
import co.dalicious.client.external.sms.SmsService;
import co.dalicious.client.external.sms.dto.SmsMessageRequestDto;
import co.dalicious.client.oauth.SnsLoginResponseDto;
import co.dalicious.client.oauth.SnsLoginService;
import co.dalicious.data.redis.entity.CertificationHash;
import co.dalicious.data.redis.repository.CertificationHashRepository;
import co.dalicious.data.redis.repository.TempRefreshTokenRepository;
import co.dalicious.domain.client.entity.Employee;
import co.dalicious.domain.client.repository.EmployeeRepository;
import co.dalicious.domain.user.dto.ProviderEmailDto;
import co.dalicious.domain.user.entity.enums.Provider;
import co.dalicious.domain.user.entity.enums.Role;
import co.dalicious.domain.user.entity.enums.SpotStatus;
import co.dalicious.domain.user.entity.enums.UserStatus;
import co.dalicious.domain.user.repository.QUserRepository;
import co.dalicious.domain.user.util.ClientUtil;
import co.dalicious.domain.user.validator.UserValidator;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.GenerateRandomNumber;
import co.dalicious.system.enums.RequiredAuth;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.AuthService;
import co.kurrant.app.public_api.dto.user.*;
import co.kurrant.app.public_api.mapper.user.UserMapper;
import co.kurrant.app.public_api.util.UserUtil;
import co.kurrant.app.public_api.util.VerifyUtil;
import exception.ApiException;
import exception.CustomException;
import exception.ExceptionEnum;
import co.dalicious.client.core.filter.provider.JwtTokenProvider;
import co.dalicious.client.external.mail.EmailService;
import co.dalicious.client.external.mail.MailMessageDto;
import co.dalicious.client.external.sms.dto.SmsResponseDto;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.repository.ProviderEmailRepository;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.domain.user.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private final TempRefreshTokenRepository tempRefreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ProviderEmailRepository providerEmailRepository;
    private final ClientUtil clientUtil;
    private final EmailService emailService;
    private final SmsService smsService;
    private final VerifyUtil verifyUtil;
    private final CertificationHashRepository certificationHashRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlackListTokenRepository blackListTokenRepository;
    private final SnsLoginService snsLoginService;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final UserUtil userUtil;
    private final EmployeeRepository employeeRepository;
    private final QUserRepository qUserRepository;
    private final ConcurrentHashMap<String, Lock> userLocks = new ConcurrentHashMap<>();

    // 이메일 인증
    @Override
    public void mailConfirm(Authentication authentication, MailMessageDto mailMessageDto, String type) throws Exception {
        // 인증을 요청하는 위치 파악하기
        for (String email : mailMessageDto.getReceivers()) {
            UserValidator.isValidEmail(email);
        }
        RequiredAuth requiredAuth = RequiredAuth.ofId(type);
        switch (requiredAuth) {
            case SIGNUP -> {
                // 기존에 가입된 사용자인지 확인
                String mail = mailMessageDto.getReceivers().get(0);
                userValidator.isEmailValid(mail);
            }
            case FIND_PASSWORD -> {
                // 존재하는 유저인지 확인
                User user = userRepository.findOneByEmail(mailMessageDto.getReceivers().get(0)).orElseThrow(() -> new ApiException(ExceptionEnum.USER_NOT_FOUND));
            }
            case PAYMENT_PASSWORD_CREATE_APPLE -> {
                SecurityUser securityUser = UserUtil.securityUser(authentication);
                User user = userUtil.getUser(securityUser);
                userValidator.isEmailValid(user, mailMessageDto.getReceivers().get(0));
                Optional<User> userOptional = userRepository.findOneByEmail(mailMessageDto.getReceivers().get(0));

                if (userOptional.isPresent()) {
                    throw new ApiException(ExceptionEnum.EXCEL_EMAIL_DUPLICATION);
                }
            }
        }

        // 이메일 폼 작성
        String key = GenerateRandomNumber.create6DigitKey(); // 4자리 랜덤 인증번호 생성
        String receiver = mailMessageDto.getReceivers().get(0);

        log.info("인증 번호 : " + key);
        log.info("보내는 대상 : " + receiver);


        String subject = ("[커런트] 회원가입 인증 코드: "); //메일 제목

        if (requiredAuth.equals(RequiredAuth.PAYMENT_PASSWORD_CREATE) || requiredAuth.equals(RequiredAuth.PAYMENT_PASSWORD_CREATE_APPLE)) {
            subject = ("[커런트] 결제 비밀번호 등록 인증 코드: ");
        }

        if (requiredAuth == RequiredAuth.PAYMENT_PASSWORD_CHECK || type.equals("6")) {
            subject = ("[커런트] 결제 비밀번호 확인 인증 코드: ");
        }

        // 메일 내용 메일의 subtype을 html로 지정하여 html문법 사용 가능
        String content = "";
        content += """
                <!DOCTYPE html>
                <html>
                <head></head>
                <body>
                <header>
                    <div style="width:100%; max-width: 481px;">
                        <img style="margin-bottom: 32px;" src="https://asset.kurrant.co/img/common/logo.png" />
                    </div>
                </header>
                <div>
                    <div style="width:100%; justify-content: center;  align-items: center; display: flex; flex-direction: column;">
                                
                        <div style="padding: 50px; padding-right: 104px; border:1px solid #E4E3E7; border-radius: 14px; max-width: 481px; min-height: 481px; width: 100%; box-sizing: border-box;">
                            <div style="font-size: 26px; font-weight: 600; font-family: ‘Franklin Gothic Medium’, ‘Arial Narrow’, Arial, sans-serif; line-height: 35px; margin-bottom: 32px; color: #343337;">커런트에서 요청하신 인증번호를 발송해 드립니다.</div>
                            <div style="font-size: 14px; line-height: 22px; font-weight: 400; color: #343337;">아래 인증번호 6자리를 인증번호 입력창에 입력해주세요</div>
                            <div style="color: #343337; font-size: 22px; font-weight: 600; line-height: 30px;">
                """;
        content += key;
        content += "</div>\n" + "</div>\n" + "</div>";
        content += """
                             <footer  style="justify-content: center;  align-items: center; display: flex; flex-direction: column; margin-top: 125px;">
                                <div style="font-size: 12px; line-height: 16px; letter-spacing: -0.5px; font-weight: 400;">서울특별시 강남구 테헤란로51길 21 3층</div>
                                <div style="font-size: 12px; line-height: 16px; letter-spacing: -0.5px; font-weight: 400;">달리셔스주식회사</div>
                            </footer>
                        </div>
                </body>
                </html>
                """;

        // 인증번호 발송
        emailService.sendSimpleMessage(mailMessageDto.getReceivers(), subject, content);

        // Redis에 인증번호 저장
        CertificationHash certificationHash = CertificationHash.builder().id(null).type(type).to(receiver).certificationNumber(key).build();
        certificationHashRepository.save(certificationHash);
    }

    // Sms 인증
    @Override
    public String sendSms(SmsMessageRequestDto smsMessageRequestDto, String type) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        // 인증을 요청하는 위치 파악하기
        RequiredAuth requiredAuth = RequiredAuth.ofId(type);
        switch (requiredAuth) {
            case SIGNUP ->
                // 기존에 등록된 휴대폰 번호인지 확인
                    userValidator.isPhoneValid(smsMessageRequestDto.getTo());
            case FIND_ID, FIND_PASSWORD -> {
                // 유저가 존재하는지 확인
                User user = qUserRepository.findOneByPhone(smsMessageRequestDto.getTo()).orElseThrow(() -> new ApiException(ExceptionEnum.USER_NOT_FOUND));
            }
        }

        // 인증번호 발송
        String key = GenerateRandomNumber.create6DigitKey();
        String content = "[커런트] 인증번호 [" + key + "]를 입력해주세요";
        SmsResponseDto smsResponseDto = smsService.sendSms(smsMessageRequestDto, content);

        log.info("인증 번호 : " + key);
        log.info("보내는 대상 : " + smsMessageRequestDto.getTo());

        // Redis에 인증번호 저장
        CertificationHash certificationHash = CertificationHash.builder().id(null).type(type).to(smsMessageRequestDto.getTo()).certificationNumber(key).build();
        certificationHashRepository.save(certificationHash);

        if (requiredAuth == RequiredAuth.SIGNUP) {
            return UserUtil.generateRandomNickName();
        }
        return null;
    }

    // 회원가입
    @Override
    public User signUp(SignUpRequestDto signUpRequestDto) {

        //가입가능 리스트에 있는 유저검색
        List<Employee> employeeList = employeeRepository.findAllByEmail(signUpRequestDto.getEmail());

        // 기존에 가입된 사용자인지 확인
        String mail = signUpRequestDto.getEmail();
        User user = userValidator.getExistingUser(mail);

        // 기존에 일반 로그인으로 가입한 사용자인지 확인
        Provider provider = Provider.GENERAL;
        userValidator.isEmailValid(provider, mail);

        // 비밀번호 일치/조건 체크
        String password = signUpRequestDto.getPassword();
        UserValidator.isPasswordMatched(password, signUpRequestDto.getPasswordCheck());
        userValidator.isValidPassword(password);

        // 인증을 진행한 유저인지 체크
        if (employeeList.isEmpty()) {
            verifyUtil.isAuthenticated(signUpRequestDto.getEmail(), RequiredAuth.SIGNUP);
            verifyUtil.isAuthenticated(signUpRequestDto.getPhone(), RequiredAuth.SIGNUP);
        }
        // Hashed Password 생성
        String hashedPassword = passwordEncoder.encode(password);

        // 기존에 회원가입을 한 이력이 없는 유저라면 -> 유저 생성
        if (user == null) {
            UserDto userDto = UserDto.builder()
                    .email(signUpRequestDto.getEmail().trim())
                    .phone(signUpRequestDto.getPhone())
                    .password(hashedPassword)
                    .name(signUpRequestDto.getName())
                    .nickname(signUpRequestDto.getNickname())
                    .role(Role.USER)
                    .build();

            // Corporation가 null로 대입되는 오류 발생 -> nullable = true 설정
            user = userMapper.toEntity(userDto);

            // User 저장
            if (user.isAdmin() && userValidator.adminExists()) {
                throw new ApiException(ExceptionEnum.ADMIN_USER_SHOULD_BE_UNIQUE);
            }
            user = userRepository.save(user);

            // 등록된 사원인지 검증
            clientUtil.isRegisteredUser(user);
        }

        ProviderEmail providerEmail = ProviderEmail.builder().email(mail).provider(Provider.GENERAL).user(user).build();
        providerEmailRepository.save(providerEmail);

        //가입가능리스트에서 삭제
        if (employeeList.size() >= 1) {
            employeeRepository.deleteAllByEmail(employeeList.get(0).getEmail());
        }

        return user;
    }

    // 유저 인증 완료 후 토큰 발급
    public LoginResponseDto getLoginAccessToken(User user, SpotStatus spotStatus) {
        // 토큰에 권한 넣기
        List<String> authorities = new ArrayList<String>();
        authorities.add(user.getRole().getAuthority());
        LoginTokenDto loginResponseDto = jwtTokenProvider.createToken(user.getId().toString(), authorities);

        // 로그인 날짜 업데이트
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        user.updateRecentLoginDateTime(timestamp);

        Integer leftWithdrawDays = null;

        if (user.getUserStatus().equals(UserStatus.REQUEST_WITHDRAWAL)) {
            LocalDateTime updatedDateTime = user.getUpdatedDateTime().toLocalDateTime().plusDays(8);
            Duration duration = Duration.between(LocalDateTime.now(), updatedDateTime);
            leftWithdrawDays = (int) duration.toDays();
        }

        return LoginResponseDto.builder()
                .accessToken(loginResponseDto.getAccessToken())
                .refreshToken(loginResponseDto.getRefreshToken())
                .expiresIn(loginResponseDto.getAccessTokenExpiredIn())
                .spotStatus(spotStatus.getCode())
                .isActive(user.getUserStatus().equals(UserStatus.ACTIVE))
                .leftWithdrawDays(leftWithdrawDays)
                .hasNickname(user.hasNickname())
                .build();
    }

    // 로그인
    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto dto) {
        if (dto.getEmail() == null) {
            throw new ApiException(ExceptionEnum.NOT_INPUT_USERNAME);
        }
        if (dto.getPassword() == null) {
            throw new ApiException(ExceptionEnum.NOT_INPUT_PASSWORD);
        }

        User user = userRepository.findOneByEmail(dto.getEmail()).orElseThrow(() -> {
            throw new ApiException(ExceptionEnum.USER_NOT_FOUND);
        });

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
        }

        SpotStatus spotStatus = clientUtil.getSpotStatus(user);

        //fcm토큰 저장 로직 추가
        if (dto.getFcmToken() != null && dto.getFcmToken().equals("")){
            qUserRepository.saveFcmToken(dto.getFcmToken(), user.getId());
        }

        return getLoginAccessToken(user, spotStatus);
    }

    @Override
    @Transactional
    public LoginResponseDto lookingAround() {
        User user = userRepository.findOneByEmail("test@dalicious.co").orElseThrow(
                () -> new ApiException(ExceptionEnum.USER_NOT_FOUND)
        );
        SpotStatus spotStatus = clientUtil.getSpotStatus(user);
        return getLoginAccessToken(user, spotStatus);
    }

    @Override
    @Transactional
    public LoginResponseDto snsLoginOrJoin(String sns, SnsAccessToken snsAccessToken) {
        Provider provider = Provider.valueOf(sns);
        // Vendor 로그인 시도
        SnsLoginResponseDto snsLoginResponseDto = snsLoginService.getSnsLoginUserInfo(provider, snsAccessToken.getSnsAccessToken());

        // Response 값이 존재하지 않으면 예외 발생
        if (snsLoginResponseDto == null) {
            throw new ApiException(ExceptionEnum.CANNOT_CONNECT_SNS);
        }

        String email = snsLoginResponseDto.getEmail();
        String phone = snsLoginResponseDto.getPhone();
        String name = snsLoginResponseDto.getName();

        // 해당 아이디를 가지고 있는 유저가 존재하지는 지 확인.
        Optional<ProviderEmail> providerEmail = providerEmailRepository.findOneByProviderAndEmail(provider, email);

        // 이미 소셜로그인으로 가입한 이력이 있는 유저라면 토큰 발행
        if (providerEmail.isPresent()) {
            User user = providerEmail.orElseThrow().getUser();
            SpotStatus spotStatus = clientUtil.getSpotStatus(user);
            return getLoginAccessToken(user, spotStatus);
        }

        // 소셜 로그인으로 가입한 이력은 없지만, 소셜 로그인 이메일과 ProviderEmail에 동일한 이메일이 있는지 확인
        List<ProviderEmail> providerEmails = providerEmailRepository.findAllByEmail(email);
        // 동일한 이메일로 가입한 이력이 있는 유저를 가져온다.
        if (!providerEmails.isEmpty()) {
            User user = providerEmails.get(0).getUser();
            ProviderEmail newProviderEmail = ProviderEmail.builder().provider(provider).email(snsLoginResponseDto.getEmail()).user(user).build();
            providerEmailRepository.save(newProviderEmail);
            return getLoginAccessToken(user, clientUtil.getSpotStatus(user));
        }

        // 어떤 것도 가입되지 않은 유저라면 계정 생성
        UserDto userDto = UserDto.builder().role(Role.USER).email(email.trim()).phone(phone).name(name).build();

        User user = userRepository.save(userMapper.toEntity(userDto));

        if (user.isAdmin() && userValidator.adminExists()) {
            throw new ApiException(ExceptionEnum.ADMIN_USER_SHOULD_BE_UNIQUE);
        }

        // 등록된 사원인지 검증
        Boolean isRegisteredUser = clientUtil.isRegisteredUser(user);

        ProviderEmail newProviderEmail2 = ProviderEmail.builder().provider(provider).email(snsLoginResponseDto.getEmail()).user(user).build();
        providerEmailRepository.save(newProviderEmail2);

        if (isRegisteredUser) {
            return getLoginAccessToken(user, SpotStatus.NO_SPOT_BUT_HAS_CLIENT);

        }

        //fcm 토큰 저장
        qUserRepository.saveFcmToken(snsAccessToken.getFcmToken(), user.getId());

        return getLoginAccessToken(user, clientUtil.getSpotStatus(user));
    }

    @Override
    @Transactional
    public LoginResponseDto appleLoginOrJoin(Map<String, Object> appleLoginDto) throws JsonProcessingException {
        Provider provider = Provider.APPLE;

        // Vendor 로그인 시도
        SnsLoginResponseDto snsLoginResponseDto = snsLoginService.getAppleLoginUserInfo(appleLoginDto);

        String email = snsLoginResponseDto.getEmail();
        String name = snsLoginResponseDto.getName();

        // 해당 아이디를 가지고 있는 유저가 존재하지는 지 확인.
        Optional<ProviderEmail> providerEmail = providerEmailRepository.findOneByProviderAndEmail(provider, email);

        // 이미 소셜로그인으로 가입한 이력이 있는 유저라면 토큰 발행
        if (providerEmail.isPresent()) {
            User user = providerEmail.orElseThrow().getUser();
            SpotStatus spotStatus = clientUtil.getSpotStatus(user);
            return getLoginAccessToken(user, spotStatus);
        }

        // 소셜 로그인으로 가입한 이력은 없지만, 소셜 로그인 이메일과 ProviderEmail에 동일한 이메일이 있는지 확인
        List<ProviderEmail> providerEmails = providerEmailRepository.findAllByEmail(email);
        // 동일한 이메일로 가입한 이력이 있는 유저를 가져온다.
        if (!providerEmails.isEmpty()) {
            User user = providerEmails.get(0).getUser();
            ProviderEmail newProviderEmail = ProviderEmail.builder().provider(provider).email(snsLoginResponseDto.getEmail()).user(user).build();
            providerEmailRepository.save(newProviderEmail);
            return getLoginAccessToken(user, clientUtil.getSpotStatus(user));
        }

        // 어떤 것도 가입되지 않은 유저라면 계정 생성
        UserDto userDto = UserDto.builder().role(Role.USER).email(email.trim()).name(name).build();

        User user = userRepository.save(userMapper.toEntity(userDto));
        if (user.isAdmin() && userValidator.adminExists()) {
            throw new ApiException(ExceptionEnum.ADMIN_USER_SHOULD_BE_UNIQUE);
        }

        // 등록된 사원인지 검증
        Boolean isRegisteredUser = clientUtil.isRegisteredUser(user);

        ProviderEmail newProviderEmail2 = ProviderEmail.builder().provider(provider).email(snsLoginResponseDto.getEmail()).user(user).build();
        providerEmailRepository.save(newProviderEmail2);

        if (isRegisteredUser) {
            return getLoginAccessToken(user, SpotStatus.NO_SPOT_BUT_HAS_CLIENT);

        }
        return getLoginAccessToken(user, clientUtil.getSpotStatus(user));
    }

    @Override
    @Transactional
    public LoginTokenDto reissue(TokenDto reissueTokenDto) {
        System.out.println("Request: reissueTokenDto.refershToken = " + reissueTokenDto.getRefreshToken());
        System.out.println("Request: reissueTokenDto.accessToken = " + reissueTokenDto.getAccessToken());

        String userId;
        boolean accessTokenValid = jwtTokenProvider.validateToken(reissueTokenDto.getAccessToken());
        // 엑세스 토큰이 유효할 경우
        if (accessTokenValid) {
            userId = jwtTokenProvider.getUserPk(reissueTokenDto.getAccessToken());
        }
        // 엑세스 토큰이 유효하지 않을 경우
        else {
            Optional<RefreshToken> refreshToken = refreshTokenRepository.findOneByRefreshToken(reissueTokenDto.getRefreshToken());
            if (refreshToken.isEmpty()) {
                throw new ApiException(ExceptionEnum.REFRESH_TOKEN_ERROR);
            }
            userId = refreshToken.get().getUserId().toString();
        }
        // Get or create the lock for the userId
        Lock userLock = userLocks.computeIfAbsent(userId, id -> new ReentrantLock());

        // Lock only for the same userId
        userLock.lock();

        // 기존에 reissue 요청이 왔는지 검증
        try {
            List<TempRefreshTokenHash> tempRefreshTokenHashes = tempRefreshTokenRepository.findAllByUserId(userId);

            Optional<TempRefreshTokenHash> matchingHash = tempRefreshTokenHashes.stream()
                    .filter(hash -> hash.getOldRefreshToken().equals(reissueTokenDto.getRefreshToken()))
                    .findFirst();

            // reissue 요청이 온 적이 없는 경우
            if (matchingHash.isEmpty()) {
                List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUserId(BigInteger.valueOf(Integer.parseInt(userId)));

                // 5. 로그아웃 되어 Refresh Token이 존재하지 않는 경우 처리
                if (refreshTokens == null) {
                    throw new ApiException(ExceptionEnum.REFRESH_TOKEN_ERROR);
                }
                // 6. 잘못된 Refresh Token일 경우 예외 처리
                refreshTokens.stream().filter(v -> v.getRefreshToken().equals(reissueTokenDto.getRefreshToken()))
                        .findAny()
                        .orElseThrow(() -> new ApiException(ExceptionEnum.REFRESH_TOKEN_ERROR));

                // 7. 새로운 토큰 생성
                List<String> roles = new ArrayList<>();

                if (accessTokenValid) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(reissueTokenDto.getAccessToken());
                    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                    for (GrantedAuthority authority : authorities) {
                        roles.add(authority.getAuthority());
                    }
                } else {
                    User user = userRepository.findById(BigInteger.valueOf(Integer.parseInt(userId))).orElseThrow(
                            () -> new ApiException(ExceptionEnum.USER_NOT_FOUND)
                    );
                    roles.add(user.getRole().getAuthority());
                }

                LoginTokenDto loginResponseDto = jwtTokenProvider.createToken(userId, roles);

                // 8. RefreshToken Redis 업데이트
                refreshTokenRepository.deleteAll(refreshTokens);

                tempRefreshTokenRepository.deleteAll(tempRefreshTokenHashes);

                TempRefreshTokenHash tempRefreshTokenHash = TempRefreshTokenHash.builder()
                        .userId(userId)
                        .oldRefreshToken(reissueTokenDto.getRefreshToken())
                        .newRefreshToken(loginResponseDto.getRefreshToken())
                        .newAccessToken(loginResponseDto.getAccessToken())
                        .build();

                tempRefreshTokenRepository.save(tempRefreshTokenHash);
                System.out.println("첫번째 발급 토큰: loginResponseDto.accessToken = " + loginResponseDto.getAccessToken());
                System.out.println("첫번째 발급 토큰: loginResponseDto.refreshToken = " + loginResponseDto.getRefreshToken());
                return loginResponseDto;
            }
            // reissue 요청이 온 적이 있는 경우
            else {
                System.out.println("기존 발급 토큰: matchingHash.accessToken = " + matchingHash.get().getNewAccessToken());
                System.out.println("기존 발급 토큰: matchingHash.refreshToken = " + matchingHash.get().getNewRefreshToken());
                return LoginTokenDto.builder()
                        .refreshToken(matchingHash.get().getNewRefreshToken())
                        .accessToken(matchingHash.get().getNewAccessToken())
                        .build();
            }
        } finally {
            userLock.unlock();
        }
    }

    @Override
    public void logout(TokenDto tokenDto) {
        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateRefreshToken(tokenDto.getRefreshToken())) {
            return;
        }
        // 2. Access Token 에서 UserId 를 가져오기.
        String userId = jwtTokenProvider.getUserPk(tokenDto.getAccessToken());

        // 3. Redis 에서 해당 UserId로 저장된 Refresh Token 이 있는지 여부를 확인 후 존재할 경우 삭제.
        // TODO: 다른 기기에서 로그인 한 유저들을 구분하기 위해서 특정 토큰만 받아와서 삭제하기
        List<RefreshToken> refreshTokenHashes = refreshTokenRepository.findAllByUserId(BigInteger.valueOf(Integer.parseInt(userId)));
        if (refreshTokenHashes != null) {
            refreshTokenRepository.deleteAll(refreshTokenHashes);
        }

        // 4. 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        Long accessTokenExpiredIn = jwtTokenProvider.getExpiredIn(tokenDto.getAccessToken());
        blackListTokenRepository.save(BlackListTokenHash.builder()
                .expiredIn(accessTokenExpiredIn)
                .accessToken(tokenDto.getAccessToken())
                .build());
    }

    @Override
    @Transactional
    public FindIdResponseDto findUserEmail(FindIdRequestDto findIdRequestDto) {
        // 휴대폰 인증을 했는지 체크
        verifyUtil.isAuthenticated(findIdRequestDto.phone, RequiredAuth.FIND_ID);

        // 유저 가져오기
        User user = qUserRepository.findOneByPhone(findIdRequestDto.getPhone()).orElseThrow(() -> new ApiException(ExceptionEnum.USER_NOT_FOUND));

        // 아이디 찾기 응답 Response 생성
        List<ProviderEmailDto> connectedSns = new ArrayList<>();
        for (ProviderEmail providerEmail : user.getProviderEmails()) {
            ProviderEmailDto providerEmailDto = ProviderEmailDto.builder().provider(providerEmail.getProvider().getProvider()).email(providerEmail.getEmail()).build();
            connectedSns.add(providerEmailDto);
        }
        return FindIdResponseDto.builder().connectedSns(connectedSns).email(user.getEmail()).recentLoginDateTime(DateUtils.toISO(user.getRecentLoginDateTime())).build();
    }

    @Override
    public void checkUser(FindPasswordUserCheckRequestDto findPasswordUserCheckRequestDto) {
        User user = userRepository.findOneByNameAndEmail(findPasswordUserCheckRequestDto.getName(), findPasswordUserCheckRequestDto.getEmail()).orElseThrow(() -> new ApiException(ExceptionEnum.USER_NOT_FOUND));
        if (user.getUserStatus().equals(UserStatus.REQUEST_WITHDRAWAL)) {
            LocalDateTime updatedDateTime = user.getUpdatedDateTime().toLocalDateTime().plusDays(8);
            Duration duration = Duration.between(LocalDateTime.now(), updatedDateTime);
            throw new CustomException(HttpStatus.BAD_REQUEST, "CE400010", "탈퇴한 계정입니다. 계정을 복구하시겠습니까? 복구 가능 남은 기간 " + duration.toDays() + "일");
        }
    }

    @Override
    @Transactional
    public void findPasswordEmail(FindPasswordEmailRequestDto findPasswordEmailRequestDto) {
        // 비밀번호 일치/조건 체크
        String password = findPasswordEmailRequestDto.getPassword();
        UserValidator.isPasswordMatched(password, findPasswordEmailRequestDto.getPasswordCheck());
        userValidator.isValidPassword(password);

        // 인증을 진행한 유저인지 체크
        verifyUtil.isAuthenticated(findPasswordEmailRequestDto.getEmail(), RequiredAuth.FIND_PASSWORD);

        // 유저 정보 가져오기
        User user = qUserRepository.findOneByEmail(findPasswordEmailRequestDto.getEmail()).orElseThrow(() -> new ApiException(ExceptionEnum.USER_NOT_FOUND));
        // 비밀번호 변경
        String hashedPassword = passwordEncoder.encode(password);
        user.changePassword(hashedPassword);
    }

    @Override
    @Transactional
    public void findPasswordPhone(FindPasswordPhoneRequestDto findPasswordPhoneRequestDto) {
        // 인증을 진행한 유저인지 체크
        verifyUtil.isAuthenticated(findPasswordPhoneRequestDto.getPhone(), RequiredAuth.FIND_PASSWORD);
        // 비밀번호 일치/조건 체크
        String password = findPasswordPhoneRequestDto.getPassword();
        UserValidator.isPasswordMatched(password, findPasswordPhoneRequestDto.getPasswordCheck());
        userValidator.isValidPassword(password);
        // 유저 정보 가져오기
        User user = userRepository.findOneByPhone(findPasswordPhoneRequestDto.getPhone()).orElseThrow(() -> new ApiException(ExceptionEnum.USER_NOT_FOUND));
        // 비밀번호 변경
        String hashedPassword = passwordEncoder.encode(password);
        user.changePassword(hashedPassword);
    }


}
