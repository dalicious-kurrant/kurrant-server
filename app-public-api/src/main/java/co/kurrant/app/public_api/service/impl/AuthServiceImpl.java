package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.external.sms.dto.SmsMessageRequestDto;
import co.dalicious.system.util.GenerateRandomNumber;
import exception.ApiException;
import exception.ExceptionEnum;
import co.dalicious.client.core.filter.provider.JwtTokenProvider;
import co.dalicious.client.external.mail.EmailService;
import co.dalicious.client.external.mail.MailMessageDto;
import co.dalicious.client.external.sms.dto.SmsResponseDto;
import co.dalicious.client.external.sms.NaverSmsServiceImpl;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.repository.ProviderEmailRepository;
import co.kurrant.app.public_api.dto.user.LoginRequestDto;
import co.kurrant.app.public_api.dto.user.LoginResponseDto;
import co.kurrant.app.public_api.dto.user.SignUpRequestDto;
import co.kurrant.app.public_api.service.AuthService;
import co.kurrant.app.public_api.service.impl.mapper.UserMapper;
import co.kurrant.app.public_api.validator.UserValidator;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.domain.user.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
    public void mailConfirm(MailMessageDto mailMessageDto) throws Exception {
        // 기존에 가입된 사용자인지 확인
        Provider provider = Provider.GENERAL;
        String mail = mailMessageDto.getReceivers().get(0);
        userValidator.isEmailValid(provider, mail);

        // 인증번호 발송
        emailService.sendSimpleMessage(mailMessageDto.getReceivers());
    }

    // Sms 인증
    @Override
    public void sendSms(SmsMessageRequestDto smsMessageRequestDto) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        // 기존에 등록된 휴대폰 번호인지 확인
        userValidator.isPhoneValid(smsMessageRequestDto.getTo());

        // 인증번호 발송
        String key = GenerateRandomNumber.create8DigitKey();
        String content = "[커런트] 인증번호 [" + key + "]를 입력해주세요";
        SmsResponseDto smsResponseDto = smsService.sendSms(smsMessageRequestDto, content);
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

        // 비밀번호 일치 체크
        String password = signUpRequestDto.getPassword();
        userValidator.isPasswordMatched(password, signUpRequestDto.getPasswordCheck());

        // Hashed Password 생성
        String hashedPassword = passwordEncoder.encode(password);

        // 기존에 회원가입을 한 이력이 없는 유저라면 -> 유저 생성
        if(user == null) {
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
    public LoginResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> {
            return new UsernameNotFoundException("");
        });

         if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
         throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
         }

        List<String> arr = new ArrayList<String>();
        arr.add(user.getRole().getAuthority());
        String accessToken = jwtTokenProvider.createToken(user.getId().toString(), arr);

        return LoginResponseDto.builder().accessToken(accessToken).expiresIn(86400).build();
    }

}
