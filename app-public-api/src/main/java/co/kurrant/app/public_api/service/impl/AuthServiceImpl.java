package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.external.sms.dto.SmsMessageRequestDto;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.GenerateRandomNumber;
import co.dalicious.system.util.RequiredAuth;
import co.kurrant.app.public_api.dto.user.*;
import exception.ApiException;
import exception.ExceptionEnum;
import co.dalicious.client.core.filter.provider.JwtTokenProvider;
import co.dalicious.client.external.mail.EmailService;
import co.dalicious.client.external.mail.MailMessageDto;
import co.dalicious.client.external.sms.dto.SmsResponseDto;
import co.dalicious.client.external.sms.NaverSmsServiceImpl;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.repository.ProviderEmailRepository;
import co.kurrant.app.public_api.service.AuthService;
import co.kurrant.app.public_api.service.impl.mapper.UserMapper;
import co.kurrant.app.public_api.validator.UserValidator;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.domain.user.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ProviderEmailRepository providerEmailRepository;
    private final UserValidator userValidator;

    private final EmailService emailService;
    private final NaverSmsServiceImpl smsService;

    // 이메일 인증
    @Override
    public void mailConfirm(MailMessageDto mailMessageDto, String type) throws Exception {
        // 인증을 요청하는 위치 파악하기
        RequiredAuth requiredAuth = RequiredAuth.ofId(type);
        switch (requiredAuth) {
            case SIGNUP :
                // 기존에 가입된 사용자인지 확인
                Provider provider = Provider.GENERAL;
                String mail = mailMessageDto.getReceivers().get(0);
                userValidator.isEmailValid(provider, mail);

            case FIND_PASSWORD:
                // 존재하는 유저인지 확인
                User user = userRepository.findByEmail(mailMessageDto.getReceivers().get(0)).orElseThrow(
                        () -> new ApiException(ExceptionEnum.USER_NOT_FOUND)
                );
        }
        // 인증번호 발송
        emailService.sendSimpleMessage(mailMessageDto.getReceivers());
    }

    // Sms 인증
    @Override
    public void sendSms(SmsMessageRequestDto smsMessageRequestDto, String type) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        // 인증을 요청하는 위치 파악하기
        RequiredAuth requiredAuth = RequiredAuth.ofId(type);
        switch (requiredAuth) {
            case SIGNUP:
                // 기존에 등록된 휴대폰 번호인지 확인
                userValidator.isPhoneValid(smsMessageRequestDto.getTo());
            case FIND_ID, FIND_PASSWORD:
                // 유저가 존재하는지 확인
                User user = userRepository.findByPhone(smsMessageRequestDto.getTo()).orElseThrow(
                        () -> new ApiException(ExceptionEnum.USER_NOT_FOUND)
                );
        }

        // 인증번호 발송
        String key = GenerateRandomNumber.create8DigitKey();
        String content = "[커런트] 인증번호 [" + key + "]를 입력해주세요";
        SmsResponseDto smsResponseDto = smsService.sendSms(smsMessageRequestDto, content, key);
    }

    // 회원가입
    @Override
    public User signUp(SignUpRequestDto signUpRequestDto) {
        // 기존에 가입된 사용자인지 확인
        String mail = signUpRequestDto.getEmail();
        User user = userValidator.getExistingUser(mail);

        // 기존에 일반 로그인으로 가입한 사용자인지 확인
        Provider provider = Provider.GENERAL;
        userValidator.isEmailValid(provider, mail);

        // 비밀번호 일치/조건 체크
        String password = signUpRequestDto.getPassword();
        userValidator.isPasswordMatched(password, signUpRequestDto.getPasswordCheck());
        userValidator.isValidPassword(password);

        // 인증을 진행한 유저인지 체크
        emailService.isAuthenticatedEmail(signUpRequestDto.getEmail(), RequiredAuth.SIGNUP);

        // Hashed Password 생성
        String hashedPassword = passwordEncoder.encode(password);

        // 기존에 회원가입을 한 이력이 없는 유저라면 -> 유저 생성
        if (user == null) {
            UserDto userDto = UserDto.builder()
                    .email(signUpRequestDto.getEmail())
                    .phone(signUpRequestDto.getPhone())
                    .password(hashedPassword)
                    .name(signUpRequestDto.getName())
                    .role(Role.USER)
                    .build();

            // Corporation과 Apartment가 null로 대입되는 오류 발생 -> nullable = true 설정
            user = UserMapper.INSTANCE.toEntity(userDto);

            //User 저장
            user = userRepository.save(user);
        }

        ProviderEmail providerEmail = ProviderEmail.builder()
                .email(mail)
                .provider(Provider.GENERAL)
                .user(user)
                .build();
        providerEmailRepository.save(providerEmail);
        return user;
    }

    // 로그인
    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> {
            return new ApiException(ExceptionEnum.USER_NOT_FOUND);
        });

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
        }

        // 토큰에 권한 넣기
        List<String> arr = new ArrayList<String>();
        arr.add(user.getRole().getAuthority());
        String accessToken = jwtTokenProvider.createToken(user.getId().toString(), arr);

        // 로그인 날짜 업데이트
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        user.updateRecentLoginDateTime(timestamp);

        return LoginResponseDto.builder().accessToken(accessToken).expiresIn(86400).build();
    }

    @Override
    @Transactional
    public FindIdResponseDto findUserEmail(FindIdRequestDto findIdRequestDto) {
        // 휴대폰 인증을 했는지 체크
        smsService.isAuthenticatedPhone(findIdRequestDto.phone, RequiredAuth.FIND_ID);

        // 유저 가져오기
        User user = userRepository.findByPhone(findIdRequestDto.getPhone()).orElseThrow(
                () -> new ApiException(ExceptionEnum.USER_NOT_FOUND)
        );

        // 아이디 찾기 응답 Response 생성
        List<String> connectedSns = new ArrayList<>();
        for(ProviderEmail providerEmail : user.getProviderEmails()) {
            connectedSns.add(providerEmail.getProvider().getProvider());
        }
        return FindIdResponseDto.builder()
                .connectedSns(connectedSns)
                .email(user.getEmail())
                .recentLoginDateTime(DateUtils.toISO(user.getRecentLoginDateTime()))
                .build();
    }

    @Override
    public void checkUser(FindPasswordUserCheckRequestDto findPasswordUserCheckRequestDto) {
        userRepository.findByNameAndEmail(findPasswordUserCheckRequestDto.getName(), findPasswordUserCheckRequestDto.getEmail()).orElseThrow(
                () -> new ApiException(ExceptionEnum.USER_NOT_FOUND)
        );
    }

    @Override
    public void findPasswordEmail(FindPasswordEmailRequestDto findPasswordEmailRequestDto) {
        // 비밀번호 일치/조건 체크
        String password = findPasswordEmailRequestDto.getPassword();
        userValidator.isPasswordMatched(password, findPasswordEmailRequestDto.getPasswordCheck());
        userValidator.isValidPassword(password);

        // 인증을 진행한 유저인지 체크
        emailService.isAuthenticatedEmail(findPasswordEmailRequestDto.getEmail(), RequiredAuth.FIND_PASSWORD);

        // 유저 정보 가져오기
        User user = userRepository.findByEmail(findPasswordEmailRequestDto.getEmail()).orElseThrow(
                () -> new ApiException(ExceptionEnum.USER_NOT_FOUND)
        );
        // 비밀번호 변경
        String hashedPassword = passwordEncoder.encode(password);
        user.changePassword(hashedPassword);
    }

    @Override
    public void findPasswordPhone(FindPasswordPhoneRequestDto findPasswordPhoneRequestDto) {
        // 인증을 진행한 유저인지 체크
        smsService.isAuthenticatedPhone(findPasswordPhoneRequestDto.getPhone(), RequiredAuth.FIND_PASSWORD);
        // 비밀번호 일치/조건 체크
        String password = findPasswordPhoneRequestDto.getPassword();
        userValidator.isPasswordMatched(password, findPasswordPhoneRequestDto.getPasswordCheck());
        userValidator.isValidPassword(password);
        // 유저 정보 가져오기
        User user = userRepository.findByPhone(findPasswordPhoneRequestDto.getPhone()).orElseThrow(
                () -> new ApiException(ExceptionEnum.USER_NOT_FOUND)
        );
        // 비밀번호 변경
        String hashedPassword = passwordEncoder.encode(password);
        user.changePassword(hashedPassword);
    }

}
