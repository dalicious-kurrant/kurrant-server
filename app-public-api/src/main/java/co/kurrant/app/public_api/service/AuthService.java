package co.kurrant.app.public_api.service;

import co.dalicious.client.core.exception.ApiException;
import co.dalicious.client.core.exception.ExceptionEnum;
import co.dalicious.client.core.filter.provider.JwtTokenProvider;
import co.dalicious.client.external.mail.EmailService;
import co.dalicious.client.external.mail.MailMessageDto;
import co.dalicious.client.external.sms.SmsMessageDto;
import co.dalicious.client.external.sms.SmsResponseDto;
import co.dalicious.client.external.sms.SmsService;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.repository.ApartmentRepository;
import co.dalicious.domain.user.repository.CorporationRepository;
import co.dalicious.domain.user.repository.ProviderEmailRepository;
import co.kurrant.app.public_api.dto.user.LoginRequestDto;
import co.kurrant.app.public_api.dto.user.LoginResponseDto;
import co.kurrant.app.public_api.dto.user.SignUpRequestDto;
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
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CorporationRepository corporationRepository;
    private final ApartmentRepository apartmentRepository;
    private final ProviderEmailRepository providerEmailRepository;
    private final UserValidator userValidator;

    private final EmailService emailService;
    private final SmsService smsService;
//    private static final SecureRandom random = new SecureRandom();
//    private static final int SALT_LENGTH = 64;

    // 이메일 인증
    public void mailConfirm(MailMessageDto mailMessageDto) throws Exception {
        // 기존에 가입된 사용자인지 확인
        Provider provider = Provider.GENERAL;
        String mail = mailMessageDto.getReceivers().get(0);
        userValidator.isEmailValid(provider, mail);

        // 인증번호 발송
        emailService.sendSimpleMessage(mailMessageDto.getReceivers());
    }

    // Sms 인증
    public void sendSms(SmsMessageDto smsMessageDto) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        // 기존에 등록된 휴대폰 번호인지 확인
        userValidator.isPhoneValid(smsMessageDto.getTo());

        // 인증번호 발송
        SmsResponseDto smsResponseDto = smsService.sendSms(smsMessageDto);
    }

    // 회원가입
    public User signUp(SignUpRequestDto signUpRequestDto) {
        // 기존에 가입된 사용자인지 확인
        String mail = signUpRequestDto.getEmail();
        User user = userValidator.getExistingUser(mail);

        // 기존에 일반 로그인으로 가입한 사용자인지 확인
        Provider provider = Provider.GENERAL;
        userValidator.isEmailValid(provider, mail);

        // 비밀번호 일치 체크
        String password = signUpRequestDto.getPassword();
        UserValidator.isPasswordMatched(password, signUpRequestDto.getPasswordCheck());

        // Hashed Password 생성
        String hashedPassword = passwordEncoder.encode(password);

        // 1. 기존에 회원가입을 한 이력이 없는 유저라면 -> 유저 생성
        if(user == null) {
            // N/A인 Corporation 과 Apartment 가져오기
            Corporation corporation = corporationRepository.findByName("N/A").orElseThrow(
                    () -> new ApiException(ExceptionEnum.NOT_FOUND)
            );

            Apartment apartment = apartmentRepository.findByName("N/A").orElseThrow(
                    () -> new ApiException(ExceptionEnum.NOT_FOUND)
            );

            UserDto userDto = UserDto.builder()
                    .email(signUpRequestDto.getEmail())
                    .phone(signUpRequestDto.getPhone())
                    .password(hashedPassword)
                    .name(signUpRequestDto.getName())
                    .role(Role.USER)
                    .apartment(apartment)
                    .corporation(corporation)
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

    public LoginResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> {
            return new UsernameNotFoundException("");
        });

         if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
         throw new ApiException(ExceptionEnum.PASSWORD_DOES_NOT_MATCH);
         }

        List<String> arr = new ArrayList<String>();
        String accessToken = jwtTokenProvider.createToken(user.getId().toString(), arr);
        // user.getUserRoles().stream().map(userRoles -> userRoles.getRole())
        // .map(role -> role.getName()).collect(Collectors.toList()));

        return LoginResponseDto.builder().accessToken(accessToken).expiresIn(86400).build();
    }

//    static byte[] createSalt() {
//        byte[] salt = new byte[SALT_LENGTH];
//        random.nextBytes(salt);
//        return salt;
//    }
}
